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
    //private NavigableMap<Long, Set<Widget>> widgetMapByBottomLeftXCoordinate = new TreeMap<>();
    private NavigableMap<Long, NavigableMap<Long, NavigableMap<Long, NavigableMap<Long, Set<Widget>>>>> widgetMapByCoordinates = new TreeMap<>();

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
        Set<Widget> filteredWidgets = searchByRectangleMap(filterRectangle);
        return filteredWidgets.stream()
                .skip(skip)
                .limit(take)
                .collect(Collectors.toCollection(() -> new TreeSet<>(DEFAULT_COMPARATOR)));
    }

    private Set<Widget> searchByRectangleMap(Rectangle rectangleFilter) {
        TreeSet<Widget> result = new TreeSet<>(DEFAULT_COMPARATOR);
        widgetMapByCoordinates.subMap(
                rectangleFilter.getBottomLeftPoint().getXCoordinate(), true,
                rectangleFilter.getUpperRightPoint().getXCoordinate(), true
        ).forEach((bottomXCoordinate, widgetMultiMapByBottomYCoordinate) -> {
            widgetMultiMapByBottomYCoordinate.subMap(
                    rectangleFilter.getBottomLeftPoint().getYCoordinate(), true,
                    rectangleFilter.getUpperRightPoint().getYCoordinate(), true
            ).forEach((bottomYCoordinate, widgetMultiMapByUpperXCoordinate) ->
                    widgetMultiMapByUpperXCoordinate.subMap(
                            rectangleFilter.getBottomLeftPoint().getXCoordinate(), true,
                            rectangleFilter.getUpperRightPoint().getXCoordinate(), true
                    ).forEach((upperXCoordinate, widgetMultiMapByUpperYCoordinate) -> {
                        widgetMultiMapByUpperYCoordinate.subMap(
                                rectangleFilter.getBottomLeftPoint().getYCoordinate(), true,
                                rectangleFilter.getUpperRightPoint().getYCoordinate(), true
                        ).forEach((k , v) -> result.addAll(v));
                    }));
        });
        return result;
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
            removeInRectangleMap(removed);
        }
        addToRectangleMap(widget);
    }

    private void addToRectangleMap(Widget widget) {
        widgetMapByCoordinates
                .putIfAbsent(widget.getBottomLeftPoint().getXCoordinate(), new TreeMap<>());
        widgetMapByCoordinates
                .get(widget.getBottomLeftPoint().getXCoordinate())
                .putIfAbsent(widget.getBottomLeftPoint().getYCoordinate(), new TreeMap<>());
        widgetMapByCoordinates
                .get(widget.getBottomLeftPoint().getXCoordinate())
                .get(widget.getBottomLeftPoint().getYCoordinate())
                .putIfAbsent(widget.getUpperRightPoint().getXCoordinate(), new TreeMap<>());
        widgetMapByCoordinates
                .get(widget.getBottomLeftPoint().getXCoordinate())
                .get(widget.getBottomLeftPoint().getYCoordinate())
                .get(widget.getUpperRightPoint().getXCoordinate())
                .putIfAbsent(widget.getUpperRightPoint().getYCoordinate(), new TreeSet<>(DEFAULT_COMPARATOR));

        widgetMapByCoordinates
                .get(widget.getBottomLeftPoint().getXCoordinate())
                .get(widget.getBottomLeftPoint().getYCoordinate())
                .get(widget.getUpperRightPoint().getXCoordinate())
                .get(widget.getUpperRightPoint().getYCoordinate())
                .add(widget);
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
            removeInRectangleMap(removed);
        }
        return removed;
    }

    private void removeInRectangleMap(Widget widget) {
        widgetMapByCoordinates
                .getOrDefault(widget.getBottomLeftPoint().getXCoordinate(), new TreeMap<>())
                .getOrDefault(widget.getBottomLeftPoint().getYCoordinate(), new TreeMap<>())
                .getOrDefault(widget.getUpperRightPoint().getXCoordinate(), new TreeMap<>())
                .getOrDefault(widget.getUpperRightPoint().getYCoordinate(), new TreeSet<>(DEFAULT_COMPARATOR))
                .remove(widget);
    }

    @Override
    public void removeAll() {
        widgetMapById.clear();
        widgetMapByZIndex.clear();
        widgetMapByCoordinates.clear();
    }
}
