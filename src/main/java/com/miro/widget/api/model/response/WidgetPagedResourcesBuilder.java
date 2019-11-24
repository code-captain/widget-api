package com.miro.widget.api.model.response;

import com.miro.widget.api.model.entity.Page;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpMethod;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class WidgetPagedResourcesBuilder<T> {
    private Collection<T> iterable;

    private Page page;

    private Collection<Link> links;

    public WidgetPagedResourcesBuilder(
            Collection<T> iterable,
            Page page,
            Link... links
    ) {
        this.iterable = iterable;
        this.page = page;
        this.links = new ArrayList<>(Arrays.asList(links));
    }

    public WidgetPagedResourcesBuilder<T> withSelfLink(Link self) {
        UriComponents prevUri = UriComponentsBuilder
                .fromHttpUrl(self.getHref())
                .queryParam("page", this.page.getNumber())
                .queryParam("page", this.page.getSize())
                .build();

        Link prevLink = self
                .withHref(prevUri.toString())
                .withRel(Link.REL_SELF)
                .withType(HttpMethod.GET.name());

        return withLink(prevLink);
    }

    public WidgetPagedResourcesBuilder<T> withPrevLink(Link self) {
        long pageNumber = this.page.getNumber();
        if (pageNumber == 1) {
           return this;
        }

        UriComponents prevUri = UriComponentsBuilder
                .fromHttpUrl(self.getHref())
                .queryParam("page", pageNumber - 1)
                .queryParam("page", this.page.getSize())
                .build();

        Link prevLink = self
                .withHref(prevUri.toString())
                .withRel(Link.REL_PREVIOUS)
                .withType(HttpMethod.GET.name());

        return withLink(prevLink);
    }

    public WidgetPagedResourcesBuilder<T> withNextLink(Link self) {
        long pageNumber = this.page.getNumber();
        if (pageNumber >= this.page.getAvailableCount()) {
            return this;
        }

        UriComponents prevUri = UriComponentsBuilder
                .fromHttpUrl(self.getHref())
                .queryParam("page", pageNumber + 1)
                .queryParam("page", this.page.getSize())
                .build();

        Link prevLink = self
                .withHref(prevUri.toString())
                .withRel(Link.REL_NEXT)
                .withType(HttpMethod.GET.name());

        return withLink(prevLink);
    }

    public WidgetPagedResourcesBuilder<T> withLink(Link newest) {
        links.add(new WidgetLink(newest));
        return this;
    }

    public WidgetPagedResources<T> build() {
        return new WidgetPagedResources<T>(iterable, page, links.toArray(new Link[0]));
    }
}
