package com.mirotest.mirotest_server;

import java.awt.*;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shape shape = (Shape) o;
        return width == shape.width && height == shape.height && coord.equals(shape.coord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coord, width, height);
    }
}
