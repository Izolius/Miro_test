package com.mirotest.mirotest_server.app;

import com.mirotest.mirotest_server.app.WidgetDesk;
import com.mirotest.mirotest_server.common.Shape;
import com.mirotest.mirotest_server.common.Widget;
import com.mirotest.mirotest_server.common.WidgetChanges;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;
// With more logic in WidgetDesk it make sense to test it
@SpringBootTest
class WidgetDeskTest {

    @Autowired
    private WidgetDesk desk;

    @Test
    void addWidget() {
        var first = new Widget();
        desk.addWidget(first);
        desk.addWidget(new Widget());
        assertEquals(1, first.zIndex);
    }

    @Test
    void changeWidget() {
        var first = new Widget();
        desk.addWidget(first);
        var changes = new WidgetChanges();
        changes.zIndex = 1;
        desk.changeWidget(first.id, changes);

        assertEquals(changes.zIndex, first.zIndex);
    }

    @Test
    void removeWidget() {
        var first = new Widget();
        desk.addWidget(first);
        assertTrue(desk.removeWidget(first.id));
    }

    @Test
    void getWidgets() {
        var first = new Widget();
        desk.addWidget(first);
        assertEquals(first, desk.getWidgets().toArray()[0]);
    }

    @Test
    void getWidget() {
        var first = new Widget(1);
        desk.addWidget(first);
        assertEquals(first, desk.getWidget(first.id));
    }

    @Test
    void getWidgetsWithFilter() {
        var first = new Widget();
        first.coord = new Point(1,1);
        first.width = 1;
        first.height = 1;
        desk.addWidget(first);

        var second = new Widget();
        second.coord = new Point(2,2);
        second.width = 1;
        second.height = 1;
        desk.addWidget(second);

        assertArrayEquals(new Object[]{second}, desk.getWidgets(new Shape(2,2,1,1)).toArray());
    }



}