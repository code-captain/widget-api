package com.miro.widget.api.model.entity;

import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class RectangleTest {

    @Test
    public void contains_WhenReflexivityCall_ReturnTrue() {
        Widget widget1 = new Widget(UUID.randomUUID(), 150, 100, 1L, 300, 200);
        Widget widget2 = new Widget(UUID.randomUUID(), 150, 100, 1L, 300, 200);
        Assert.assertTrue(widget1.contains(widget2));
        Assert.assertTrue(widget2.contains(widget1));
    }

    @Test
    public void contains_WhenFirstContainsMany_ReturnTrue() {
        Widget widget1 = new Widget(UUID.randomUUID(), 150, 100, 1L, 300, 200);
        Widget widget2 = new Widget(UUID.randomUUID(), 50, 50, 2L, 100, 100);
        Widget widget3 = new Widget(UUID.randomUUID(), 250, 150, 3L, 100, 100);

        Assert.assertTrue(widget1.contains(widget2));
        Assert.assertTrue(widget1.contains(widget3));
        Assert.assertFalse(widget2.contains(widget1));
        Assert.assertFalse(widget2.contains(widget3));
    }
}