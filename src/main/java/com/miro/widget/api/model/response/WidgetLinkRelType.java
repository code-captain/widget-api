package com.miro.widget.api.model.response;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WidgetLinkRelType {
    WIDGET("widget"),

    WIDGETS("widgets"),

    CREATE_WIDGET("create-widget"),

    UPDATE_WIDGET("update-widget"),

    DELETE_WIDGET("delete-widget");

    @NonNull
    private final String title;
}
