package com.mirotest.mirotest_server.datasources.inmem.rtree;

import com.mirotest.mirotest_server.common.Shape;

import java.util.*;

/**
 * RTree based on https://github.com/rweeks/util and http://www.bowdoin.edu/~ltoma/teaching/cs340/spring08/Papers/Rtree-chap1.pdf
 * @param <T>
 */
public class RTree<T> {
    private final int maxEntries;
    private final int minEntries;

    private Node<T> root;

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

    public RTree(){
        this(50,3);
    }

    private Node<T> buildRoot(boolean asLeaf) {
        return new Node<>(new Shape(), asLeaf);
    }

    /**
     * Searches the RTree for objects entirely in the given rectangle.
     * @return a list of objects whose rectangles overlap with the given
     * rectangle.
     */
    public List<T> search(Shape shape) {
        LinkedList<T> results = new LinkedList<>();
        search(shape, root, results);
        return results;
    }

    private void search(Shape window, Node<T> n,
                        LinkedList<T> results) {
        if (n.isLeaf) { //TODO: change to visitors pattern
            for (Node<T> child : n.children) {
                if (isOverlap(child.shape, window)) {
                    results.add(((Entry<T>) child).entry);
                }
            }
        } else {
            for (Node<T> child : n.children) {
                if (isPartialOverlap(child.shape, window)) {
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
        Node<T> leaf = findLeaf(root, window, entry);
        if (leaf == null)
            return false;
        assert (leaf.isLeaf) : "Entry is not found at leaf?!?";
        boolean removed = leaf.children.removeIf(node -> ((Entry<T>)node).entry.equals(entry));
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

    private Node<T> findLeaf(Node<T> n, Shape window, T entry) {
        if (n.isLeaf) {
            for (Node<T> child : n.children) {
                if (((Entry<T>) child).entry.equals(entry) && isOverlap(child.shape, window)) {
                    return n;
                }
            }
        } else {
            for (Node<T> child : n.children) {
                if (isPartialOverlap(child.shape, window)) {
                    Node<T> result = findLeaf(child, window, entry);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    private void condenseTree(Node<T> leaf) {
        LinkedList<Entry<T>> reinsert = new LinkedList<>();
        Node<T> currentNode = leaf;
        while (currentNode != root) {
            if (currentNode.children.size() < minEntries) {
                currentNode.parent.children.remove(currentNode);
                currentNode.VisitLeaves(reinsert::add);
            } else {
                recalcMbr(currentNode);
            }
            currentNode = currentNode.parent;
        }
        for (Entry<T> node : reinsert) {
            insert(node.shape, node.entry);
        }
    }

    /**
     * Inserts the given entry into the RTree, associated with the given
     * rectangle.
     *
     * @param entry the entry to insert
     */
    public void insert(Shape shape, T entry) {
        Entry<T> e = new Entry<>(shape, entry);
        Node<T> l = chooseLeaf(root, e);
        l.children.add(e);
        size++;
        e.parent = l;
        recalcMbr(l);
        if (l.children.size() > maxEntries) {
            Node<T> right = splitNode(l);
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
    private Node<T> splitNode(Node<T> splittedNode) {
        Node<T> right = new Node<>(splittedNode.isLeaf);
        right.parent = splittedNode.parent;
        if (splittedNode.parent != null)
            right.parent.children.add(right);

        LinkedList<Node<T>> children = new LinkedList<>(splittedNode.children);
        splittedNode.children.clear();
        qPickSeeds(children, splittedNode, right);

        while (!children.isEmpty()) {
            if (right.children.size() + children.size() == minEntries) {
                right.children.addAll(children);
                children.forEach(n -> n.parent = right);
                recalcMbr(right);
                break;
            } else if (splittedNode.children.size() + children.size() == minEntries) {
                splittedNode.children.addAll(children);
                children.forEach(n -> n.parent = splittedNode);
                recalcMbr(splittedNode);
                break;
            }
            Node<T> choosen = qPickNext(children, splittedNode, right);
            Node<T> preferred;
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
            choosen.parent = preferred;
            recalcMbr(preferred.shape, choosen);
        }
        return right;
    }

    private void adjustTree(Node<T> left, Node<T> right) {
        if (right != null) {
            if (left == root) {
                root = buildRoot(false);
                root.children.add(left);
                left.parent = root;
                root.children.add(right);
                right.parent = root;
                recalcMbr(root);
            } else {
                var curRoot = left.parent;
                if (curRoot.children.size() > maxEntries) {
                    var newRight = splitNode(curRoot);
                    adjustTree(curRoot, newRight);
                }
            }
        } else {
            if (left.parent != null) {
                adjustTree(left.parent, null);
            }
        }
    }

    // Implementation of Quadratic PickSeeds
    private void qPickSeeds(LinkedList<Node<T>> nn, Node<T> leftNode, Node<T> rightNode) {
        Node<T> left = null, right = null;
        int maxWaste = -1;
        for (Node<T> node1 : nn) {
            for (Node<T> node2 : nn) {
                if (node1 == node2) continue;
                int node1Area = getArea(node1.shape);
                int node2Area = getArea(node2.shape);

                int minx, miny, maxx, maxy;
                minx = Math.min(node1.shape.coord.x, node2.shape.coord.x);
                miny = Math.min(node1.shape.coord.y, node2.shape.coord.y);

                maxx = Math.max(node1.shape.coord.x + node1.shape.width, node2.shape.coord.x + node2.shape.width);
                maxy = Math.max(node1.shape.coord.y + node1.shape.height, node2.shape.coord.y + node2.shape.height);

                int waste = Math.abs(maxx - minx) * Math.abs(maxy - miny) - node1Area - node2Area;
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
        left.parent = leftNode;
        recalcMbr(leftNode);
        rightNode.children.add(right);
        right.parent = rightNode;
        recalcMbr(rightNode);
    }

    /**
     * Implementation of QuadraticPickNext
     *
     * @param cc the children to be divided between the new nodes, one item will be removed from this list.
     * @param left the candidate nodes for the children to be added to.
     * @param right the candidate nodes for the children to be added to.
     */
    private Node<T> qPickNext(LinkedList<Node<T>> cc, Node<T> left, Node<T> right) {
        int maxDiff = -1;
        Node<T> nextC = null;
        for (Node<T> c : cc) {
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

    private void recalcMbr(Shape shape, Node<T> newChild) {
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


    private void recalcMbr(Node<T> node) {
        assert (node.children.size() > 0) : "recalcShape() called on empty node!";
        int minx, miny, maxx, maxy;
        minx = miny = Integer.MAX_VALUE;
        maxx = maxy = Integer.MIN_VALUE;

        for (Node<T> child : node.children) {
            var shape = child.shape;
            var coord = shape.coord;

            minx = Math.min(minx, coord.x);
            miny = Math.min(miny, coord.y);

            maxx = Math.max(maxx, coord.x + shape.width);
            maxy = Math.max(maxy, coord.y + shape.height);
        }

        node.shape.coord.x = minx;
        node.shape.coord.y = miny;
        node.shape.width = maxx - minx;
        node.shape.height = maxy - miny;
    }

    private Node<T> chooseLeaf(Node<T> curRoot, Entry<T> entry) {
        if (curRoot.isLeaf) {
            return curRoot;
        }
        int minInc = Integer.MAX_VALUE;
        Node<T> next = curRoot.children.get(0);
        for (Node<T> child : curRoot.children) {
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
    private int getRequiredExpansion(Shape shape, Node<T> e) {
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

    private boolean isPartialOverlap(Shape shape,
                              Shape window) {
        if (window.coord.x < shape.coord.x) {
            if (shape.coord.x > window.coord.x + window.width)
                return false;
        } else if (window.coord.x > shape.coord.x) {
            if (shape.coord.x + shape.width < window.coord.x)
                return false;
        }

        if (window.coord.y < shape.coord.y) {
            return shape.coord.y <= window.coord.y + window.height;
        } else if (window.coord.y > shape.coord.y) {
            return shape.coord.y + shape.height >= window.coord.y;
        }
        return true;
    }


}
