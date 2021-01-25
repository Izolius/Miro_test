package com.mirotest.mirotest_server.datasources.inmem;

import com.mirotest.mirotest_server.common.PageInfo;
import com.mirotest.mirotest_server.common.Paginator;
import com.mirotest.mirotest_server.common.Widget;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SortedZWidgetsTest {

    void assertWs(SortedZWidgets ws, Object[] arr) {
        assertArrayEquals(arr, ws.toList().stream().map(w -> w.zIndex).toArray());
    }

    @Test
    void Sample1() {
        var ws = new SortedZWidgets();
        ws.add(new Widget(1));
        ws.add(new Widget(2));
        ws.add(new Widget(3));

        ws.add(new Widget(2));

        assertWs(ws, new Object[]{1,2,3,4});
    }

    @Test
    void Sample2() {
        var ws = new SortedZWidgets();
        ws.add(new Widget(1));
        ws.add(new Widget(5));
        ws.add(new Widget(6));

        ws.add(new Widget(2));

        assertWs(ws, new Object[]{1,2,5,6});
    }

    @Test
    void Sample3() {
        var ws = new SortedZWidgets();
        ws.add(new Widget(1));
        ws.add(new Widget(2));
        ws.add(new Widget(4));

        ws.add(new Widget(2));

        assertWs(ws, new Object[]{1,2,3,4});
    }

    @Test
    void addInReverseOrder() {
        var ws = new SortedZWidgets();
        ws.add(new Widget(5));
        ws.add(new Widget(3));
        ws.add(new Widget(1));

        ws.add(new Widget(0));

        assertWs(ws, new Object[]{0,1,3,5});
    }

    @Test
    void addRemoveLast() {
        var ws = new SortedZWidgets();
        var widget = new Widget();
        ws.add(widget);
        ws.remove(widget);

        assertWs(ws, new Object[0]);
    }

    @Test
    void mergeUpperWithLower() {
        var ws = new SortedZWidgets();
        ws.add(new Widget(5));
        ws.add(new Widget(3));
        ws.add(new Widget(4));

        assertWs(ws, new Object[]{3,4,5});
    }

    @Test
    void removeNotExistsSameZ() {
        var ws = new SortedZWidgets();
        ws.add(new Widget(5));
        ws.add(new Widget(3));

        assertFalse(ws.remove(new Widget(3)));
        assertWs(ws, new Object[]{3,5});
    }

    @Test
    void removeNotExistsDiffZ() {
        var ws = new SortedZWidgets();
        ws.add(new Widget(5));
        ws.add(new Widget(3));

        assertFalse(ws.remove(new Widget(4)));
        assertWs(ws, new Object[]{3,5});
    }

    @Test
    void addWithMerge() {
        var ws = new SortedZWidgets();
        ws.add(new Widget(5));
        ws.add(new Widget(3));

        ws.add(new Widget(4));
        assertWs(ws, new Object[]{3,4,5});
    }

    @Test
    void removeWithSplit() {
        var ws = new SortedZWidgets();
        ws.add(new Widget(5));
        var mid = new Widget(4);
        ws.add(new Widget(3));
        ws.add(mid);

        ws.remove(mid);

        assertWs(ws, new Object[]{3,5});
    }

    private static SortedZWidgets createWsForPagination() {
        var ws = new SortedZWidgets();
        ws.add(new Widget(0));
        ws.add(new Widget(1));
        ws.add(new Widget(2));

        ws.add(new Widget(4));
        ws.add(new Widget(5));
        ws.add(new Widget(6));
        ws.add(new Widget(7));

        ws.add(new Widget(9));
        ws.add(new Widget(10));
        return ws;
    }

    @Test
    void paginationFirstBlock() {
        var ws = createWsForPagination();
        var pageInfo = new PageInfo();
        pageInfo.itemsPerPage =2;
        pageInfo.currentPage=1;
        assertArrayEquals(new Object[]{0,1}, Paginator.paginate(ws.toList(), pageInfo).stream().map(w -> w.zIndex).toArray());
    }

    @Test
    void paginationInsideBlock() {
        var ws = createWsForPagination();
        var pageInfo = new PageInfo();
        pageInfo.itemsPerPage =2;
        pageInfo.currentPage=3;
        assertArrayEquals(new Object[]{5,6}, Paginator.paginate(ws.toList(), pageInfo).stream().map(w -> w.zIndex).toArray());
    }

    @Test
    void paginationTwoBlocks() {
        var ws = createWsForPagination();
        var pageInfo = new PageInfo();
        pageInfo.itemsPerPage =2;
        pageInfo.currentPage=2;
        assertArrayEquals(new Object[]{2,4}, Paginator.paginate(ws.toList(), pageInfo).stream().map(w -> w.zIndex).toArray());
    }

    @Test
    void paginationThreeBlocks() {
        var ws = createWsForPagination();
        var pageInfo = new PageInfo();
        pageInfo.itemsPerPage =8;
        pageInfo.currentPage=1;
        assertArrayEquals(new Object[]{0,1,2,4,5,6,7,9}, Paginator.paginate(ws.toList(), pageInfo).stream().map(w -> w.zIndex).toArray());
    }

    @Test
    void notFullLastPage() {
        var ws = createWsForPagination();
        var pageInfo = new PageInfo();
        pageInfo.itemsPerPage =4;
        pageInfo.currentPage=3;
        assertArrayEquals(new Object[]{10}, Paginator.paginate(ws.toList(), pageInfo).stream().map(w -> w.zIndex).toArray());
    }
}