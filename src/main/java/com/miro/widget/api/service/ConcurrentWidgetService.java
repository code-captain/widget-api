package com.miro.widget.api.service;

import com.miro.widget.api.contract.WidgetRepository;
import com.miro.widget.api.contract.WidgetService;
import com.miro.widget.api.model.dto.PageableDto;
import com.miro.widget.api.model.dto.WidgetDto;
import com.miro.widget.api.model.entity.Page;
import com.miro.widget.api.model.entity.Widget;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.locks.StampedLock;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ConcurrentWidgetService implements WidgetService {
    @NonNull
    private final WidgetRepository repository;
    private final StampedLock sl = new StampedLock();

    @Override
    public WidgetDto findById(UUID uuid) {
        Widget widget = repository.findById(uuid);
        if (widget == null) {
            return null;
        }
        return fromEntity(widget);
    }

    @Override
    public Page<WidgetDto> findPage(PageableDto meta) {
        if (meta.getPage() <= 0) {
            throw new IllegalArgumentException("Field `page` must be greater than 0");
        }
        if (meta.getSize() <= 0) {
            throw new IllegalArgumentException("Field `size` must be greater than 0");
        }

        long itemsToSkip = (meta.getPage() - 1) * meta.getSize();

        long stamp = sl.tryOptimisticRead();
        long count = repository.count();
        if (count <= itemsToSkip) {
            return Page.createEmptyPage(meta, count);
        }
        List<WidgetDto> widgets = findAllSortByZIndex(itemsToSkip, meta.getSize());
        if (!sl.validate(stamp)) {
            stamp = sl.readLock();
            try {
                count = repository.count();
                if (count <= itemsToSkip) {
                    return Page.createEmptyPage(meta, count);
                }
                widgets = findAllSortByZIndex(itemsToSkip, meta.getSize());

            } finally {
                sl.unlockRead(stamp);
            }
        }
        return Page.createPage(widgets, meta, count);
    }

    @Override
    public List<WidgetDto> findAll() {
        long stamp = sl.tryOptimisticRead();

        List<WidgetDto> result = findAllSortByZIndex();
        if (!sl.validate(stamp)) {
            stamp = sl.readLock();
            try {
                result = findAllSortByZIndex();
            } finally {
                sl.unlockRead(stamp);
            }
        }
        return result;
    }

    @Override
    public WidgetDto save(WidgetDto dto) {
        Widget newest = fromDto(dto);

        long stamp = sl.readLock();
        try {
            while (true) {
                Set<Widget> shiftedElementsCopies = new HashSet<>();
                if (newest.getZIndex() == null) {
                    if (repository.count() == 0) {
                        newest.setZIndex(0L);
                    } else {
                        newest.setZIndex(repository.findHighestZIndex() + 1);
                    }
                } else {
                    shiftedElementsCopies = createCopiesWithShiftZIndexAt(newest.getZIndex());
                }
                long ws = sl.tryConvertToWriteLock(stamp);
                if (ws != 0L) {
                    stamp = ws;

                    newest.setId(UUID.randomUUID());
                    newest.setModifiedAt(Date.from(Instant.now()));

                    repository.saveOrUpdate(newest);
                    repository.saveOrUpdate(shiftedElementsCopies);

                    return fromEntity(newest);
                } else {
                    sl.unlockRead(stamp);
                    stamp = sl.writeLock();
                }
            }
        } finally {
            sl.unlock(stamp);
        }
    }

    @Override
    public WidgetDto update(UUID uuid, WidgetDto dto) {
        if (dto.getZIndex() == null) {
            throw new IllegalArgumentException("Field `zIndex` must be not null");
        }
        Widget newest = fromDto(dto);
        long stamp = sl.readLock();
        try {
            while (true) {
                Widget oldest = repository.findById(uuid);
                if (oldest == null) {
                    throw new NoSuchElementException();
                }

                Set<Widget> shiftedElementsCopies = new HashSet<>();
                if (!oldest.getZIndex()
                        .equals(newest.getZIndex())) {

                    shiftedElementsCopies = createCopiesWithShiftZIndexAt(newest.getZIndex(), oldest.getZIndex());
                }

                long ws = sl.tryConvertToWriteLock(stamp);
                if (ws != 0L) {
                    stamp = ws;

                    newest.setId(oldest.getId());
                    newest.setModifiedAt(Date.from(Instant.now()));

                    repository.remove(oldest);
                    repository.saveOrUpdate(newest);
                    repository.saveOrUpdate(shiftedElementsCopies);

                    return fromEntity(newest);
                } else {
                    sl.unlockRead(stamp);
                    stamp = sl.writeLock();
                }
            }
        } finally {
            sl.unlock(stamp);
        }
    }

    @Override
    public WidgetDto delete(UUID uuid) {
        long stamp = sl.readLock();
        try {
            while (true) {
                Widget oldest = repository.findById(uuid);
                if (oldest == null) {
                    throw new NoSuchElementException();
                }

                long ws = sl.tryConvertToWriteLock(stamp);
                if (ws != 0L) {
                    stamp = ws;

                    return fromEntity(repository.remove(oldest));
                } else {
                    sl.unlockRead(stamp);
                    stamp = sl.writeLock();
                }
            }
        } finally {
            sl.unlock(stamp);
        }
    }

    @Override
    public void deleteAll() {
        repository.removeAll();
    }

    private List<WidgetDto> findAllSortByZIndex(long skip, long take) {
        return repository.findAllSortByZIndex(skip,  take).stream()
                .map(ConcurrentWidgetService::fromEntity)
                .collect(toList());
    }

    private List<WidgetDto> findAllSortByZIndex() {
        return repository.findAllSortByZIndex().stream()
                .map(ConcurrentWidgetService::fromEntity)
                .collect(toList());
    }

    private Set<Widget> createCopiesWithShiftZIndexAt(Long zIndex) {
        return createCopiesWithShiftZIndexAt(zIndex, null);
    }

    private Set<Widget> createCopiesWithShiftZIndexAt(Long zIndex, Long excludeIndex) {
        Long ceilingZIndex = repository.findLeastZIndexGreaterThanOrEqualTo(zIndex);
        if (ceilingZIndex == null
                || isDistanceGreaterThanZero(ceilingZIndex, zIndex)) {

            return Collections.emptySet();
        }

        Set<Widget> result = new LinkedHashSet<>();
        NavigableMap<Long, Widget> tailElementsMap = repository.findAllWithZIndexGreaterThanOrEqualTo(ceilingZIndex);
        for (Map.Entry<Long, Widget> e
                : tailElementsMap.entrySet()) {

            Long prevKey = tailElementsMap.lowerKey(e.getKey());
            if ((prevKey != null
                    && isDistanceGreaterThanOne(e.getKey(), prevKey)
            ) || e.getKey().equals(excludeIndex)) {

                break;
            }

            Widget copy = copy(e.getValue());
            copy.setZIndex(copy.getZIndex() + 1);
            copy.setModifiedAt(Date.from(Instant.now()));
            result.add(copy);
        }

        return result;
    }

    private static boolean isDistanceGreaterThanOne(long f, long s) {
        return getDistance(f, s) > 1;
    }

    private static boolean isDistanceGreaterThanZero(long f, long s) {
        return getDistance(f, s) > 0;
    }

    private static long getDistance(long f, long s) {
        return Math.abs(f - s);
    }

    private static Widget fromDto(WidgetDto dto) {
        Widget widget = new Widget();
        widget.setXCoordinate(dto.getXCoordinate());
        widget.setYCoordinate(dto.getYCoordinate());
        widget.setZIndex(dto.getZIndex());
        widget.setWidth(dto.getWidth());
        widget.setHeight(dto.getHeight());
        return widget;
    }

    private static WidgetDto fromEntity(Widget entity) {
        return new WidgetDto(
                entity.getId(),
                entity.getXCoordinate(),
                entity.getYCoordinate(),
                entity.getZIndex(),
                entity.getWidth(),
                entity.getHeight(),
                entity.getModifiedAt()
        );
    }

    private static Widget copy(Widget self) {
        return new Widget(
                self.getId(),
                self.getXCoordinate(),
                self.getYCoordinate(),
                self.getZIndex(),
                self.getWidth(),
                self.getHeight(),
                self.getModifiedAt()
        );
    }
}