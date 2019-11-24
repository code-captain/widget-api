package com.miro.widget.api.model.response;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;

import java.util.Arrays;

public class WidgetResources<T> extends Resources<T> {

    public WidgetResources(Iterable<T> content, Link... links) {
        super(content, Arrays.asList(links));
    }

    @Override
    public void add(Link link) {
        super.add(new WidgetLink(link));
    }
}