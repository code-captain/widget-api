package com.miro.widget.api.repository;

import com.miro.widget.api.contract.WidgetRepository;
import com.miro.widget.api.model.entity.Widget;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryWidgetRepository implements WidgetRepository {
    private static final Comparator<Widget> DEFAULT_COMPARATOR = Comparator.comparing(Widget::getZIndex);

    private Map<UUID, Widget> widgetMapById = new HashMap<>();
    private NavigableMap<Long, Widget> widgetMapByZIndex = new TreeMap<>();

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
    public NavigableSet<Widget> findAllSortByZIndexGreaterThanOrEqualTo(long index) {
        return widgetMapByZIndex.tailMap(index, true).values().stream()
                .collect(Collectors.toCollection(() -> new TreeSet<>(DEFAULT_COMPARATOR)));
    }

    @Override
    public void saveOrUpdate(Widget widget) {
        widgetMapById.put(widget.getId(), widget);
        widgetMapByZIndex.put(widget.getZIndex(), widget);
    }

    @Override
    public void saveOrUpdate(Collection<Widget> widgets) {
        widgets.forEach(element -> {
            widgetMapById.put(element.getId(), element);
            widgetMapByZIndex.put(element.getZIndex(), element);
        });
    }

    @Override
    public Widget remove(Widget widget) {
        widgetMapById.remove(widget.getId());
        return widgetMapByZIndex.remove(widget.getZIndex());
    }

    @Override
    public void removeAll() {
        widgetMapById.clear();
        widgetMapByZIndex.clear();
    }
}
