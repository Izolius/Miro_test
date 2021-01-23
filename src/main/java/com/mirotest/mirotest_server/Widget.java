package com.mirotest.mirotest_server;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Date;
import java.util.UUID;

public class Widget extends Shape {
    public int zIndex;
    public Date lastModificationTime;
    public UUID id;
    public Widget(int zIndex) {
        this.zIndex = zIndex;
        id = UUID.randomUUID();
        lastModificationTime = new Date();
    }

    public Widget() {
        this(0);
    }

    public Widget(CreateWidgetParams params) {
        this(params.zIndex);
        this.width = params.width;
        this.height = params.height;
        this.coord = params.coord;
    }
}
