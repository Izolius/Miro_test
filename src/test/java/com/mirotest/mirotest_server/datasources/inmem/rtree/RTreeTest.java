package com.mirotest.mirotest_server.datasources.inmem.rtree;

import com.mirotest.mirotest_server.common.Shape;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RTreeTest {

    static <T> Node getRoot(RTree<T> tree) {
        Node root;
        try {
            var field = tree.getClass().getDeclaredField("root");
            field.setAccessible(true);
            root = (Node) field.get(tree);
        } catch (Exception e) {
            return null;
        }
        assertParentsAndLeafs(root);
        return root;
    }

    static void assertParentsAndLeafs(Node root) {
        for (Node child : root.children)
            assertEquals(root, child.parent);
        root.children.forEach(RTreeTest::assertParentsAndLeafs);
    }

    @Test
    void search() {
        RTree<Integer> tree = new RTree<>();
        tree.insert(new Shape(0, 0, 1, 1), 2);
        var res = tree.search(new Shape(-1, -1, 2, 2));
        assertArrayEquals(res.toArray(), new Object[]{2});
    }

    @Test
    void search2() {
        RTree<Integer> tree = new RTree<>();
        tree.insert(new Shape(0, 0, 1, 1), 2);
        tree.insert(new Shape(1, 1, 2, 2), 3);
        var res = tree.search(new Shape(-1, -1, 4, 4));
        assertArrayEquals(res.toArray(), new Object[]{2, 3});
    }

    @Test
    void split() {
        RTree<Integer> tree = new RTree<>(4, 2);
        tree.insert(new Shape(0, 0, 1, 1), 2);
        tree.insert(new Shape(1, 1, 2, 2), 3);
        tree.insert(new Shape(1, 2, 2, 2), 4);
        tree.insert(new Shape(1, 1, 3, 2), 5);
        tree.insert(new Shape(-1, -1, 1, 1), 6);

        var root = getRoot(tree);
        assertEquals(new Shape(-1, -1, 5, 5), root.shape);
        assertEquals(2, root.children.size());
        assertEquals(2, root.children.get(1).children.size());
        assertEquals(new Shape(-1, -1, 2, 2), root.children.get(1).shape);
        assertEquals(root, root.children.get(0).parent);
        assertEquals(root, root.children.get(1).parent);
        assertEquals(root.children.get(0), root.children.get(0).children.get(0).parent);
        assertEquals(root.children.get(0), root.children.get(0).children.get(1).parent);
        assertEquals(root.children.get(1), root.children.get(1).children.get(0).parent);
        assertEquals(root.children.get(1), root.children.get(1).children.get(1).parent);
    }

    @Test
    void parentSplit() {
        RTree<Integer> tree = new RTree<>(3, 1);
        tree.insert(new Shape(0, 0, 1, 1), 2);
        tree.insert(new Shape(-5, 0, 1, 1), 3);
        tree.insert(new Shape(5, 2, 2, 2), 4);
        tree.insert(new Shape(5, 1, 3, 2), 5);
        tree.insert(new Shape(-5, -1, 1, 1), 6);
        tree.insert(new Shape(0, 1, 1, 1), 7);
        tree.insert(new Shape(5, 7, 2, 2), 8);
        tree.insert(new Shape(5, 7, 3, 2), 9);

        var root = getRoot(tree);
        assertEquals(2, root.children.size());
        assertEquals(new Shape(-5, -1, 13, 10), root.shape);
        assertFalse(root.children.get(0).isLeaf);
        assertFalse(root.children.get(1).isLeaf);

        assertEquals(new Shape(-5, -1, 6, 3), root.children.get(0).shape);
        assertEquals(new Shape(5, 1, 3, 8), root.children.get(1).shape);
    }

    @Test
    void deepSearch() {
        RTree<Integer> tree = new RTree<>(3, 1);
        tree.insert(new Shape(0, 0, 1, 1), 2);
        tree.insert(new Shape(-5, 0, 1, 1), 3);
        tree.insert(new Shape(5, 2, 2, 2), 4);
        tree.insert(new Shape(5, 1, 3, 2), 5);
        tree.insert(new Shape(-5, -1, 1, 1), 6);
        tree.insert(new Shape(0, 1, 1, 1), 7);
        tree.insert(new Shape(5, 7, 2, 2), 8);
        tree.insert(new Shape(5, 7, 3, 2), 9);

        var found = tree.search(new Shape(-5, -1, 6, 3));

        assertArrayEquals(found.toArray(), new Object[]{6, 3, 7, 2});
    }

    @Test
    void deepSearchWithPartOverlap() {
        RTree<Integer> tree = new RTree<>(3, 1);
        tree.insert(new Shape(0, 0, 1, 1), 2);
        tree.insert(new Shape(-5, 0, 1, 1), 3);
        tree.insert(new Shape(5, 2, 2, 2), 4);
        tree.insert(new Shape(5, 1, 3, 2), 5);
        tree.insert(new Shape(-5, -1, 1, 1), 6);
        tree.insert(new Shape(0, 1, 1, 1), 7);
        tree.insert(new Shape(5, 7, 2, 2), 8);
        tree.insert(new Shape(5, 7, 3, 2), 9);

        var found = tree.search(new Shape(-5, -1, 20, 4));
        assertArrayEquals(found.toArray(), new Object[]{6, 3, 7, 2, 5});
        found = tree.search(new Shape(-5, -1, 20, 5));
        assertArrayEquals(found.toArray(), new Object[]{6, 3, 7, 2, 5, 4});
    }

    @Test
    void simpleDelete() {
        RTree<Integer> tree = new RTree<>();
        tree.insert(new Shape(0, 0, 1, 1), 2);
        tree.insert(new Shape(1, 1, 2, 2), 3);
        assertTrue(tree.delete(new Shape(1, 1, 2, 2), 3));
    }

    @Test
    void deleteWithWrongRegion() {
        RTree<Integer> tree = new RTree<>();
        tree.insert(new Shape(0, 0, 1, 1), 2);
        tree.insert(new Shape(1, 1, 2, 2), 3);
        assertFalse(tree.delete(new Shape(5, 5, 2, 2), 3));
    }

    @Test
    void deleteDeep() {
        RTree<Integer> tree = new RTree<>(3, 1);
        tree.insert(new Shape(0, 0, 1, 1), 2);
        tree.insert(new Shape(-5, 0, 1, 1), 3);
        tree.insert(new Shape(5, 2, 2, 2), 4);
        tree.insert(new Shape(5, 1, 3, 2), 5);
        tree.insert(new Shape(-5, -1, 1, 1), 6);
        tree.insert(new Shape(0, 1, 1, 1), 7);
        tree.insert(new Shape(5, 7, 2, 2), 8);
        tree.insert(new Shape(5, 7, 3, 2), 9);

        assertTrue(tree.delete(new Shape(-5, -1, 20, 5), 4));
        assertTrue(tree.delete(new Shape(-5, -1, 20, 4), 5));
    }

    @Test
    void deleteWithReplaceRoot() {
        RTree<Integer> tree = new RTree<>(3, 1);
        tree.insert(new Shape(0, 0, 1, 1), 2);
        tree.insert(new Shape(-5, 0, 1, 1), 3);
        tree.insert(new Shape(5, 2, 2, 2), 4);
        tree.insert(new Shape(5, 1, 3, 2), 5);
        tree.insert(new Shape(-5, -1, 1, 1), 6);
        tree.insert(new Shape(0, 1, 1, 1), 7);
        tree.insert(new Shape(5, 7, 2, 2), 8);
        tree.insert(new Shape(5, 7, 3, 2), 9);

        assertTrue(tree.delete(new Shape(5, 2, 2, 2), 4));
        assertTrue(tree.delete(new Shape(5, 1, 3, 2), 5));

        var root = getRoot(tree);
        assertEquals(1, root.children.get(1).children.size());

        assertTrue(tree.delete(new Shape(5, 7, 2, 2), 8));
        assertTrue(tree.delete(new Shape(5, 7, 3, 2), 9));

        root = getRoot(tree);
        assertEquals(2, root.children.size());
        assertEquals(new Shape(-5, -1, 6, 3), root.shape);
    }

    @Test
    void deleteWithReinsert() {
        RTree<Integer> tree = new RTree<>(4, 2);
        tree.insert(new Shape(0, 0, 1, 1), 2);
        tree.insert(new Shape(1, 1, 2, 2), 3);
        tree.insert(new Shape(1, 2, 2, 2), 4);
        tree.insert(new Shape(1, 1, 3, 2), 5);
        tree.insert(new Shape(-1, -1, 1, 1), 6);

        assertTrue(tree.delete(new Shape(0, 0, 1, 1), 2));

        var root = getRoot(tree);
        assertEquals(4, root.children.size());
        root = getRoot(tree);
    }

}