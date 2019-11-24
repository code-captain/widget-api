package com.miro.widget.api.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.hateoas.Link;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class WidgetLink extends Link {

    @JsonProperty
    private String type;

    public WidgetLink(Link link) {
        super(link.getHref(), link.getRel());

        this.type = link.getType();
    }
}