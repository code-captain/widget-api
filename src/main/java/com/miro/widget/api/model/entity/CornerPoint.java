package com.miro.widget.api.model.entity;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@EqualsAndHashCode
@RequiredArgsConstructor
public class CornerPoint implements Comparable<CornerPoint> {
    @NonNull
    private final long xCoordinate;
    @NonNull
    private final long yCoordinate;

    @Override
    public int compareTo(CornerPoint o) {
        Objects.requireNonNull(o, "Specified object is null");
        if ((xCoordinate >= o.xCoordinate && yCoordinate >  o.yCoordinate)
                || (yCoordinate >=  o.yCoordinate && xCoordinate > o.xCoordinate)
        ) {
            return 1;
        } else if (xCoordinate == o.xCoordinate
                && yCoordinate == o.yCoordinate
        ) {
            return 0;
        } else {
            return -1;
        }
    }
}