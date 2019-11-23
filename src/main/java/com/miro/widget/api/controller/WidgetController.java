package com.miro.widget.api.controller;

import com.miro.widget.api.contract.WidgetService;
import com.miro.widget.api.model.dto.WidgetDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping(path = "api/widgets")
@RequiredArgsConstructor
public class WidgetController {

    private final WidgetService service;

    @GetMapping(path = "/")
    public ResponseEntity<Collection<WidgetDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<WidgetDto> getOne(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity create(@RequestBody WidgetDto widget) {
        return ResponseEntity.ok(service.save(widget));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity update(@PathVariable UUID id, @RequestBody WidgetDto widget) {
        WidgetDto current = service.getById(id);
        if (current == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(service.save(widget));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity delete(@PathVariable UUID id) {
        WidgetDto current = service.getById(id);
        if (current == null) {
            return ResponseEntity.notFound().build();
        }
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
