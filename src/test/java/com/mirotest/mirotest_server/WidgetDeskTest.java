package com.mirotest.mirotest_server;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

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


}