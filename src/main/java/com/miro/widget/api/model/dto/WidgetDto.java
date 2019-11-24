package com.miro.widget.api.model.dto;

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
    private UUID id;

    private long xCoordinate;

    private long yCoordinate;

    private Long zIndex;

    private long width;

    private long height;

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