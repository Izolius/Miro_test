package com.mirotest.mirotest_server;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.UUID;

@Component
public class WidgetDesk {
    /**
     *
     * @param widget widget to add
     * @return changed version of same @param
     */
    @NonNull
    public Widget addWidget(Widget widget) {
        return widget;
    }

    public boolean deleteWidget(UUID id) {
        return false;
    }

    /**
     *
     * @param id id of widget to change
     * @param newWidget new fields for widget
     * @return changed version of same @param
     */
    @NonNull
    public Widget changeWidget(UUID id, Widget newWidget) { // TODO: specify exception type
        return newWidget;
    }

    @NonNull
    public ArrayList<Widget> getWidgets() {
        return new ArrayList<>();
    }


}
