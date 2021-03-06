package com.mirotest.mirotest_server.datasources.inmem;

import com.mirotest.mirotest_server.common.Widget;

import java.util.*;

public class SortedZWidgets {
    final private LinkedList<Widget> sortedWidgets = new LinkedList<>();

    public void add(Widget widget) {
        if (widget.zIndex == null) {
            int nextIndex = 0;
            if (sortedWidgets.size() > 0) {
                nextIndex = sortedWidgets.getLast().zIndex + 1;
            }
            widget.zIndex = nextIndex;
            sortedWidgets.add(widget);
            return;
        }
        int insertIndex = Collections.binarySearch(sortedWidgets, widget, Comparator.comparingInt(o -> o.zIndex));
        if (insertIndex >= 0) {
            var iter = sortedWidgets.listIterator(insertIndex);
            iter.add(widget);
            shift(iter, widget);
        } else {
            insertIndex = -insertIndex - 1;
            var iter = sortedWidgets.listIterator(insertIndex);
            int curZ = widget.zIndex;
            while(iter.hasPrevious()) {
                var prev = iter.previous();
                if (prev.zIndex != curZ) {
                    iter.next();
                    break;
                }
            }
            iter.add(widget);
            shift(iter, widget);
        }
    }

    private void shift(ListIterator<Widget> iter, Widget widget) {
        int curZ = widget.zIndex;
        while(iter.hasNext()) {
            var next = iter.next();
            if (next.zIndex == curZ) {
                next.zIndex++;
                curZ++;
            } else {
                break;
            }
        }
    }

    public boolean remove(Widget widget) {
        int index = Collections.binarySearch(sortedWidgets, widget, Comparator.comparingInt(o -> o.zIndex));
        if (index < 0)
            return false;
        if (sortedWidgets.get(index) != widget)
            return false;
        sortedWidgets.remove(index);
        return true;
    }

    public List<Widget> toList() {
        return sortedWidgets;
    }
}
