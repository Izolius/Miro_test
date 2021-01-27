package com.mirotest.mirotest_server.app;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.awt.*;

public class CreateWidgetParams {
    @NonNull
    public Point coord=new Point();

    @Nullable
    public Integer zIndex;

    @NonNull
    public int width, height;
}
