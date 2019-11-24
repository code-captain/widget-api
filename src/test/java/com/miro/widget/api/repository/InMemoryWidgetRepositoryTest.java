package com.miro.widget.api.repository;

import com.miro.widget.api.contract.WidgetRepository;
import com.miro.widget.api.model.entity.Widget;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.rules.ExpectedException;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
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
    public void findHighestZIndex_WhenWidgetsWereNotSaved_ThrowNoSuchElementException() {
        expectedException.expect(IsInstanceOf.instanceOf(NoSuchElementException.class));
        repository.findHighestZIndex();
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
    public void findAllWithZIndexGreaterThanOrEqualTo_WhenWidgetsWereNotSaved_ReturnEmptyNavigableMap() {
        assertEquals(Collections.emptyNavigableMap(), repository.findAllWithZIndexGreaterThanOrEqualTo(1));
    }

    @Test
    public void findAllWithZIndexGreaterThanOrEqualTo_WithFromKeyParamWhenWidgetsWereSaved_ReturnNavigableMap() {
        Set<Widget> test = createWidgets(4);
        repository.saveOrUpdate(test);

        NavigableMap<Long, Widget> testable = repository.findAllWithZIndexGreaterThanOrEqualTo(2);
        List<Widget> target = test.stream().skip(2).limit(2).collect(Collectors.toList());
        assertEquals(createWidgetByZIndexMap(target), testable);
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

    private static NavigableMap<Long, Widget> createWidgetByZIndexMap(Collection<Widget> widgets) {
        NavigableMap<Long, Widget> map = new TreeMap<>();
        widgets.forEach(widget -> map.put(widget.getZIndex(), widget));
        return map;
    }

    private static Set<Widget> createWidgets(int limit) {
        return Stream
                .iterate(0L, i -> i + 1)
                .map(InMemoryWidgetRepositoryTest::createWidget)
                .limit(limit)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static Widget createWidget(Long zIndex) {
        return new Widget(
                UUID.randomUUID(),
                40,
                50,
                zIndex,
                100,
                50,
                Date.from(Instant.now())
        );
    }
}