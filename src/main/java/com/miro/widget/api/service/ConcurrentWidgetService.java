package com.miro.widget.api.service;

import com.miro.widget.api.contract.WidgetService;
import com.miro.widget.api.model.dto.WidgetDto;
import com.miro.widget.api.model.entity.Widget;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.StampedLock;
import java.util.stream.Collectors;

@Service
public class ConcurrentWidgetService implements WidgetService {
    private Map<UUID, Widget> widgetMapById = new ConcurrentHashMap<>();
    private NavigableMap<Long, Widget> widgetMapByZIndex = new ConcurrentSkipListMap<>();

    private final StampedLock sl = new StampedLock();

    @Override
    public WidgetDto getById(UUID uuid) {
        long stamp = sl.tryOptimisticRead();
        WidgetDto widget = WidgetDto.fromEntity(widgetMapById.get(uuid));
        if (!sl.validate(stamp)) {
            stamp = sl.readLock();
            try {
                widget = WidgetDto.fromEntity(widgetMapById.get(uuid));
            } finally {
                sl.unlockRead(stamp);
            }
        }
        return widget;
    }

    @Override
    public Set<WidgetDto> getAll() {
        long stamp = sl.tryOptimisticRead();
        Set<WidgetDto> result = widgetMapByZIndex.values().stream()
                .map(WidgetDto::fromEntity)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (!sl.validate(stamp)) {
            stamp = sl.readLock();
            try {
                result = widgetMapByZIndex.values().stream()
                        .map(WidgetDto::fromEntity)
                        .collect(Collectors.toCollection(LinkedHashSet::new));
            } finally {
                sl.unlockRead(stamp);
            }
        }
        return result;
    }

    @Override
    public WidgetDto save(WidgetDto dto) {
        Widget newest = Widget.fromDto(dto);
        long stamp = sl.readLock();
        try {
            while (true) {
                Set<Widget> shiftedElementsCopies = new HashSet<>();
                if (newest.getZIndex() == null) {
                    if (widgetMapById.size() == 0) {
                        newest.setZIndex(0L);
                    } else {
                        Long lastKey = widgetMapByZIndex.lastKey();
                        newest.setZIndex(lastKey + 1);
                    }
                } else {
                    shiftedElementsCopies = getElementsCopiesShiftFrom(newest.getZIndex());
                }
                long ws = sl.tryConvertToWriteLock(stamp);
                if (ws != 0L) {
                    stamp = ws;

                    newest.setId(UUID.randomUUID());
                    newest.setModifiedAt(Date.from(Instant.now()));
                    widgetMapById.put(newest.getId(), newest);
                    widgetMapByZIndex.put(newest.getZIndex(), newest);
                    shiftedElementsCopies.forEach(element -> {
                        widgetMapById.put(element.getId(), element);
                        widgetMapByZIndex.put(element.getZIndex(), element);
                    });

                    return WidgetDto.fromEntity(newest);
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
        Widget newest = Widget.fromDto(dto);
        long stamp = sl.readLock();
        try {
            while (true) {
                Widget oldest = widgetMapById.get(uuid);
                if (oldest == null) {
                    throw new NoSuchElementException();
                }

                Set<Widget> shiftedElementsCopies = new HashSet<>();
                if (!oldest.getZIndex()
                        .equals(newest.getZIndex())) {

                    shiftedElementsCopies = getElementsCopiesShiftFrom(newest.getZIndex(), oldest.getZIndex());
                }

                long ws = sl.tryConvertToWriteLock(stamp);
                if (ws != 0L) {
                    stamp = ws;

                    newest.setId(oldest.getId());
                    newest.setModifiedAt(Date.from(Instant.now()));
                    widgetMapById.remove(oldest.getId());
                    widgetMapByZIndex.remove(oldest.getZIndex());
                    widgetMapById.put(newest.getId(), newest);
                    widgetMapByZIndex.put(newest.getZIndex(), newest);
                    shiftedElementsCopies.forEach(element -> {
                        widgetMapById.put(element.getId(), element);
                        widgetMapByZIndex.put(element.getZIndex(), element);
                    });

                    return WidgetDto.fromEntity(newest);
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
                Widget oldest = widgetMapById.get(uuid);
                if (oldest == null) {
                    throw new NoSuchElementException();
                }

                long ws = sl.tryConvertToWriteLock(stamp);
                if (ws != 0L) {
                    stamp = ws;

                    widgetMapById.remove(oldest.getId());
                    return WidgetDto.fromEntity(
                            widgetMapByZIndex.remove(oldest.getZIndex())
                    );
                } else {
                    sl.unlockRead(stamp);
                    stamp = sl.writeLock();
                }
            }
        } finally {
            sl.unlock(stamp);
        }
    }

    private Set<Widget> getElementsCopiesShiftFrom(Long zIndex) {
        return getElementsCopiesShiftFrom(zIndex, null);
    }

    private Set<Widget> getElementsCopiesShiftFrom(Long zIndex, Long excludeIndex) {
        Long ceilingZIndex = widgetMapByZIndex.ceilingKey(zIndex);
        if (ceilingZIndex == null
                || isDistanceGreaterThanZero(ceilingZIndex, zIndex)) {

            return Collections.emptySet();
        }

        Set<Widget> result = new LinkedHashSet<>();
        NavigableMap<Long, Widget> tailElementsMap = widgetMapByZIndex.tailMap(ceilingZIndex, true);
        for (Map.Entry<Long, Widget> e
                : tailElementsMap.entrySet()) {

            Long prevKey = tailElementsMap.lowerKey(e.getKey());
            if ((prevKey != null
                    && isDistanceGreaterThanOne(e.getKey(), prevKey)
            ) || e.getKey().equals(excludeIndex)) {

                break;
            }

            Widget copy = Widget.copy(e.getValue());
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
}