package com.miro.widget.api.repository;

import com.miro.widget.api.contract.WidgetRepository;
import com.miro.widget.api.model.entity.Point;
import com.miro.widget.api.model.entity.Rectangle;
import com.miro.widget.api.model.entity.Widget;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryWidgetRepository implements WidgetRepository {
    private static final Comparator<Widget> DEFAULT_COMPARATOR =
            Comparator.comparing(Widget::getZIndex)
                .thenComparing(Widget::getId);

    private Map<UUID, Widget> widgetMapById = new HashMap<>();
    private NavigableMap<Long, Widget> widgetMapByZIndex = new TreeMap<>();
    private NavigableMap<Long, Set<Widget>> widgetMapByBottomLeftXCoordinate = new TreeMap<>();

    @Override
    public long count() {
        return widgetMapById.size();
    }

    @Override
    public Long findHighestZIndex() {
        return widgetMapByZIndex.size() == 0
                ? null
                : widgetMapByZIndex.lastKey();
    }

    @Override
    public Long findLeastZIndexGreaterThanOrEqualTo(long index) {
        return widgetMapByZIndex.ceilingKey(index);
    }

    @Override
    public Widget findById(UUID uuid) {
        return widgetMapById.get(uuid);
    }

    @Override
    public Set<Widget> findAllSortByZIndex() {
        return new LinkedHashSet<>(widgetMapByZIndex.values());
    }

    @Override
    public Set<Widget> findAllSortByZIndex(long skip, long take) {
        return widgetMapByZIndex.values().stream()
                .skip(skip)
                .limit(take)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Set<Widget> findAllInAreaSortByZIndex(Point bottomLeft, Point upperRight, long skip, long take) {
        Rectangle filterRectangle = new Rectangle(bottomLeft, upperRight);
        return widgetMapByBottomLeftXCoordinate.subMap(bottomLeft.getXCoordinate(), upperRight.getXCoordinate()).entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .filter(filterRectangle::contains)
                .skip(skip)
                .limit(take)
                .collect(Collectors.toCollection(() -> new TreeSet<>(DEFAULT_COMPARATOR)));
    }

    @Override
    public NavigableSet<Widget> findAllSortByZIndexGreaterThanOrEqualTo(long index) {
        return widgetMapByZIndex.tailMap(index, true).values().stream()
                .collect(Collectors.toCollection(() -> new TreeSet<>(DEFAULT_COMPARATOR)));
    }

    @Override
    public void saveOrUpdate(Widget widget) {
        Widget removed = widgetMapById.put(widget.getId(), widget);
        widgetMapByZIndex.put(widget.getZIndex(), widget);
        if (removed != null) {
            removeInMapByBottomLeftXCoordinate(removed);
        }
        widgetMapByBottomLeftXCoordinate.putIfAbsent(widget.getBottomLeftPoint().getXCoordinate(), new TreeSet<>(DEFAULT_COMPARATOR));
        widgetMapByBottomLeftXCoordinate.get(widget.getBottomLeftPoint().getXCoordinate()).add(widget);
    }

    @Override
    public void saveOrUpdate(Collection<Widget> widgets) {
        widgets.forEach(this::saveOrUpdate);
    }

    @Override
    public Widget remove(Widget widget) {
        Widget removed = widgetMapById.remove(widget.getId());
        widgetMapByZIndex.remove(widget.getZIndex());
        if (removed != null) {
            removeInMapByBottomLeftXCoordinate(removed);
        }
        return removed;
    }

    private void removeInMapByBottomLeftXCoordinate(Widget widget) {
        Set<Widget> widgets = widgetMapByBottomLeftXCoordinate.get(widget.getBottomLeftPoint().getXCoordinate());
        if (widgets != null) {
            widgets.remove(widget);
        }
    }

    @Override
    public void removeAll() {
        widgetMapById.clear();
        widgetMapByZIndex.clear();
        widgetMapByBottomLeftXCoordinate.clear();
    }
}
