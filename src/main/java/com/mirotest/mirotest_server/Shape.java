package com.mirotest.mirotest_server;

import java.awt.*;

public class Shape {
    public Point coord;
    public int width, height;

    public Shape(Shape orig) {
        this.coord = new Point(orig.coord);
        this.width = orig.width;
        this.height = orig.height;
    }

    public Shape(int x, int y, int width, int height) {
        coord = new Point(x,y);
        this.width = width;
        this.height = height;
    }

    public Shape() {
        coord = new Point();
    }
}
