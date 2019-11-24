package com.miro.widget.api.controller;

import com.miro.widget.api.contract.WidgetService;
import com.miro.widget.api.model.dto.PageableDto;
import com.miro.widget.api.model.dto.WidgetDto;
import com.miro.widget.api.model.entity.Page;
import com.miro.widget.api.model.request.Pageable;
import com.miro.widget.api.model.request.WidgetRequest;
import com.miro.widget.api.model.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
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
    public ResponseEntity<WidgetPagedResources<WidgetResponse>> getAll(
            @Valid Pageable pageable
    ) {
        Page<WidgetDto> page = service.findPage(fromPageable(pageable));
        List<WidgetResponse> widgetResponses = page.getItems().stream()
                .map(widget -> {
                    WidgetResponse widgetResponse = toResponse(widget);
                    widgetResponse.add(
                            generateWidgetLink(widget.getId(), true));
                    return widgetResponse;
                }).collect(Collectors.toList());

        Link selfLink = generateWidgetsLink(true);
        WidgetPagedResources<WidgetResponse> result = new WidgetPagedResourcesBuilder<>(widgetResponses, page)
                .withSelfLink(selfLink)
                .withPrevLink(selfLink)
                .withNextLink(selfLink)
                .withLink(generateCreateLink())
                .build();
        return ResponseEntity.ok(result);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Resource<WidgetResponse>> getOne(
            @PathVariable UUID id
    ) {
        WidgetDto widget = service.findById(id);
        if (widget == null) {
            return ResponseEntity.notFound().build();
        }
        Resource<WidgetResponse> result = new WidgetResource<>(
                toResponse(widget),
                generateWidgetResourceLinks(widget.getId())
        );
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Resource<WidgetResponse>> create(
            @Valid @RequestBody WidgetRequest request
    ) {
        WidgetDto saved = service.save(fromRequest(request));
        Resource<WidgetResponse> result = new WidgetResource<>(
                toResponse(saved),
                generateWidgetResourceLinks(saved.getId())
        );
        return ResponseEntity.ok(result);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Resource<WidgetResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody WidgetRequest request
    ) {
        WidgetDto current = service.findById(id);
        if (current == null) {
            return ResponseEntity.notFound().build();
        }
        WidgetDto updated = service.update(id, fromRequest(request));
        Resource<WidgetResponse> result = new WidgetResource<>(
                toResponse(updated),
                generateWidgetResourceLinks(updated.getId())
        );
        return ResponseEntity.ok(result);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity delete(
            @PathVariable UUID id
    ) {
        WidgetDto current = service.findById(id);
        if (current == null) {
            return ResponseEntity.notFound().build();
        }
        WidgetDto deleted = service.delete(id);
        Resource<WidgetResponse> result = new WidgetResource<>(
                toResponse(deleted),
                generateWidgetsLink(false)
        );
        return ResponseEntity.ok(result);
    }

    private Link[] generateWidgetResourceLinks(UUID id) {
        return new Link[] {
            generateWidgetLink(id, true),
            generateUpdateLink(id),
            generateDeleteLink(id),
            generateWidgetsLink(false)
        };
    }

    private Link generateWidgetLink(UUID id, boolean isSelf) {
        return linkTo(methodOn(getClass()).getOne(id))
                .withRel(isSelf
                        ? Link.REL_SELF
                        : WidgetLinkRelType.WIDGET.getTitle())
                .withType(HttpMethod.GET.name());
    }

    private Link generateWidgetsLink(boolean isSelf) {
        return linkTo(methodOn(getClass()).getAll(null))
                .withRel(isSelf
                        ? Link.REL_SELF
                        : WidgetLinkRelType.WIDGETS.getTitle())
                .withType(HttpMethod.GET.name());
    }

    private Link generateCreateLink() {
        return linkTo(methodOn(getClass()).create(null))
                .withRel(WidgetLinkRelType.CREATE.getTitle())
                .withType(HttpMethod.POST.name());
    }

    private Link generateUpdateLink(UUID id) {
        return linkTo(methodOn(getClass()).update(id, null))
                .withRel(WidgetLinkRelType.UPDATE.getTitle())
                .withType(HttpMethod.PUT.name());
    }

    private Link generateDeleteLink(UUID id) {
        return linkTo(methodOn(getClass()).delete(id))
                .withRel(WidgetLinkRelType.DELETE.getTitle())
                .withType(HttpMethod.DELETE.name());
    }

    private static WidgetResponse toResponse(WidgetDto dto) {
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

    private static WidgetDto fromRequest(WidgetRequest request) {
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

    private static PageableDto fromPageable(Pageable pageable) {
        return new PageableDto(
                pageable.getPage(),
                pageable.getSize()
        );
    }
}
