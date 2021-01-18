package com.mirotest.mirotest_server.datasources.inmem;

import com.mirotest.mirotest_server.PageInfo;
import com.mirotest.mirotest_server.Widget;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeMap;

class ConsecutiveWidgets {
    int first=0, last=-1;
    LinkedList<Widget> widgets = new LinkedList<>();

    public ConsecutiveWidgets(Widget widget) {
        widgets.add(widget);
        first = widget.zIndex;
        last = widget.zIndex;
    }

    public ConsecutiveWidgets() {}

    public void add(Widget widget) {
        if (widgets.isEmpty()) {
            widgets.add(widget);
            first = widget.zIndex;
            last = widget.zIndex;
            return;
        }
        if (widget.zIndex == first - 1) {
            widgets.addFirst(widget);
            first--;
        } else if (widget.zIndex == last + 1) {
            widgets.add(widget);
            last++;
        } else {
            widgets.add(widget.zIndex - first, widget);
            var iter = widgets.listIterator(widget.zIndex - first + 1);
            long time = System.currentTimeMillis();
            iter.forEachRemaining(widget1 -> {
                widget1.zIndex++;
                widget1.lastModificationTime.setTime(time);
            });
            last++;
        }
    }

    public Integer lastIndex() {
        return last;
    }

    public Integer firstIndex() {
        return first;
    }

    public void merge(ConsecutiveWidgets consWidgets) {
        widgets.addAll(consWidgets.widgets);
        last = consWidgets.lastIndex();
    }

    public Widget[] toArray() {
        return widgets.toArray(new Widget[0]);
    }

    public Collection<Widget> toCollection() {
        return widgets;
    }

    public ConsecutiveWidgets remove(Widget widget) throws Exception {
        if (widget.zIndex == first) {
            if (widget != widgets.getFirst())
                throw new Exception("widget not found");
            widgets.removeFirst();
            first++;
            return null;
        }
        if (widget.zIndex == last) {
            if (widget != widgets.getLast())
                throw new Exception("widget not found");
            widgets.removeLast();
            last--;
            return null;
        }
        var resultElements = new LinkedList<>(widgets.subList(widget.zIndex - first + 1, widgets.size()));
        widgets.subList(widget.zIndex - first, widgets.size()).clear();
        last = widgets.getLast().zIndex;
        var cons = new ConsecutiveWidgets();
        cons.widgets = resultElements;
        cons.first = resultElements.getFirst().zIndex;
        cons.last = resultElements.getLast().zIndex;
        return cons;
    }

}

public class SortedZWidgets {
    private TreeMap<Integer, ConsecutiveWidgets> data = new TreeMap<>();
    private int totalCount;

    public void add(Widget widget) {
        totalCount++;
        Integer floorKey = data.floorKey(widget.zIndex);
        if (floorKey != null) {
            var curCons = data.get(floorKey);
            if (widget.zIndex <= curCons.lastIndex() + 1 &&
                    widget.zIndex >= curCons.firstIndex()) {
                curCons.add(widget);

                var upperKey = data.ceilingKey(curCons.lastIndex() + 1);
                if (upperKey != null) {
                    var upperCons = data.get(upperKey);
                    if (upperCons.firstIndex() - 1 == curCons.lastIndex()) {
                        curCons.merge(upperCons);
                        data.remove(upperKey);
                    }
                }
                return;
            }
        }
        Integer ceilingKey = data.ceilingKey(widget.zIndex);
        if (ceilingKey != null) {
            var curCons = data.get(ceilingKey);
            if (curCons.firstIndex() - 1 == widget.zIndex) {
                curCons.add(widget);

                var lowerKey = data.floorKey(ceilingKey - 1);
                if (lowerKey != null) {
                    var lowerCons = data.get(lowerKey);
                    if (lowerCons.lastIndex() + 1 == curCons.firstIndex()) {
                        lowerCons.merge(curCons);
                        data.remove(ceilingKey);
                    }
                }

                return;
            }
        }

        data.put(widget.zIndex, new ConsecutiveWidgets(widget));
    }

    public boolean remove(Widget widget) {
        var curEntry = data.floorEntry(widget.zIndex);
        if (curEntry != null) {
            if (curEntry.getValue().lastIndex() >= widget.zIndex) {
                int oldFirst = curEntry.getValue().firstIndex();
                ConsecutiveWidgets newCons;
                try {
                    newCons = curEntry.getValue().remove(widget);
                }
                catch (Exception e) {
                    return false;
                }
                if (newCons != null) {
                    data.put(newCons.firstIndex(), newCons);
                }
                else if (oldFirst != curEntry.getValue().firstIndex()) {
                    var cons = curEntry.getValue();
                    data.remove(curEntry.getKey());
                    data.put(cons.firstIndex(), cons);
                }
                totalCount--;
                return true;
            }
        }
        return false;
    }

    public Collection<Widget> toCollection() {
        var result = new ArrayList<Widget>();
        result.ensureCapacity(totalCount);
        data.forEach((integer, consecutiveWidgets) -> result.addAll(consecutiveWidgets.toCollection()));
        return result;
    }

    public Collection<Widget> toCollection(@NonNull PageInfo pageInfo) {
        int startIndex = (pageInfo.currentPage - 1) * pageInfo.itemsPerPage;
        int endIndex = startIndex + pageInfo.itemsPerPage;
        var result = new ArrayList<Widget>();
        result.ensureCapacity(pageInfo.itemsPerPage);
        int passed = 0;
        for (var entry : data.entrySet()) {
            if (passed + entry.getValue().toCollection().size() <= startIndex) {
                passed += entry.getValue().toCollection().size();
                continue;
            }

            for (var widget : entry.getValue().toCollection()) {
                if (passed >= startIndex && passed < endIndex) {
                    result.add(widget);
                }
                passed++;
            }
        }
        return result;
    }
}
