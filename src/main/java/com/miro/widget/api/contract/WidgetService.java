package com.miro.widget.api.contract;

import com.miro.widget.api.model.dto.PageableDto;
import com.miro.widget.api.model.dto.WidgetDto;
import com.miro.widget.api.model.entity.Page;

import java.util.Collection;
import java.util.UUID;

public interface WidgetService {

    WidgetDto getById(UUID uuid);

    Collection<WidgetDto> getAll();

    Page<WidgetDto> getPage(PageableDto meta);

    WidgetDto save(WidgetDto dto);

    WidgetDto update(UUID uuid, WidgetDto dto);

    WidgetDto delete(UUID uuid);
}
