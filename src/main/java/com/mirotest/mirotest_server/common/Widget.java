package com.mirotest.mirotest_server.common;

import com.mirotest.mirotest_server.app.CreateWidgetParams;

import java.util.Date;
import java.util.UUID;

public class Widget extends Shape {
    public Integer zIndex;
    public final Date lastModificationTime;
    public final UUID id;
    public Widget(Integer zIndex) {
        super();
        this.zIndex = zIndex;
        id = UUID.randomUUID();
        lastModificationTime = new Date();
    }

    public Widget() {
        this(0);
    }

    public Widget(CreateWidgetParams params) throws Exception {
        this(params.zIndex);
        this.width = params.width;
        this.height = params.height;
        if (width <= 0) {
            throw new WrongWidgetField("Widget creation with not positive width");
        }
        if (height <= 0) {
            throw new WrongWidgetField("Widget creation with not positive height");
        }
        this.coord = params.coord;
    }
}
