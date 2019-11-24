package com.miro.widget.api.model.response;

import com.miro.widget.api.model.entity.Page;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;

import java.util.Collection;

public class WidgetPagedResources<T> extends PagedResources<T> {

    public WidgetPagedResources(Collection<T> iterable, Page page, Link... links) {
        super(iterable, new PageMetadata(page.getItemCount(), page.getNumber(), page.getTotalItems(), page.getAvailableCount()), links);
    }

    @Override
    public void add(Link link) {
        super.add(new WidgetLink(link));
    }
}
