package com.miro.widget.api.model.response;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

public class WidgetResourceSupport extends ResourceSupport {

    @Override
    public void add(Link link) {
        super.add(new WidgetLink(link));
    }
}
