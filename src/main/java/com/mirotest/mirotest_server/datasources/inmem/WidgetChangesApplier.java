package com.mirotest.mirotest_server.datasources.inmem;

import com.mirotest.mirotest_server.Widget;
import com.mirotest.mirotest_server.WidgetChanges;

public class WidgetChangesApplier {

    public static void applyChanges(Widget widget, WidgetChanges changes) {
        boolean wasChanged = false;
        if (changes.coord != null) {
            widget.coord = changes.coord;
            wasChanged = true;
        }
        if (changes.zIndex != null) {
            widget.zIndex = changes.zIndex;
            wasChanged = true;
        }
        if (changes.width != null) {
            widget.width = changes.width;
            wasChanged = true;
        }
        if (changes.height != null) {
            widget.height = changes.height;
            wasChanged = true;
        }
        if (wasChanged)
            widget.lastModificationTime.setTime(System.currentTimeMillis());
    }
}
