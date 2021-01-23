package com.mirotest.mirotest_server.datasources.inmem;

import com.mirotest.mirotest_server.Shape;

import java.util.*;
import java.util.function.Consumer;

/**
 * Implementation of an arbitrary-dimension RTree. Based on R-Trees: A Dynamic
 * Index Structure for Spatial Searching (Antonn Guttmann, 1984)
 * <p>
 * This class is not thread-safe.
 * <p>
 * Copyright 2010 Russ Weeks rweeks@newbrightidea.com Licensed under the GNU
 * LGPL License details here: http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * @param <T> the type of entry to store in this RTree.
 */
public class RTree<T> {
    private final int maxEntries;
    private final int minEntries;

    private Node root;

    private int size;

    /**
     * Creates a new RTree.
     *
     * @param maxEntries maximum number of entries per node
     * @param minEntries minimum number of entries per node (except for the root node)
     */
    public RTree(int maxEntries, int minEntries) {
        assert (minEntries <= (maxEntries / 2));
        this.maxEntries = maxEntries;
        this.minEntries = minEntries;
        root = buildRoot(true);
    }

    private Node buildRoot(boolean asLeaf) {
        return new Node(new Shape(), asLeaf);
    }

    /**
     * Builds a new RTree using default parameters: maximum 50 entries per node
     * minimum 2 entries per node 2 dimensions
     */
    public RTree() {
        this(50, 2);
    }

    /**
     * @return the maximum number of entries per node
     */
    public int getMaxEntries() {
        return maxEntries;
    }

    /**
     * @return the minimum number of entries per node for all nodes except the
     * root.
     */
    public int getMinEntries() {
        return minEntries;
    }

    /**
     * @return the number of items in this tree.
     */
    public int size() {
        return size;
    }

    /**
     * Searches the RTree for objects overlapping with the given rectangle.
     * @return a list of objects whose rectangles overlap with the given
     * rectangle.
     */
    public List<T> search(Shape shape) {
        LinkedList<T> results = new LinkedList<>();
        search(shape, root, results);
        return results;
    }

    private void search(Shape window, Node n,
                        LinkedList<T> results) {
        if (n.isLeaf) { //TODO: change to visitors pattern
            for (Node child : n.children) {
                if (isOverlap(child.shape, window)) {
                    results.add(((Entry) child).entry);
                }
            }
        } else {
            for (Node child : n.children) {
                if (isOverlap(child.shape, window)) {
                    search(window, child, results);
                }
            }
        }
    }

    /**
     * Deletes the entry associated with the given rectangle from the RTree
     *
     * @param entry      the entry to delete
     * @return true iff the entry was deleted from the RTree.
     */
    public boolean delete(Shape window, T entry) {
        Node leaf = findLeaf(root, window, entry);
        if (leaf == null)
            return false;
        assert (leaf.isLeaf) : "Entry is not found at leaf?!?";
        boolean removed = leaf.children.removeIf(node -> ((Entry)node).entry.equals(entry));
        assert removed;

        condenseTree(leaf);
        size--;
        if (root.children.size() == 1 && !root.isLeaf) {
            root = root.children.get(0);
            root.parent = null;
        }
        if (size == 0) {
            root = buildRoot(true);
        }
        return true;
    }

    private Node findLeaf(Node n, Shape window, T entry) {
        if (n.isLeaf) {
            for (Node child : n.children) {
                if (((Entry) child).entry.equals(entry)) {
                    return n;
                }
            }
        } else {
            for (Node child : n.children) {
                if (isOverlap(child.shape, window)) {
                    Node result = findLeaf(child, window, entry);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    private void condenseTree(Node leaf) {
        LinkedList<Entry> reinsert = new LinkedList<>();
        Node currentNode = leaf;
        while (currentNode != root) {
            if (currentNode.children.size() < minEntries) {
                currentNode.parent.children.remove(currentNode);
                currentNode.VisitLeafs(reinsert::add);
            } else {
                recalcMbr(currentNode);
            }
            currentNode = currentNode.parent;
        }
        for (Entry node : reinsert) {
            insert(node.shape, node.entry);
        }
    }

    /**
     * Empties the RTree
     */
    public void clear() {
        root = buildRoot(true);
        // let the GC take care of the rest.
    }

    /**
     * Inserts the given entry into the RTree, associated with the given
     * rectangle.
     *
     * @param entry      the entry to insert
     */
    public void insert(Shape shape, T entry) {
        Entry e = new Entry(shape, entry);
        Node l = chooseLeaf(root, e);
        l.children.add(e);
        size++;
        e.parent = l;
        if (l.children.size() > maxEntries) {
            Node right = splitNode(l);
            adjustTree(l, right);
        } else {
            adjustTree(l, null);
        }
    }

    /***
     *
     * @param splittedNode node for split. Will be changed
     * @return new Node
     */
    private Node splitNode(Node splittedNode) {
        // TODO: this class probably calls "tighten" a little too often.
        // For instance the call at the end of the "while (!cc.isEmpty())" loop
        // could be modified and inlined because it's only adjusting for the addition
        // of a single node.  Left as-is for now for readability.
        Node right = new Node();
        right.parent = splittedNode.parent;
        if (splittedNode.parent != null)
            right.parent.children.add(right);

        LinkedList<Node> children = new LinkedList<>(splittedNode.children);
        splittedNode.children.clear();
        qPickSeeds(children, splittedNode, right);

        while (!children.isEmpty()) {
            if (right.children.size() + children.size() == minEntries) {
                right.children.addAll(children);
                recalcMbr(right);
                break;
            } else if (splittedNode.children.size() + children.size() == minEntries) {
                splittedNode.children.addAll(children);
                recalcMbr(splittedNode);
                break;
            }
            Node choosen = qPickNext(children, splittedNode, right);
            Node preferred;
            int e0 = getRequiredExpansion(splittedNode.shape, choosen);
            int e1 = getRequiredExpansion(right.shape, choosen);
            if (e0 < e1) {
                preferred = splittedNode;
            } else if (e0 > e1) {
                preferred = right;
            } else {
                int a0 = getArea(splittedNode.shape);
                int a1 = getArea(right.shape);
                if (a0 < a1) {
                    preferred = splittedNode;
                } else if (e0 > a1) {
                    preferred = right;
                } else {
                    if (splittedNode.children.size() < right.children.size()) {
                        preferred = splittedNode;
                    } else if (splittedNode.children.size() > right.children.size()) {
                        preferred = right;
                    } else {
                        if (Math.random() > 0.5)
                            preferred = splittedNode;
                        else
                            preferred = right;
                    }
                }
            }
            preferred.children.add(choosen);
            recalcMbr(preferred.shape, choosen);
        }
        return right;
    }

    private void adjustTree(Node left, Node right) {
        if (right != null) {
            if (left == root) {
                root = buildRoot(false);
                root.children.add(left);
                left.parent = root;
                root.children.add(right);
                right.parent = root;
                recalcMbr(root);
            } else {
                if (left.parent.children.size() > maxEntries) {
                    var newRight = splitNode(left.parent);
                    adjustTree(left.parent, newRight);
                }
            }
        } else {
            if (left.parent != null) {
                adjustTree(left.parent, null);
            }
        }
    }

    // Implementation of Quadratic PickSeeds
    private void qPickSeeds(LinkedList<Node> nn, Node leftNode, Node rightNode) { // TODO: create Pair class
        Node left = null, right = null;
        int maxWaste = -1;
        for (Node node1 : nn) {
            for (Node node2 : nn) {
                if (node1 == node2) continue;
                int node1Area = getArea(node1.shape);
                int node2Area = getArea(node2.shape);

                int minx, miny, maxx, maxy;
                minx = Math.min(node1.shape.coord.x, node2.shape.coord.x);
                miny = Math.min(node1.shape.coord.y, node2.shape.coord.y);

                maxx = Math.max(node1.shape.coord.x + node1.shape.width, node2.shape.coord.x + node2.shape.width);
                maxy = Math.max(node1.shape.coord.y + node1.shape.height, node2.shape.coord.y + node2.shape.height);

                int waste = (maxx - minx) * (maxy - miny) - node1Area - node2Area;
                if (waste > maxWaste) {
                    maxWaste = waste;
                    left = node1;
                    right = node2;
                }
            }
        }
        nn.remove(left);
        nn.remove(right);

        leftNode.children.add(left);
        rightNode.children.add(right);
    }

    /**
     * Implementation of QuadraticPickNext
     *
     * @param cc the children to be divided between the new nodes, one item will be removed from this list.
     * @param left the candidate nodes for the children to be added to.
     * @param right the candidate nodes for the children to be added to.
     */
    private Node qPickNext(LinkedList<Node> cc, Node left, Node right) {
        int maxDiff = -1;
        Node nextC = null;
        for (Node c : cc) {
            int n0Exp = getRequiredExpansion(left.shape, c);
            int n1Exp = getRequiredExpansion(right.shape, c);
            int diff = Math.abs(n1Exp - n0Exp);
            if (diff > maxDiff) {
                maxDiff = diff;
                nextC = c;
            }
        }
        assert (nextC != null) : "No node selected from qPickNext";
        cc.remove(nextC);
        return nextC;
    }

    private void recalcMbr(Shape shape, Node newChild) {
        int minx, miny, maxx, maxy;

        var coord = shape.coord;
        var childShape = newChild.shape;

        minx = Math.min(childShape.coord.x, coord.x);
        miny = Math.min(childShape.coord.y, coord.y);

        maxx = Math.max(childShape.coord.x + childShape.width, coord.x + shape.width);
        maxy = Math.max(childShape.coord.y + childShape.height, coord.y + shape.height);

        shape.coord.x = minx;
        shape.coord.y = miny;
        shape.width = maxx - minx;
        shape.height = maxy - miny;
    }


    private void recalcMbr(Node node) {
        assert (node.children.size() > 0) : "recalcShape() called on empty node!";
        int minx, miny, maxx, maxy;
        minx = miny = Integer.MAX_VALUE;
        maxx = maxy = Integer.MIN_VALUE;

        for (Node child : node.children) {
            var shape = child.shape;
            var coord = shape.coord;

            minx = Math.min(minx, coord.x);
            miny = Math.min(miny, coord.y);

            maxx = Math.max(maxx, coord.x + shape.width);
            maxy = Math.max(maxy, coord.y + shape.height);
        }

        if (node.shape == null)
            node.shape = new Shape(minx, miny, maxx - minx, maxy - miny);
        else {
            node.shape.coord.x = minx;
            node.shape.coord.y = miny;
            node.shape.width = maxx - minx;
            node.shape.height = maxy - miny;
        }
    }

    private Node chooseLeaf(Node curRoot, Entry entry) {
        if (curRoot.isLeaf) {
            return curRoot;
        }
        int minInc = Integer.MAX_VALUE;
        Node next = curRoot.children.get(0);
        for (Node child : curRoot.children) {
            int inc = getRequiredExpansion(child.shape, entry);
            if (inc < minInc) {
                minInc = inc;
                next = child;
            } else if (inc == minInc) {
                int curArea = getArea(next.shape);
                int childArea = getArea(child.shape);
                if (childArea < curArea) {
                    next = child;
                }
            }
        }
        return chooseLeaf(next, entry);
    }

    /**
     * Returns the increase in area necessary for the given rectangle to cover the
     * given entry.
     */
    private int getRequiredExpansion(Shape shape, Node e) {
        int area = getArea(shape);
        var tmp = new Shape(shape);
        recalcMbr(tmp, e);
        return getArea(tmp) - area;
    }

    private int getArea(Shape shape) {
        return shape.width * shape.height;
    }

    private boolean isOverlap(Shape shape,
                              Shape window) {
        if (shape.coord.x < window.coord.x)
            return false;
        if (shape.coord.y < window.coord.y)
            return false;
        if (shape.coord.x + shape.width > window.coord.x + window.width)
            return false;
        return shape.coord.y + shape.height <= window.coord.y + window.height;
    }

    private class Node {
        Shape shape;
        final LinkedList<Node> children = new LinkedList<>();
        final boolean isLeaf;

        Node parent;

        public Node(Shape shape, boolean leaf) {
            this.shape = new Shape(shape);
            this.isLeaf = leaf;
        }

        public Node() {
            this.isLeaf = false;
        }

        public void VisitLeafs(Consumer<Entry> visitor) {
            for (Node child : children) {
                child.VisitLeafs(visitor);
            }
        }

    }

    private class Entry extends Node {
        final T entry;

        public Entry(Shape shape, T entry) {
            // an entry isn't actually a leaf (its parent is a leaf)
            // but all the algorithms should stop at the first leaf they encounter,
            // so this little hack shouldn't be a problem.
            super(shape, true);
            this.entry = entry;
        }

        @Override
        public void VisitLeafs(Consumer<Entry> visitor) {
            visitor.accept(this);
        }

        public String toString() {
            return "Entry: " + entry;
        }
    }
}
