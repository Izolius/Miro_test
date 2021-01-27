package com.mirotest.mirotest_server.datasources.inmem;

import com.mirotest.mirotest_server.common.Widget;
import com.mirotest.mirotest_server.common.WidgetChanges;
import com.mirotest.mirotest_server.common.WrongWidgetField;

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
            if (widget.width <= 0) {
                throw new WrongWidgetField("Widget width have to be positive");
            }
            wasChanged = true;
        }
        if (changes.height != null) {
            widget.height = changes.height;
            if (widget.height <= 0) {
                throw new WrongWidgetField("Widget height have to be positive");
            }
            wasChanged = true;
        }
        if (wasChanged)
            widget.lastModificationTime.setTime(System.currentTimeMillis());
    }
}
