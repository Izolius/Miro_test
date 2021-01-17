package com.mirotest.mirotest_server.create_widget;

import com.mirotest.mirotest_server.Widget;
import org.springframework.lang.Nullable;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.UUID;

public class createWidgetRequest {
    public Point coord=new Point();
    @Nullable
    public int zIndex;
    public int width, height;
}
