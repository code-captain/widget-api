package com.miro.widget.api.model.entity;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class CornerPointComparerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void compareTo_WhenSpecifiedCornerPointIsNull_NullPointerException() {
        expectedException.expect(IsInstanceOf.instanceOf(NullPointerException.class));
        CornerPoint testPoint = new CornerPoint(0, 0);
        testPoint.compareTo(null);
    }

    @Test
    public void compareTo_WhenReflexiveCall_CornerPointsAreEqual() {
        CornerPoint firstPoint = new CornerPoint(0, 0);
        CornerPoint secondPoint = new CornerPoint(0, 0);
        assertEquals(firstPoint, secondPoint);
        assertEquals(0, firstPoint.compareTo(secondPoint));
    }

    @Test
    public void compareTo_WhenSymmetricCall_FirstCornerPointIsLessThanSecond() {
        CornerPoint firstPoint = new CornerPoint(50, 50);
        CornerPoint secondPoint = new CornerPoint(75, 50);
        assertNotEquals(firstPoint, secondPoint);
        assertEquals(-1, firstPoint.compareTo(secondPoint));
        assertEquals(1, secondPoint.compareTo(firstPoint));

        secondPoint = new CornerPoint(75, 75);
        assertNotEquals(firstPoint, secondPoint);
        assertEquals(-1, firstPoint.compareTo(secondPoint));
        assertEquals(1, secondPoint.compareTo(firstPoint));
    }

    @Test
    public void compareTo_WhenTransitiveCall_FirstCornerPointIsLessThanSecondAndSecondCornerPointIsLessThanThird() {
        CornerPoint firstPoint = new CornerPoint(50, 50);
        CornerPoint secondPoint = new CornerPoint(50, 100);
        CornerPoint thirdPoint = new CornerPoint(100, 100);
        assertNotEquals(firstPoint, secondPoint);
        assertNotEquals(firstPoint, thirdPoint);
        assertNotEquals(secondPoint, thirdPoint);
        assertEquals(-1, firstPoint.compareTo(secondPoint));
        assertEquals(-1, secondPoint.compareTo(thirdPoint));
        assertEquals(-1, firstPoint.compareTo(thirdPoint));
    }
}