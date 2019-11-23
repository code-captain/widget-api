package com.miro.widget.api.model.entity;

import com.miro.widget.api.model.dto.WidgetDto;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString(onlyExplicitlyIncluded = true)
public class Widget {
    @ToString.Include
    private UUID id;

    @ToString.Include
    private long xCoordinate;

    private long yCoordinate;

    @ToString.Include
    private Long zIndex;

    private long width;

    private long height;

    private Date modifiedAt;

    public static Widget fromDto(WidgetDto dto) {
        Widget widget = new Widget();
        widget.setXCoordinate(dto.getXCoordinate());
        widget.setYCoordinate(dto.getYCoordinate());
        widget.setZIndex(dto.getZIndex());
        widget.setWidth(dto.getWidth());
        widget.setHeight(dto.getHeight());
        return widget;
    }

    public static Widget copy(Widget self) {
        return new Widget(
                self.getId(),
                self.getXCoordinate(),
                self.getYCoordinate(),
                self.getZIndex(),
                self.getWidth(),
                self.getHeight(),
                self.getModifiedAt()
        );
    }
}