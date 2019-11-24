package com.miro.widget.api.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WidgetRequest {

    @JsonProperty(value = "xCoordinate")
    private long xCoordinate;

    @JsonProperty(value = "yCoordinate")
    private long yCoordinate;

    @JsonProperty(value = "zIndex")
    private Long zIndex;

    @Min(value = 1, message = "Field `width` must be greater than 0")
    @JsonProperty(value = "width")
    private long width;

    @Min(value = 1, message = "Field `width` must be greater than 0")
    @JsonProperty(value = "height")
    private long height;
}
