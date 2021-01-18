package com.mirotest.mirotest_server.datasources.inmem;

import com.mirotest.mirotest_server.Widget;
import com.mirotest.mirotest_server.WidgetChanges;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WidgetChangesApplierTest {

    @Test
    void withoutChanges() {
        var widget = new Widget();
        var changes = new WidgetChanges();
        var creationDate = widget.lastModificationTime;

        WidgetChangesApplier.applyChanges(widget, changes);

        assertEquals(creationDate, widget.lastModificationTime);
    }

    @Test
    void applyIndex() {
        var widget = new Widget();
        var changes = new WidgetChanges();
        changes.zIndex = 2;
        var creationDate = widget.lastModificationTime.getTime();
        try {
            // or time won't change
            Thread.sleep(1);
        }
        catch (Exception e){

        }

        WidgetChangesApplier.applyChanges(widget, changes);

        assertEquals(widget.zIndex, 2);
        assertNotEquals(widget.lastModificationTime.getTime(), creationDate);
    }
}