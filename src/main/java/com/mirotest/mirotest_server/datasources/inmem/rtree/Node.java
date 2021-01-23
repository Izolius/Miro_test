package com.mirotest.mirotest_server.datasources.inmem.rtree;

import com.mirotest.mirotest_server.common.Shape;

import java.util.LinkedList;
import java.util.function.Consumer;

class Node {
    final Shape shape;
    final LinkedList<Node> children = new LinkedList<>();
    final boolean isLeaf;

    Node parent;

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

    public <T> void VisitLeafs(Consumer<Entry<T>> visitor) {
        for (Node child : children) {
            child.VisitLeafs(visitor);
        }
    }

}

class Entry<T> extends Node {
    final T entry;

    public Entry(Shape shape, T entry) {
        // an entry isn't actually a leaf (its parent is a leaf)
        // but all the algorithms should stop at the first leaf they encounter,
        // so this little hack shouldn't be a problem.
        super(shape, true);
        this.entry = entry;
    }

    @Override
    public <E> void VisitLeafs(Consumer<Entry<E>> visitor) {
        visitor.accept((Entry<E>) this);
    }

    public String toString() {
        return "Entry: " + entry;
    }
}