package com.miro.widget.api.model.response;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import java.util.Arrays;

import static com.miro.widget.api.model.response.WidgetLink.*;

public class WidgetResource extends Resource<WidgetResponse> {

    public WidgetResource(WidgetResponse content, Link... links) {
        super(content, Arrays.asList(links));
    }

    @Override
    public void add(Link link) {
        super.add(new WidgetLink(link));
    }

    public static <T> WidgetResource withLink(
            WidgetResponse response,
            ControllerLinkBuilder methodGetAll
    ) {
        return new WidgetResource(
                response,
                widgetsLink(methodGetAll, false)
        );
    }

    public static <T> WidgetResource withLinks(
            WidgetResponse response,
            ControllerLinkBuilder methodGetOne,
            ControllerLinkBuilder methodGetAll,
            ControllerLinkBuilder methodUpdate,
            ControllerLinkBuilder methodDelete
    ) {
        return new WidgetResource(
                response,
                widgetLink(methodGetOne, true),
                updateWidgetLink(methodUpdate),
                deleteWidgetLink(methodDelete),
                widgetsLink(methodGetAll, false)
        );
    }
}