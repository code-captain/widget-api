package com.miro.widget.api.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class WidgetRequest {

    @JsonProperty(value = "xCoordinate")
    private long xCoordinate;

    @JsonProperty(value = "yCoordinate")
    private long yCoordinate;

    @JsonProperty(value = "zIndex")
    private Long zIndex;

    @JsonProperty(value = "width")
    private long width;

    @JsonProperty(value = "height")
    private long height;
}
