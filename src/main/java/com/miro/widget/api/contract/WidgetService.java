package com.miro.widget.api.contract;

import com.miro.widget.api.model.dto.WidgetDto;

import java.util.Collection;
import java.util.UUID;

public interface WidgetService {

    WidgetDto getById(UUID uuid);

    Collection<WidgetDto> getAll();

    WidgetDto save(WidgetDto dto);

    WidgetDto update(UUID uuid, WidgetDto dto);

    WidgetDto delete(UUID uuid);
}
