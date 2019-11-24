package com.miro.widget.api.model.response;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WidgetLinkRelType {

    WIDGET("widget"),

    WIDGETS("widgets"),

    CREATE("create"),

    UPDATE("update"),

    DELETE("delete");

    @NonNull
    private final String title;
}
