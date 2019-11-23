package com.miro.widget.api.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.miro.widget.api.model.entity.Widget;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class WidgetDto {

    @JsonProperty(value = "id")
    private UUID id;

    @JsonProperty(value = "xcoordinate")
    private long xCoordinate;

    @JsonProperty(value = "ycoordinate")
    private long yCoordinate;

    @JsonProperty(value = "zindex")
    private Long zIndex;

    @JsonProperty(value = "width")
    private long width;

    @JsonProperty(value = "height")
    private long height;

    @JsonProperty(value = "modifiedAt")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date modifiedAt;

    public static WidgetDto fromEntity(Widget entity) {
        return new WidgetDto(
                entity.getId(),
                entity.getXCoordinate(),
                entity.getYCoordinate(),
                entity.getZIndex(),
                entity.getWidth(),
                entity.getHeight(),
                entity.getModifiedAt()
        );
    }
}