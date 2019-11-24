package com.miro.widget.api.model.response;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import java.util.Arrays;

public class WidgetResource<T> extends Resource<T> {

    public WidgetResource(T content, Link... links) {
        super(content, Arrays.asList(links));
    }

    @Override
    public void add(Link link) {
        super.add(new WidgetLink(link));
    }
}