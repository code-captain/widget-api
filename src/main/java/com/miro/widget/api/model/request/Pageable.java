package com.miro.widget.api.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pageable {
    @Min(value = 1, message = "Field `page` must be greater than 0")
    @JsonProperty(value = "page")
    private long page = 1;

    @Min(value = 1, message = "Field `size` must be greater than 0")
    @Max(value = 500, message = "Field `size` must be less than 500")
    @JsonProperty(value = "size")
    private long size = 10;
}
