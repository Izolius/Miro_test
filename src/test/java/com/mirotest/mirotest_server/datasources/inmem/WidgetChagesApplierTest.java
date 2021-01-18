package com.mirotest.mirotest_server.datasources.inmem;

import com.mirotest.mirotest_server.Widget;
import com.mirotest.mirotest_server.WidgetChanges;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WidgetChagesApplierTest {

    @Test
    void withoutChanges() {
        var widget = new Widget();
        var changes = new WidgetChanges();
        var creationDate = widget.lastModificationTime;

        WidgetChagesApplier.applyChanges(widget, changes);

        assertEquals(creationDate, widget.lastModificationTime);
    }

    @Test
    void applyIndex() {
        var widget = new Widget();
        var changes = new WidgetChanges();
        changes.zIndex = 2;
        var creationDate = widget.lastModificationTime;
        try {
            // or time won't change
            Thread.sleep(1000);
        }
        catch (Exception e){

        }

        WidgetChagesApplier.applyChanges(widget, changes);

        assertEquals(widget.zIndex, 2);
        assertNotEquals(widget.lastModificationTime, creationDate);
    }
}