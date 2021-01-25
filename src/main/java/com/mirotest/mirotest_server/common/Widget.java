package com.mirotest.mirotest_server.common;

import com.mirotest.mirotest_server.app.CreateWidgetParams;

import java.util.Date;
import java.util.UUID;

public class Widget extends Shape {
    public int zIndex;
    public final Date lastModificationTime;
    public final UUID id;
    public Widget(int zIndex) {
        super();
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
