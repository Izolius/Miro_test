package com.mirotest.mirotest_server.datasources;

import com.mirotest.mirotest_server.PageInfo;
import com.mirotest.mirotest_server.Widget;
import com.mirotest.mirotest_server.WidgetChanges;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.UUID;

public interface IWidgetDataSource {
    @NonNull
    Widget addWidget(Widget widget);

    @Nullable
    Widget changeWidget(UUID id, @NonNull WidgetChanges changes);

    boolean deleteWidget(UUID id);

    @NonNull
    Collection<Widget> getSortedZWidgets(PageInfo pageInfo);

    @NonNull
    Collection<Widget> getSortedZWidgets();

    Widget getWidget(UUID id);
}
