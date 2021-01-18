package com.mirotest.mirotest_server.datasources;

import com.mirotest.mirotest_server.Widget;
import com.mirotest.mirotest_server.WidgetChanges;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

public interface IWidgetDataSource {
    @NonNull
    Widget addWidget(Widget widget);
    @Nullable
    Widget changeWidget(UUID id, @NonNull WidgetChanges changes);
    boolean deleteWidget(UUID id);
    @NonNull
    Collection<Widget> getSortedZWidgets();
}
