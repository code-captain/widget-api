package com.miro.widget.api.service;

import com.miro.widget.api.contract.WidgetRepository;
import com.miro.widget.api.contract.WidgetService;
import com.miro.widget.api.model.dto.PageableDto;
import com.miro.widget.api.model.dto.WidgetDto;
import com.miro.widget.api.model.entity.Page;
import com.miro.widget.api.model.entity.Widget;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toCollection;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ConcurrentWidgetServiceTest {
    private WidgetRepository repository;
    private WidgetService service;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        repository = mock(WidgetRepository.class);
        service = new ConcurrentWidgetService(repository);
    }

    @Test
    public void findById_WhenWidgetIsExist_ReturnExpectedWidgetDto() {
        Widget test = createWidget();
        doReturn(test).when(repository).findById(eq(test.getId()));

        WidgetDto testable = service.findById(test.getId());
        assertEquals(test.getId(), testable.getId());
        assertEquals(test.getXCoordinate(), testable.getXCoordinate());
        assertEquals(test.getYCoordinate(), testable.getYCoordinate());
        assertEquals(test.getZIndex(), testable.getZIndex());
        assertEquals(test.getWidth(), testable.getWidth());
        assertEquals(test.getHeight(), testable.getHeight());
        assertEquals(test.getModifiedAt(), testable.getModifiedAt());
    }

    @Test
    public void findById_WhenWidgetIsNotExist_ReturnNull() {
        Widget test = createWidget();
        doReturn(test).when(repository).findById(eq(test.getId()));

        WidgetDto testable = service.findById(UUID.randomUUID());
        assertNull(testable);
    }

    @Test
    public void findPage_WhenParamPageableIsInvalid_ThrowIllegalArgumentException() {
        expectedException.expect(IsInstanceOf.instanceOf(IllegalArgumentException.class));
        service.findPage(createInvalidPageableDto());
    }

    @Test
    public void findPage_WhenRequestWidgetsPageIsNotExist_ReturnEmptyPage() {
        doReturn(0L).when(repository).count();

        PageableDto pageableDto = createPageableDto();
        Page<WidgetDto> page = service.findPage(pageableDto);

        assertEquals(Collections.emptyList(), page.getItems());
        assertEquals(pageableDto.getPage(), page.getNumber());
        assertEquals(pageableDto.getSize(), page.getSize());
        assertEquals(0, page.getTotalItems());
    }

    @Test
    public void findPage_WhenRequestWidgetsPageIsExist_ReturnExpectedPage() {
        doReturn(2L).when(repository).count();

        Set<Widget> widgets = createTwoWidgets();
        doReturn(widgets).when(repository).findAllSortByZIndex(anyLong(), anyLong());

        PageableDto pageableDto = createPageableDto();
        Page<WidgetDto> page = service.findPage(pageableDto);

        assertEquals(widgets.size(), page.getItems().size());
        Iterator<Widget> testIterator = widgets.iterator();
        Iterator<WidgetDto> testableIterator = page.getItems().iterator();
        while (testIterator.hasNext()
                && testableIterator.hasNext()
        ) {
            Widget test = testIterator.next();
            WidgetDto testable = testableIterator.next();

            assertEquals(test.getId(), testable.getId());
            assertEquals(test.getXCoordinate(), testable.getXCoordinate());
            assertEquals(test.getYCoordinate(), testable.getYCoordinate());
            assertEquals(test.getZIndex(), testable.getZIndex());
            assertEquals(test.getWidth(), testable.getWidth());
            assertEquals(test.getHeight(), testable.getHeight());
            assertEquals(test.getModifiedAt(), testable.getModifiedAt());
        }

        assertEquals(pageableDto.getPage(), page.getNumber());
        assertEquals(pageableDto.getSize(), page.getSize());
        assertEquals(2, page.getTotalItems());
    }


    @Test
    public void findAll_WhenWidgetsAreNotExists_ReturnEmptyCollection() {
        doReturn(emptySet()).when(repository).findAllSortByZIndex(anyLong(), anyLong());
        Collection<WidgetDto> all = service.findAll();

        Assert.assertEquals(0, all.size());
    }

    @Test
    public void findAll_WhenWidgetsAreExists_ReturnExpectedWidgets() {
        Set<Widget> widgets = createTwoWidgets();
        doReturn(widgets).when(repository).findAllSortByZIndex();

        Collection<WidgetDto> all = service.findAll();

        assertEquals(widgets.size(), all.size());
        Iterator<Widget> testIterator = widgets.iterator();
        Iterator<WidgetDto> testableIterator = all.iterator();
        while (testIterator.hasNext()
                && testableIterator.hasNext()
        ) {
            Widget test = testIterator.next();
            WidgetDto testable = testableIterator.next();

            assertEquals(test.getId(), testable.getId());
            assertEquals(test.getXCoordinate(), testable.getXCoordinate());
            assertEquals(test.getYCoordinate(), testable.getYCoordinate());
            assertEquals(test.getZIndex(), testable.getZIndex());
            assertEquals(test.getWidth(), testable.getWidth());
            assertEquals(test.getHeight(), testable.getHeight());
            assertEquals(test.getModifiedAt(), testable.getModifiedAt());
        }
    }

    @Test
    public void save_WhenWidgetsAreNotExistsAndZIndexIsNull_ReturnSavedWidgetWithZeroZIndex() {
        doReturn(null).when(repository).findHighestZIndex();
        doReturn(null).when(repository).findLeastZIndexGreaterThanOrEqualTo(anyLong());
        Widget test = createWidget(null);
        WidgetDto saved = service.save(fromEntity(test));

        verify(repository, times(1)).saveOrUpdate(any(Widget.class));
        verify(repository, never()).saveOrUpdate(anyCollection());

        Assert.assertNotNull(saved);
        assertNotEquals(test.getId(), saved.getId());
        assertEquals(test.getXCoordinate(), saved.getXCoordinate());
        assertEquals(test.getYCoordinate(), saved.getYCoordinate());
        assertEquals(0L, saved.getZIndex().longValue());
        assertEquals(test.getWidth(), saved.getWidth());
        assertEquals(test.getHeight(), saved.getHeight());
    }

    @Test
    public void save_WhenWidgetsAreExistsAndZIndexIsNull_ReturnSavedWidgetWithHighestZIndex() {
        doReturn(1L).when(repository).count();
        doReturn(1L).when(repository).findHighestZIndex();

        Widget test = createWidget(null);
        WidgetDto saved = service.save(fromEntity(test));

        verify(repository, times(1)).saveOrUpdate(any(Widget.class));
        verify(repository, never()).saveOrUpdate(anyCollection());

        Assert.assertNotNull(saved);
        assertNotEquals(test.getId(), saved.getId());
        assertEquals(test.getXCoordinate(), saved.getXCoordinate());
        assertEquals(test.getYCoordinate(), saved.getYCoordinate());
        assertEquals(2L, saved.getZIndex().longValue());
        assertEquals(test.getWidth(), saved.getWidth());
        assertEquals(test.getHeight(), saved.getHeight());
    }

    @Test
    public void save_WhenInputZIndexShiftExistedWidgets_ReturnSavedWidgetWithActualZIndex() {
        doReturn(2L).when(repository).count();
        doReturn(1L).when(repository).findHighestZIndex();
        doReturn(1L).when(repository).findLeastZIndexGreaterThanOrEqualTo(eq(1L));

        Widget test = createWidget(1L);
        doReturn(Stream.of(test).collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Widget::getZIndex)))))
                .when(repository).findAllSortByZIndexGreaterThanOrEqualTo(eq(1L));

        WidgetDto saved = service.save(fromEntity(test));

        verify(repository, times(1)).saveOrUpdate(any(Widget.class));
        verify(repository, times(1)).saveOrUpdate(anyCollection());

        Assert.assertNotNull(saved);
        assertNotEquals(test.getId(), saved.getId());
        assertEquals(test.getXCoordinate(), saved.getXCoordinate());
        assertEquals(test.getYCoordinate(), saved.getYCoordinate());
        assertEquals(1L, saved.getZIndex().longValue());
        assertEquals(test.getWidth(), saved.getWidth());
        assertEquals(test.getHeight(), saved.getHeight());
    }

    @Test
    public void update_WhenWidgetDtoIsInvalid_ThrowIllegalArgumentException() {
        expectedException.expect(IsInstanceOf.instanceOf(IllegalArgumentException.class));
        Widget test = createWidget(null);
        service.update(test.getId(), fromEntity(test));
    }

    @Test
    public void update_WhenWidgetIsNotExist_ThrowNoSuchElementException() {
        expectedException.expect(IsInstanceOf.instanceOf(NoSuchElementException.class));
        Widget test = createWidget(1L);
        doReturn(null).when(repository).findById(any());

        service.update(test.getId(), fromEntity(test));
    }

    @Test
    public void update_WhenWidgetsAreExist_ReturnUpdatedWidget() {
        doReturn(2L).when(repository).count();
        doReturn(1L).when(repository).findHighestZIndex();
        doReturn(1L).when(repository).findLeastZIndexGreaterThanOrEqualTo(eq(1L));

        Widget test = createWidget(2L);
        doReturn(test).when(repository).findById(eq(test.getId()));

        WidgetDto updated = fromEntity(createWidget(5L));
        updated.setXCoordinate(110);
        updated.setYCoordinate(120);
        WidgetDto testable = service.update(test.getId(), updated);

        verify(repository, times(1)).remove(any(Widget.class));
        verify(repository, times(1)).saveOrUpdate(any(Widget.class));
        verify(repository, never()).saveOrUpdate(anyCollection());

        Assert.assertNotNull(testable);
        assertEquals(test.getId(), testable.getId());
        assertEquals(updated.getXCoordinate(), testable.getXCoordinate());
        assertEquals(updated.getYCoordinate(), testable.getYCoordinate());
        assertEquals(updated.getZIndex(), testable.getZIndex());
        assertEquals(test.getWidth(), testable.getWidth());
        assertEquals(test.getHeight(), testable.getHeight());
    }

    @Test
    public void delete_WhenWidgetIsNotExist_ThrowNoSuchElementException() {
        expectedException.expect(IsInstanceOf.instanceOf(NoSuchElementException.class));
        doReturn(null).when(repository).findById(any());

        service.delete(UUID.randomUUID());
    }

    @Test
    public void delete_WhenWidgetIsExist_ReturnDeletedWidget() {
        Widget test = createWidget(2L);
        doReturn(test).when(repository).findById(eq(test.getId()));
        doReturn(test).when(repository).remove(any());

        WidgetDto deleted = service.delete(test.getId());
        verify(repository, times(1)).remove(any(Widget.class));

        Assert.assertNotNull(deleted);
        assertEquals(test.getId(), deleted.getId());
    }


    private static PageableDto createPageableDto() {
        return new PageableDto(1, 10);
    }

    private static PageableDto createInvalidPageableDto() {
        return new PageableDto(-1, -1);
    }

    private static Set<Widget> createTwoWidgets() {
        return Stream
                .iterate(0L, i -> i + 1)
                .map(ConcurrentWidgetServiceTest::createWidget)
                .limit(2)
                .collect(toCollection(LinkedHashSet::new));
    }

    private static Widget createWidget(Long zIndex) {
        return new Widget(
                UUID.randomUUID(),
                40,
                50,
                zIndex,
                100,
                50
        );
    }

    private static Widget createWidget() {
        return new Widget(
                UUID.randomUUID(),
                40,
                50,
                5L,
                100,
                50
        );
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
}