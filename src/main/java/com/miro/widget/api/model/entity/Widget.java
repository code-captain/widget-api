package com.miro.widget.api.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Getter
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

    private CornerPoint bottomLeftPoint;

    private CornerPoint upperRightPoint;

    public Widget(UUID id, long xCoordinate, long yCoordinate, Long zIndex, long width, long height) {
        if (width <= 0) {
            throw new IllegalArgumentException("Param `width` must be greater than zero");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Param `height` must be greater than zero");
        }

        this.id = id;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.zIndex = zIndex;
        this.width = width;
        this.height = height;
        this.bottomLeftPoint = new CornerPoint(
                calculateBottomLeftCoordinate(xCoordinate, width),
                calculateBottomLeftCoordinate(yCoordinate, height)
        );
        this.upperRightPoint = new CornerPoint(
                calculateUpperRightCoordinate(xCoordinate, width),
                calculateUpperRightCoordinate(yCoordinate, height)
        );
        this.modifiedAt = Date.from(Instant.now());
    }

    public void incrementZIndex() {
        this.zIndex++;
        this.modifiedAt = Date.from(Instant.now());
    }

    private static long calculateBottomLeftCoordinate(long centerCoordinate, long side) {
        return centerCoordinate - side / 2;
    }

    private static long calculateUpperRightCoordinate(long centerCoordinate, long side) {
        return centerCoordinate + side / 2;
    }
}