package com.miro.widget.api.contract;

import com.miro.widget.api.model.entity.Point;
import com.miro.widget.api.model.entity.Widget;

import java.util.*;

public interface WidgetRepository {
    long count();

    Long findHighestZIndex();

    Long findLeastZIndexGreaterThanOrEqualTo(long index);

    Widget findById(UUID uuid);

    Set<Widget> findAllSortByZIndex();

    Set<Widget> findAllSortByZIndex(long skip, long take);

    Set<Widget> findAllInAreaSortByZIndex(Point bottomLeft, Point upperRight, long skip, long take);

    NavigableSet<Widget> findAllSortByZIndexGreaterThanOrEqualTo(long index);

    void saveOrUpdate(Widget widget);

    void saveOrUpdate(Collection<Widget> widget);

    Widget remove(Widget widget);

    void removeAll();
}
