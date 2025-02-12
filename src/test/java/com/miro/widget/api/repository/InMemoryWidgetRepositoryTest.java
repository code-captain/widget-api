package com.miro.widget.api.repository;

import com.miro.widget.api.contract.WidgetRepository;
import com.miro.widget.api.model.entity.Point;
import com.miro.widget.api.model.entity.Widget;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class InMemoryWidgetRepositoryTest {
    private WidgetRepository repository;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        repository = new InMemoryWidgetRepository();
    }

    @Test
    public void count_WhenWidgetsWereNotSaved_ReturnZero() {
        assertEquals(0, repository.count());
    }

    @Test
    public void count_WhenOneWidgetWasSaved_ReturnExpectedCount() {
        repository.saveOrUpdate(createWidget(1L));
        assertEquals(1, repository.count());
    }

    @Test
    public void count_WhenWidgetsWereSaved_ReturnExpectedCount() {
        repository.saveOrUpdate(createWidgets(2));
        assertEquals(2, repository.count());
    }

    @Test
    public void findHighestZIndex_WhenWidgetsWereNotSaved_ReturnNull() {
        Long highestZIndex = repository.findHighestZIndex();
        Assert.assertNull(highestZIndex);
    }

    @Test
    public void findHighestZIndex_WhenWidgetsWereSaved_ReturnHighestZIndex() {
        repository.saveOrUpdate(createWidgets(2));
        assertEquals(1L, repository.findHighestZIndex().longValue());
    }

    @Test
    public void findLeastZIndexGreaterThanOrEqualTo_WhenWidgetsWereNotSaved_ReturnNull() {
        assertNull(repository.findLeastZIndexGreaterThanOrEqualTo(0));
    }

    @Test
    public void findLeastZIndexGreaterThanOrEqualTo_WhenWidgetsWereSaved_ReturnLeastGreaterZIndex() {
        repository.saveOrUpdate(createWidgets(2));
        assertEquals(1, repository.findLeastZIndexGreaterThanOrEqualTo(1).longValue());
    }

    @Test
    public void findById_WhenWidgetWasNotSaved_ReturnNull() {
        assertNull(repository.findById(UUID.randomUUID()));
    }

    @Test
    public void findById_WhenWidgetWasSaved_ReturnWidget() {
        Set<Widget> twoWidgets = createWidgets(2);
        repository.saveOrUpdate(twoWidgets);

        Widget test = twoWidgets.iterator().next();
        Widget testable = repository.findById(test.getId());
        assertNotNull(testable);
        assertEquals(test.getId(), testable.getId());
        assertEquals(test.getXCoordinate(), testable.getXCoordinate());
        assertEquals(test.getYCoordinate(), testable.getYCoordinate());
        assertEquals(test.getZIndex(), testable.getZIndex());
        assertEquals(test.getWidth(), testable.getWidth());
        assertEquals(test.getHeight(), testable.getHeight());
        assertEquals(test.getModifiedAt(), testable.getModifiedAt());
    }

    @Test
    public void findAllSortByZIndex_WhenWidgetsWereNotSaved_ReturnEmptySortedSet() {
        assertEquals(Collections.emptySortedSet(), repository.findAllSortByZIndex());
    }

    @Test
    public void findAllSortByZIndex_WhenWidgetsWereSaved_ReturnSortedWidgets() {
        Set<Widget> test = createWidgets(2);
        repository.saveOrUpdate(test);

        Set<Widget> testable = repository.findAllSortByZIndex();
        assertEquals(test, testable);
    }

    @Test
    public void findAllSortByZIndex_WithRangeParamsWhenWidgetsWereSavedAndAllSkip_ReturnEmptySortedSet() {
        Set<Widget> test = createWidgets(2);
        repository.saveOrUpdate(test);

        assertEquals(Collections.emptySortedSet(), repository.findAllSortByZIndex(2, 2));
    }

    @Test
    public void findAllSortByZIndex_WithRangeParamsWhenWidgetsWereSaved_ReturnSortedWidgets() {
        Set<Widget> test = createWidgets(4);
        repository.saveOrUpdate(test);

        Set<Widget> testable = repository.findAllSortByZIndex(2, 2);
        assertEquals(test.stream().skip(2).limit(2).collect(Collectors.toSet()), testable);
    }

    @Test
    public void findAllInAreaSortByZIndex_WithRangeParamsWhenWidgetsWereSaved_ReturnSortedWidgetsInArea() {
        Set<Widget> test = createWidgets(4);
        repository.saveOrUpdate(test);

        Set<Widget> testable = repository.findAllInAreaSortByZIndex(createFilterBottomLeftPoint(), createFilterUpperRightPoint(), 2, 2);
        assertEquals(test.stream().skip(2).limit(2).collect(Collectors.toSet()), testable);
    }

    @Test
    public void findAllInAreaSortByZIndex_WhenOnlyOneWidgetIsNotMatch_ReturnWidgetInArea() {
        Set<Widget> test = createWidgets(4);
        Widget widget = createWidget(300, 200);
        repository.saveOrUpdate(widget);
        repository.saveOrUpdate(test);

        Set<Widget> testable = repository.findAllInAreaSortByZIndex(createFilterBottomLeftPoint(), createFilterUpperRightPoint(), 0, 5);
        assertEquals(test, testable);
    }

    @Test
    public void findAllWithZIndexGreaterThanOrEqualTo_WhenWidgetsWereNotSaved_ReturnEmptyNavigableMap() {
        assertEquals(Collections.emptyNavigableSet(), repository.findAllSortByZIndexGreaterThanOrEqualTo(1));
    }

    @Test
    public void findAllWithZIndexGreaterThanOrEqualTo_WithFromKeyParamWhenWidgetsWereSaved_ReturnNavigableMap() {
        Set<Widget> test = createWidgets(4);
        repository.saveOrUpdate(test);

        NavigableSet<Widget> testable = repository.findAllSortByZIndexGreaterThanOrEqualTo(2);
        Set<Widget> target = test.stream().skip(2).limit(2).collect(Collectors.toCollection(LinkedHashSet::new));
        assertEquals(target, testable);
    }

    @Test
    public void remove_WhenWidgetsWereNotSaved_ReturnNull() {
        assertNull(repository.remove(createWidget(1L)));
    }

    @Test
    public void remove_WhenWidgetsWereNotSaved_ReturnEmptySortedSet() {
        Set<Widget> testSet = createWidgets(4);
        repository.saveOrUpdate(testSet);

        Widget test = testSet.iterator().next();
        Widget removed = repository.remove(test);
        assertEquals(3, repository.count());
        assertEquals(test.getId(), removed.getId());
        assertEquals(test.getXCoordinate(), removed.getXCoordinate());
        assertEquals(test.getYCoordinate(), removed.getYCoordinate());
        assertEquals(test.getZIndex(), removed.getZIndex());
        assertEquals(test.getWidth(), removed.getWidth());
        assertEquals(test.getHeight(), removed.getHeight());
        assertEquals(test.getModifiedAt(), removed.getModifiedAt());
    }

    @Test
    public void findAllInAreaSortByZIndex_WhenParamInvokeWidgetReflexivity_ReturnExpectedWidget() {
        Widget widget1 = new Widget(UUID.randomUUID(), 150, 100, 1L, 300, 200);
        Widget widget2 = new Widget(UUID.randomUUID(), 150, 100, 1L, 300, 200);
        repository.saveOrUpdate(Collections.singleton(widget1));

        Set<Widget> widgets = repository.findAllInAreaSortByZIndex(widget2.getBottomLeftPoint(), widget2.getUpperRightPoint(), 0, 2);
        Assert.assertTrue(widgets.contains(widget1));
    }

    @Test
    public void findAllInAreaSortByZIndex_WhenAreaContainsTwoWidgets_ReturnExpectedWidgets() {
        Widget widget1 = new Widget(UUID.randomUUID(), 150, 100, 1L, 300, 200);
        Widget widget2 = new Widget(UUID.randomUUID(), 50, 50, 2L, 100, 100);
        Widget widget3 = new Widget(UUID.randomUUID(), 250, 150, 3L, 100, 100);
        repository.saveOrUpdate(Arrays.asList(widget2, widget3));

        Set<Widget> widgets = repository.findAllInAreaSortByZIndex(widget1.getBottomLeftPoint(), widget1.getUpperRightPoint(), 0, 2);
        Assert.assertTrue(widgets.contains(widget2));
        Assert.assertTrue(widgets.contains(widget3));

        widgets = repository.findAllInAreaSortByZIndex(widget2.getBottomLeftPoint(), widget2.getUpperRightPoint(), 0, 2);
        Assert.assertFalse(widgets.contains(widget1));
        Assert.assertFalse(widgets.contains(widget3));
    }

    private static Point createFilterBottomLeftPoint() {
        return new Point(0, 0);
    }

    private static Point createFilterUpperRightPoint() {
        return new Point(300, 200);
    }

    private static Set<Widget> createWidgets(int limit) {
        return Stream
                .iterate(0L, i -> i + 1)
                .map(InMemoryWidgetRepositoryTest::createWidget)
                .limit(limit)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static Widget createWidget(long xCoordinate, long yCoordinate) {
        return new Widget(
                UUID.randomUUID(),
                xCoordinate,
                yCoordinate,
                5L,
                100,
                100
        );
    }

    private static Widget createWidget(Long zIndex) {
        return new Widget(
                UUID.randomUUID(),
                50,
                50,
                zIndex,
                100,
                100
        );
    }
}