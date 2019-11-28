package com.miro.widget.api.model.response;

import com.miro.widget.api.model.entity.Page;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import static com.miro.widget.api.model.response.WidgetLink.*;

public class WidgetPagedResources extends PagedResources<WidgetResponse> {

    public WidgetPagedResources(Page<WidgetResponse> page, Link... links) {
        super(page.getItems(), new PageMetadata(page.getItemCount(), page.getNumber(), page.getTotalItems(), page.getAvailableCount()), links);
    }

    @Override
    public void add(Link link) {
        super.add(new WidgetLink(link));
    }

    public static <T> WidgetPagedResources withLinks(
            Page<WidgetResponse> page,
            ControllerLinkBuilder methodGetOne,
            ControllerLinkBuilder methodGetAll,
            ControllerLinkBuilder methodCreate
    ) {
        page.getItems().forEach(item ->
                item.add(widgetLink(methodGetOne, true)));

        Link selfLink = widgetsLink(methodGetAll, true);
        WidgetPagedResources widgetPagedResources = new WidgetPagedResources(
                page,
                selfLinkWithParam(selfLink, page),
                createWidgetLink(methodCreate)
        );

        if (page.getNumber() > 1) {
            widgetPagedResources.add(
                    prevLinkWithParam(selfLink, page));
        }

        if (page.getNumber() < page.getAvailableCount()) {
            widgetPagedResources.add(
                    nextLinkWithParam(selfLink, page));
        }
        return widgetPagedResources;
    }

    private static Link selfLinkWithParam(Link self, Page page) {
        UriComponents prevUri = UriComponentsBuilder
                .fromHttpUrl(self.getHref())
                .queryParam("page", page.getNumber())
                .queryParam("size", page.getSize())
                .build();

        Link prevLink = self
                .withHref(prevUri.toString())
                .withRel(Link.REL_SELF)
                .withType(HttpMethod.GET.name());

        return new WidgetLink(prevLink);
    }

    public static Link prevLinkWithParam(Link self, Page page) {
        UriComponents prevUri = UriComponentsBuilder
                .fromHttpUrl(self.getHref())
                .queryParam("page", page.getNumber() - 1)
                .queryParam("size", page.getSize())
                .build();

        Link prevLink = self
                .withHref(prevUri.toString())
                .withRel(Link.REL_PREVIOUS)
                .withType(HttpMethod.GET.name());

        return new WidgetLink(prevLink);
    }

    public static Link nextLinkWithParam(Link self, Page page) {
        UriComponents prevUri = UriComponentsBuilder
                .fromHttpUrl(self.getHref())
                .queryParam("page", page.getNumber() + 1)
                .queryParam("size", page.getSize())
                .build();

        Link nextLink = self
                .withHref(prevUri.toString())
                .withRel(Link.REL_NEXT)
                .withType(HttpMethod.GET.name());

        return new WidgetLink(nextLink);
    }
}
