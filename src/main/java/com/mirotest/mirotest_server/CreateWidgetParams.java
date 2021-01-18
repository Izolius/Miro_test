package com.mirotest.mirotest_server;

import com.mirotest.mirotest_server.Widget;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.UUID;

public class CreateWidgetParams {
    @NonNull
    public Point coord=new Point();

    @Nullable
    public int zIndex;

    @NonNull
    public int width, height;
}
