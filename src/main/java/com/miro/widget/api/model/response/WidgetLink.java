package com.miro.widget.api.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpMethod;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class WidgetLink extends Link {
    @JsonProperty
    private String type;

    public WidgetLink(Link link) {
        super(link.getHref(), link.getRel());

        this.type = link.getType();
    }

    public static <T> Link widgetLink(ControllerLinkBuilder builder, boolean isSelf) {
        Link link = builder
                .withRel(isSelf
                        ? Link.REL_SELF
                        : WidgetLinkRelType.WIDGET.getTitle())
                .withType(HttpMethod.GET.name());
        return new WidgetLink(link);
    }

    public static <T> Link widgetsLink(ControllerLinkBuilder builder, boolean isSelf) {
        Link link = builder
                .withRel(isSelf
                        ? Link.REL_SELF
                        : WidgetLinkRelType.WIDGETS.getTitle())
                .withType(HttpMethod.GET.name());
        return new WidgetLink(link);
    }

    public static <T> Link createWidgetLink(ControllerLinkBuilder builder) {
        Link link = builder
                .withRel(WidgetLinkRelType.CREATE_WIDGET.getTitle())
                .withType(HttpMethod.POST.name());
        return new WidgetLink(link);
    }

    public static <T> Link updateWidgetLink(ControllerLinkBuilder builder) {
        Link link = builder
                .withRel(WidgetLinkRelType.UPDATE_WIDGET.getTitle())
                .withType(HttpMethod.PUT.name());
        return new WidgetLink(link);
    }

    public static <T> Link deleteWidgetLink(ControllerLinkBuilder builder) {
        Link link = builder
                .withRel(WidgetLinkRelType.DELETE_WIDGET.getTitle())
                .withType(HttpMethod.DELETE.name());
        return new WidgetLink(link);
    }
}