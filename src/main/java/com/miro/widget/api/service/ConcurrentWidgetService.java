package com.miro.widget.api.service;

import com.miro.widget.api.contract.WidgetRepository;
import com.miro.widget.api.contract.WidgetService;
import com.miro.widget.api.model.dto.PageableDto;
import com.miro.widget.api.model.dto.WidgetDto;
import com.miro.widget.api.model.entity.Filter;
import com.miro.widget.api.model.entity.Page;
import com.miro.widget.api.model.entity.Point;
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
    private final StampedLock lock = new StampedLock();

    @Override
    public WidgetDto findById(UUID uuid) {
        long stamp = lock.readLock();
        try {
            Widget widget = repository.findById(uuid);
            return widget != null
                    ? convertFromEntity(widget)
                    : null;
        } finally {
            lock.unlockRead(stamp);
        }
    }

    @Override
    public Page<WidgetDto> findPage(PageableDto meta, Filter filter) {
        assertPageableIsValid(meta);
        assertFilterIsValid(filter);

        long count;
        Set<Widget> widgets;
        long itemsToSkip = (meta.getPage() - 1) * meta.getSize();
        long stamp = lock.readLock();
        try {
            count = repository.count();
            if (filter.isFilled()) {
                Point bottomLeftPoint = new Point(filter.getBottomLeftX(), filter.getBottomLeftY());
                Point upperRightPoint = new Point(filter.getUpperRightX(), filter.getUpperRightY());
                widgets = repository.findAllInAreaSortByZIndex(bottomLeftPoint, upperRightPoint, itemsToSkip,  meta.getSize());
            } else {
                widgets = repository.findAllSortByZIndex(itemsToSkip,  meta.getSize());
            }
        } finally {
            lock.unlockRead(stamp);
        }

        if (count <= itemsToSkip) {
            return Page.createEmptyPage(meta, count);
        }
        List<WidgetDto> widgetDtoList = widgets.stream()
                .map(ConcurrentWidgetService::convertFromEntity)
                .collect(toList());

        return Page.createPage(widgetDtoList, meta, count);
    }

    @Override
    public List<WidgetDto> findAll() {
        Set<Widget> widgets;
        long stamp = lock.readLock();
        try {
            widgets = repository.findAllSortByZIndex();
        } finally {
            lock.unlockRead(stamp);
        }
        return widgets.stream()
                .map(ConcurrentWidgetService::convertFromEntity)
                .collect(toList());
    }

    @Override
    public WidgetDto save(WidgetDto dto) {
        long stamp = lock.writeLock();
        try {
            if (dto.getZIndex() == null) {
                Long highestZIndex = repository.findHighestZIndex();
                dto.setZIndex(highestZIndex != null
                        ? highestZIndex + 1
                        : 0);
            }

            if (isNeedToShiftTailWidgetsAt(dto.getZIndex())) {
                repository.saveOrUpdate(
                        getShiftedTailWidgetsAt(dto.getZIndex()));
            }
            dto.setId(UUID.randomUUID());
            dto.setModifiedAt(Date.from(Instant.now()));
            Widget newest = convertFromDto(dto);
            repository.saveOrUpdate(newest);

            return dto;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public WidgetDto update(UUID uuid, WidgetDto dto) {
        assertUpdatedWidgetIsValid(dto);

        long stamp = lock.writeLock();
        try {
            Widget oldest = repository.findById(uuid);
            assertWidgetWasFound(uuid, oldest);

            if (isNeedToShiftTailWidgetsAt(dto.getZIndex())) {
                repository.saveOrUpdate(
                        getShiftedTailWidgetsAt(dto.getZIndex(), oldest.getZIndex()));
            }
            dto.setId(uuid);
            dto.setModifiedAt(Date.from(Instant.now()));
            Widget newest = convertFromDto(dto);
            repository.remove(oldest);
            repository.saveOrUpdate(newest);

            return dto;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public WidgetDto delete(UUID uuid) {
        long stamp = lock.writeLock();
        try {
            Widget oldest = repository.findById(uuid);
            assertWidgetWasFound(uuid, oldest);

            Widget removed = repository.remove(oldest);
            return convertFromEntity(removed);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public void deleteAll() {
        long stamp = lock.writeLock();
        try {
            repository.removeAll();
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    private Set<Widget> getShiftedTailWidgetsAt(Long zIndex) {
        return getShiftedTailWidgetsAt(zIndex, null);
    }

    private Set<Widget> getShiftedTailWidgetsAt(Long zIndex, Long excludeIndex) {
        Set<Widget> result = new LinkedHashSet<>();

        NavigableSet<Widget> allWithZIndexGreaterThanOrEqualTo = repository.findAllSortByZIndexGreaterThanOrEqualTo(zIndex);
        for (Widget widget : allWithZIndexGreaterThanOrEqualTo) {

            Widget prevWidget = allWithZIndexGreaterThanOrEqualTo.lower(widget);
            if ((prevWidget != null
                    && isDistanceGreaterThanOne(widget.getZIndex(), prevWidget.getZIndex())
            ) || widget.getZIndex().equals(excludeIndex)) {

                break;
            }

            Widget copy = createCopy(widget);
            copy.incrementZIndex();
            result.add(copy);
        }

        return result;
    }

    private boolean isNeedToShiftTailWidgetsAt(Long newZIndex) {
        Long ceilingZIndex = repository.findLeastZIndexGreaterThanOrEqualTo(newZIndex);
        return ceilingZIndex != null
                && !isDistanceGreaterThanZero(ceilingZIndex, newZIndex);
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

    private static void assertWidgetWasFound(UUID uuid, Widget widget) {
        if (widget == null) {
            throw new NoSuchElementException(String.format("Widget with id `%s` was not found", uuid));
        }
    }

    private static void assertUpdatedWidgetIsValid(WidgetDto dto) {
        if (dto.getZIndex() == null) {
            throw new IllegalArgumentException("Field `zIndex` must be not null");
        }
    }

    private static void assertPageableIsValid(PageableDto dto) {
        if (dto == null) {
            throw new NullPointerException("Param `pageableDto` must be not null");
        }
        if (dto.getPage() <= 0) {
            throw new IllegalArgumentException("Field `page` must be greater than 0");
        }
        if (dto.getSize() <= 0) {
            throw new IllegalArgumentException("Field `size` must be greater than 0");
        }

    }

    private static void assertFilterIsValid(Filter filter) {
        if (filter == null) {
            throw new NullPointerException("Param `filter` must be not null");
        }

        if ((filter.getBottomLeftX() != null && filter.getUpperRightX() != null)
                && (filter.getBottomLeftX() >= filter.getUpperRightX())) {
            throw new IllegalArgumentException("Field `bottomLeftX` must be less than `upperRightX` in param `filter`");
        }
        if ((filter.getBottomLeftY() != null && filter.getUpperRightY() != null)
                && (filter.getBottomLeftY() >= filter.getUpperRightY())) {
            throw new IllegalArgumentException("Field `bottomLeftY` must be less than `upperRightY` in param `filter`");
        }
    }

    private static Widget convertFromDto(WidgetDto dto) {
        return new Widget(
                dto.getId(),
                dto.getXCoordinate(),
                dto.getYCoordinate(),
                dto.getZIndex(),
                dto.getHeight(),
                dto.getWidth()
        );
    }

    private static WidgetDto convertFromEntity(Widget entity) {
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

    private static Widget createCopy(Widget self) {
        return new Widget(
                self.getId(),
                self.getXCoordinate(),
                self.getYCoordinate(),
                self.getZIndex(),
                self.getWidth(),
                self.getHeight()
        );
    }
}