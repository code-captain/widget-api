package com.miro.widget.api.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class Point {

    @NonNull
    private final long xCoordinate;

    @NonNull
    private final long yCoordinate;
}