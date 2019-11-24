package com.miro.widget.api.model.entity;

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
}