package com.miro.widget.api.model.dto;

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
}