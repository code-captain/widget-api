package com.miro.widget.api.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(onlyExplicitlyIncluded = true)
public class Widget extends Rectangle {
    @ToString.Include
    private UUID id;

    private Long zIndex;

    private Date modifiedAt;

    public Widget(UUID id, long xCoordinate, long yCoordinate, Long zIndex, long width, long height) {
        super(xCoordinate, yCoordinate, width, height);

        this.id = id;
        this.zIndex = zIndex;
        this.modifiedAt = Date.from(Instant.now());
    }

    public void incrementZIndex() {
        this.zIndex++;
        this.modifiedAt = Date.from(Instant.now());
    }
}