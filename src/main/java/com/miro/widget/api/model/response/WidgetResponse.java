package com.miro.widget.api.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@NoArgsConstructor
@AllArgsConstructor
public class WidgetResponse extends WidgetResourceSupport {

    @JsonProperty(value = "id")
    private UUID uuid;

    @JsonProperty(value = "xСoordinate")
    private long xCoordinate;

    @JsonProperty(value = "yСoordinate")
    private long yCoordinate;

    @JsonProperty(value = "zIndex")
    private Long zIndex;

    @JsonProperty(value = "width")
    private long width;

    @JsonProperty(value = "height")
    private long height;

    @JsonProperty(value = "modifiedAt")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "Asia/Yekaterinburg")
    private Date modifiedAt;
}
