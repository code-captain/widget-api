package com.miro.widget.api.controller;

import com.miro.widget.api.contract.WidgetService;
import com.miro.widget.api.model.dto.PageableDto;
import com.miro.widget.api.model.dto.WidgetDto;
import com.miro.widget.api.model.entity.Filter;
import com.miro.widget.api.model.entity.Page;
import com.miro.widget.api.model.request.Pageable;
import com.miro.widget.api.model.request.WidgetRequest;
import com.miro.widget.api.model.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.core.DummyInvocationUtils.methodOn;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping(path = "api/widgets")
@RequiredArgsConstructor
public class WidgetController {
    private final WidgetService service;

    @GetMapping
    public ResponseEntity<WidgetPagedResources> getAll(
            @Valid Pageable pageable, @Valid Filter filter
    ) {
        assertPageableIsValid(pageable);
        assertFilterIsValid(filter);

        Page<WidgetDto> page = service.findPage(convertFromPageable(pageable), filter);
        Page<WidgetResponse> responsePage = Page.createPageBy(
                page,
                page.getItems().stream()
                        .map(WidgetController::convertToResponse)
                        .collect(Collectors.toList())
        );
        WidgetPagedResources widgetPagedResources = WidgetPagedResources.withLinks(
                responsePage,
                linkToGetOne(null),
                linkToGetAll(),
                linkToCreate()
        );
        return ResponseEntity.ok(widgetPagedResources);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<WidgetResource> getOne(
            @PathVariable UUID id
    ) {
        WidgetDto widget = service.findById(id);
        if (widget == null) {
            return ResponseEntity.notFound().build();
        }
        WidgetResponse widgetResponse = convertToResponse(widget);
        WidgetResource widgetResourceWithLink = WidgetResource.withLinks(
                widgetResponse,
                linkToGetOne(widgetResponse.getUuid()),
                linkToGetAll(),
                linkToUpdate(widgetResponse.getUuid()),
                linkToDelete(widgetResponse.getUuid())
        );
        return ResponseEntity.ok(widgetResourceWithLink);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WidgetResource> create(
            @Valid @RequestBody WidgetRequest request
    ) {
        WidgetDto saved = service.save(convertFromRequest(request));
        WidgetResponse widgetResponse = convertToResponse(saved);
        WidgetResource widgetResourceWithLink = WidgetResource.withLinks(
                widgetResponse,
                linkToGetOne(widgetResponse.getUuid()),
                linkToGetAll(),
                linkToUpdate(widgetResponse.getUuid()),
                linkToDelete(widgetResponse.getUuid())
        );
        return new ResponseEntity<>(widgetResourceWithLink, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WidgetResource> update(
            @PathVariable UUID id,
            @Valid @RequestBody WidgetRequest request
    ) {
        WidgetDto current = service.findById(id);
        if (current == null) {
            return ResponseEntity.notFound().build();
        }

        WidgetDto updated = service.update(id, convertFromRequest(request));
        WidgetResponse widgetResponse = convertToResponse(updated);
        WidgetResource widgetResourceWithLink = WidgetResource.withLinks(
                widgetResponse,
                linkToGetOne(id),
                linkToGetAll(),
                linkToUpdate(id),
                linkToDelete(id)
        );
        return ResponseEntity.ok(widgetResourceWithLink);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<WidgetResource> delete(
            @PathVariable UUID id
    ) {
        WidgetDto current = service.findById(id);
        if (current == null) {
            return ResponseEntity.notFound().build();
        }

        WidgetDto deleted = service.delete(id);
        WidgetResponse widgetResponse = convertToResponse(deleted);
        WidgetResource widgetResourceWithLink = WidgetResource.withLink(widgetResponse, linkToGetAll());
        return ResponseEntity.ok(widgetResourceWithLink);
    }

    private ControllerLinkBuilder linkToGetOne(UUID id) {
        return linkTo(methodOn(getClass()).getOne(id));
    }

    private ControllerLinkBuilder linkToGetAll() {
        return linkTo(methodOn(getClass()).getAll(null, null));
    }

    private ControllerLinkBuilder linkToCreate() {
        return linkTo(methodOn(getClass()).create(null));
    }

    private ControllerLinkBuilder linkToUpdate(UUID id) {
        return linkTo(methodOn(getClass()).update(id, null));
    }

    private ControllerLinkBuilder linkToDelete(UUID id) {
        return linkTo(methodOn(getClass()).delete(id));
    }

    private static WidgetResponse convertToResponse(WidgetDto dto) {
        return new WidgetResponse(
                dto.getId(),
                dto.getXCoordinate(),
                dto.getYCoordinate(),
                dto.getZIndex(),
                dto.getWidth(),
                dto.getHeight(),
                dto.getModifiedAt()
        );
    }

    private static WidgetDto convertFromRequest(WidgetRequest request) {
        return new WidgetDto(
                null,
                request.getXCoordinate(),
                request.getYCoordinate(),
                request.getZIndex(),
                request.getWidth(),
                request.getHeight(),
                null
        );
    }

    private static PageableDto convertFromPageable(Pageable pageable) {
        return new PageableDto(
                pageable.getPage(),
                pageable.getSize()
        );
    }

    private static void assertPageableIsValid(Pageable pageable) {
        if (pageable == null) {
            throw new NullPointerException("Param `pageableDto` must be not null");
        }
        if (pageable.getPage() <= 0) {
            throw new IllegalArgumentException("Field `page` must be greater than 0");
        }
        if (pageable.getSize() <= 0) {
            throw new IllegalArgumentException("Field `size` must be greater than 0");
        }

    }

    private static void assertFilterIsValid(Filter filter) {
        if (filter == null) {
            throw new NullPointerException("Param `filter` must be not null");
        }

        if ((filter.getBottomLeftX() != null
            || filter.getBottomLeftY() != null
            || filter.getUpperRightX() != null
            || filter.getUpperRightY() != null
        ) && !filter.isFilled()) {
            throw new IllegalArgumentException("All filter fields must be completed simultaneously");
        }
    }
}
