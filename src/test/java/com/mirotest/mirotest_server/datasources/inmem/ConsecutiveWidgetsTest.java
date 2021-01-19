package com.mirotest.mirotest_server.datasources.inmem;

import com.mirotest.mirotest_server.Widget;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConsecutiveWidgetsTest {

    @Test
    void addSame() {
        var cons = new ConsecutiveWidgets();
        cons.add(new Widget());
        cons.add(new Widget());
        var widgets = cons.toArray();
        assertEquals(0, widgets[0].zIndex);
        assertEquals(1, widgets[1].zIndex);
    }

    @Test
    void addSameNegative() {
        var cons = new ConsecutiveWidgets();
        cons.add(new Widget(-1));
        cons.add(new Widget(-1));
        var widgets = cons.toArray();
        assertEquals(-1, widgets[0].zIndex);
        assertEquals(0, widgets[1].zIndex);
    }

    @Test
    void addSameTimeChanged() {
        var cons = new ConsecutiveWidgets();
        var first = new Widget();
        cons.add(first);
        var creationTime = first.lastModificationTime.getTime();
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cons.add(new Widget());
        assertNotEquals(creationTime, first.lastModificationTime.getTime());
    }

    @Test
    void addToEnd() {
        var cons = new ConsecutiveWidgets();
        cons.add(new Widget());
        cons.add(new Widget(1));
        var widgets = cons.toArray();
        assertEquals(0, widgets[0].zIndex);
        assertEquals(1, widgets[1].zIndex);
    }

    @Test
    void addToBegining() {
        var cons = new ConsecutiveWidgets();
        cons.add(new Widget(1));
        cons.add(new Widget(0));
        var widgets = cons.toArray();
        assertEquals(0, widgets[0].zIndex);
        assertEquals(1, widgets[1].zIndex);
    }

    @Test
    void removeFirst() throws Exception {
        var cons = new ConsecutiveWidgets();
        var first = new Widget(0);
        cons.add(first);
        cons.add(new Widget(1));
        cons.add(new Widget(2));
        assertNull(cons.remove(first));

        var widgets = cons.toArray();
        assertEquals(1, widgets[0].zIndex);
        assertEquals(2, widgets[1].zIndex);
    }

    @Test
    void removeLast() throws Exception {
        var cons = new ConsecutiveWidgets();
        var last = new Widget(2);
        cons.add(new Widget(0));
        cons.add(new Widget(1));
        cons.add(last);
        assertNull(cons.remove(last));

        var widgets = cons.toArray();
        assertEquals(0, widgets[0].zIndex);
        assertEquals(1, widgets[1].zIndex);
    }

    @Test
    void removeMid() throws Exception {
        var cons = new ConsecutiveWidgets();
        var mid = new Widget(1);
        cons.add(new Widget(0));
        cons.add(mid);
        cons.add(new Widget(2));
        var next = cons.remove(mid);
        assertNotNull(next);

        var widgets = cons.toArray();
        assertEquals(0, widgets[0].zIndex);

        widgets = next.toArray();
        assertEquals(2, widgets[0].zIndex);
    }

    @Test
    void merge() {
        var cons = new ConsecutiveWidgets();
        cons.add(new Widget(1));

        var cons2 = new ConsecutiveWidgets();
        cons2.add(new Widget(2));

        cons.merge(cons2);

        assertEquals(1, cons.firstIndex());
        assertEquals(2, cons.lastIndex());
        assertEquals(cons2.toArray()[0], cons.toArray()[1]);
    }

}