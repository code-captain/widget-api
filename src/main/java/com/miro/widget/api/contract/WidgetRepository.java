package com.miro.widget.api.contract;

import com.miro.widget.api.model.entity.Widget;

import java.util.Collection;
import java.util.NavigableMap;
import java.util.Set;
import java.util.UUID;

public interface WidgetRepository {
    long count();

    Long findHighestZIndex();

    Long findLeastZIndexGreaterThanOrEqualTo(long index);

    Widget findById(UUID uuid);

    Set<Widget> findAllSortByZIndex();

    Set<Widget> findAllSortByZIndex(long skip, long take);

    NavigableMap<Long, Widget> findAllWithZIndexGreaterThanOrEqualTo(long index);

    void saveOrUpdate(Widget widget);

    void saveOrUpdate(Collection<Widget> widget);

    Widget remove(Widget widget);
}
