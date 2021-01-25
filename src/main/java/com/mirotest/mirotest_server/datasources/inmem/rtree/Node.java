package com.mirotest.mirotest_server.datasources.inmem.rtree;

import com.mirotest.mirotest_server.common.Shape;

import java.util.LinkedList;
import java.util.function.Consumer;

class Node<T> {
    final Shape shape; // only dependency from common package
    final LinkedList<Node<T>> children = new LinkedList<>();
    final boolean isLeaf;

    Node<T> parent;

    public Node(Shape shape, boolean leaf) {
        this.shape = new Shape(shape);
        this.isLeaf = leaf;
    }

    public Node(boolean leaf) {
        this.shape = new Shape();
        this.isLeaf = leaf;
    }

    public Node() {
        this.isLeaf = false;
        this.shape = new Shape();
    }

    public void VisitLeaves(Consumer<Entry<T>> visitor) {
        for (Node<T> child : children) {
            child.VisitLeaves(visitor);
        }
    }

}

class Entry<T> extends Node<T> {
    final T entry;

    public Entry(Shape shape, T entry) {
        // an entry isn't actually a leaf (its parent is a leaf)
        // but all the algorithms should stop at the first leaf they encounter,
        // so this little hack shouldn't be a problem.
        super(shape, true);
        this.entry = entry;
    }

    @Override
    public void VisitLeaves(Consumer<Entry<T>> visitor) {
        visitor.accept(this);
    }

    public String toString() {
        return "Entry: " + entry;
    }
}