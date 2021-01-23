package com.mirotest.mirotest_server;

import com.mirotest.mirotest_server.common.PageInfo;
import com.mirotest.mirotest_server.common.Shape;
import com.mirotest.mirotest_server.common.WidgetChanges;
import com.mirotest.mirotest_server.datasources.IWidgetDataSource;
import com.mirotest.mirotest_server.common.Widget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

@Component
public class WidgetDesk {

    private final IWidgetDataSource widgets;

    public WidgetDesk(@Autowired IWidgetDataSource widgets) {
        this.widgets = widgets;
    }
    /**
     *
     * @param widget widget to add
     * @return changed version of same @param
     */
    @NonNull
    public Widget addWidget(Widget widget) {
        return widgets.addWidget(widget);
    }

    public boolean removeWidget(UUID id) {
        return widgets.deleteWidget(id);
    }

    /**
     *
     * @param id id of widget to change
     * @param changes new fields for widget
     * @return changed version of widget or null if widget doesn't exist
     */
    @Nullable
    public Widget changeWidget(UUID id, WidgetChanges changes) {
        return widgets.changeWidget(id, changes);
    }

    @NonNull
    public Collection<Widget> getWidgets() {
        return widgets.getSortedZWidgets();
    }

    @NonNull
    public Collection<Widget> getWidgets(Shape filter) {
        return widgets.getSortedZWidgets(filter);
    }

    @NonNull
    public Collection<Widget> getWidgets(PageInfo pageInfo) {
        return widgets.getSortedZWidgets(pageInfo);
    }

    @Nullable
    public Widget getWidget(UUID id) {
        return widgets.getWidget(id);
    }


}
