package com.miro.widget.api.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Rectangle {
    //center point x
    protected long xCoordinate;
    //center point y
    protected long yCoordinate;

    protected long width;

    protected long height;

    protected Point bottomLeftPoint;

    protected Point upperRightPoint;

    public Rectangle(long xCoordinate, long yCoordinate, long width, long height) {
        if (width <= 0) {
            throw new IllegalArgumentException("Param `width` must be greater than zero");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Param `height` must be greater than zero");
        }

        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.width = width;
        this.height = height;
        this.bottomLeftPoint = new Point(
                calculateBottomLeftCoordinate(xCoordinate, width),
                calculateBottomLeftCoordinate(yCoordinate, height)
        );
        this.upperRightPoint = new Point(
                calculateUpperRightCoordinate(xCoordinate, width),
                calculateUpperRightCoordinate(yCoordinate, height)
        );
    }

    public Rectangle(Point bottomLeftPoint, Point upperRightPoint) {
        if (bottomLeftPoint == null) {
            throw new NullPointerException("Param `bottomLeftPoint` must be set");
        }
        if (upperRightPoint == null) {
            throw new NullPointerException("Param `upperRightPoint` must be set");
        }
        if (bottomLeftPoint.getXCoordinate() >= upperRightPoint.getXCoordinate()) {
            throw new IllegalArgumentException("Field `xCoordinate` in bottomLeftPoint must be less than `xCoordinate` in upperRightPoint");
        }
        if (bottomLeftPoint.getYCoordinate() >= upperRightPoint.getYCoordinate()) {
            throw new IllegalArgumentException("Field `yCoordinate` in bottomLeftPoint must be less than `yCoordinate` in upperRightPoint");
        }

        this.bottomLeftPoint = bottomLeftPoint;
        this.upperRightPoint = upperRightPoint;

        this.width = upperRightPoint.getXCoordinate() - bottomLeftPoint.getXCoordinate();
        this.height = upperRightPoint.getYCoordinate() - bottomLeftPoint.getYCoordinate();
        this.xCoordinate = bottomLeftPoint.getXCoordinate() + width / 2;
        this.yCoordinate = bottomLeftPoint.getYCoordinate() + height / 2;
    }

    public boolean contains(Rectangle other) {
        long width = this.width;
        long height = this.height;
        if ((width | height | other.width | other.height) < 0) {
            // at least one of the dimensions is negative
            return false;
        }
        // if any dimension is zero, tests below must return false
        if (this.bottomLeftPoint.getXCoordinate() > other.bottomLeftPoint.getXCoordinate()
            || this.bottomLeftPoint.getYCoordinate() > other.bottomLeftPoint.getYCoordinate()
        ) {
            return false;
        }

        if (other.upperRightPoint.getXCoordinate() <= other.bottomLeftPoint.getXCoordinate()) {

            if (this.upperRightPoint.getXCoordinate() > this.bottomLeftPoint.getXCoordinate()
                    || other.upperRightPoint.getXCoordinate() > this.upperRightPoint.getXCoordinate()
            ) {
                return false;
            }
        } else {

            if (this.upperRightPoint.getXCoordinate() > this.bottomLeftPoint.getXCoordinate()
                    && other.upperRightPoint.getXCoordinate() > this.upperRightPoint.getXCoordinate()
            ) {
                return false;
            }
        }
        if (other.upperRightPoint.getYCoordinate() <= other.bottomLeftPoint.getYCoordinate()) {
            if (this.upperRightPoint.getYCoordinate() > this.bottomLeftPoint.getYCoordinate()
                    || other.upperRightPoint.getYCoordinate() > this.upperRightPoint.getYCoordinate()
            ) {
                return false;
            }
        } else {
            if (this.upperRightPoint.getYCoordinate() > this.bottomLeftPoint.getYCoordinate()
                    && other.upperRightPoint.getYCoordinate() > this.upperRightPoint.getYCoordinate()
            ) {
                return false;
            }
        }
        return true;
    }

    private static long calculateBottomLeftCoordinate(long centerCoordinate, long side) {
        return centerCoordinate - side / 2;
    }

    private static long calculateUpperRightCoordinate(long centerCoordinate, long side) {
        return centerCoordinate + side / 2;
    }
}
