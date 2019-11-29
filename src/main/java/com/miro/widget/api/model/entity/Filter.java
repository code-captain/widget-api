package com.miro.widget.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Filter {

    @JsonProperty(value = "bottomLeftX")
    private Long bottomLeftX;

    @JsonProperty(value = "bottomLeftY")
    private Long bottomLeftY;

    @JsonProperty(value = "upperRightX")
    private Long upperRightX;

    @JsonProperty(value = "upperRightY")
    private Long upperRightY;

    public boolean isFilled() {
        return bottomLeftX != null
                && bottomLeftY != null
                && upperRightX != null
                && upperRightY != null;
    }
}
