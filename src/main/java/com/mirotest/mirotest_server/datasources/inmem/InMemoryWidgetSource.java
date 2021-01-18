package com.mirotest.mirotest_server.datasources.inmem;

import com.mirotest.mirotest_server.PageInfo;
import com.mirotest.mirotest_server.Widget;
import com.mirotest.mirotest_server.WidgetChanges;
import com.mirotest.mirotest_server.datasources.IWidgetDataSource;
import org.springframework.lang.NonNull;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class InMemoryWidgetSource implements IWidgetDataSource {
    final private SortedZWidgets sortedZWidgets = new SortedZWidgets();
    final private HashMap<UUID, Widget> widgetsById = new HashMap<>();
    final private ReadWriteLock rwLock = new ReentrantReadWriteLock(true);

    @Override
    @NonNull
    public Widget addWidget(Widget widget) {
        rwLock.writeLock().lock();
        try {
            sortedZWidgets.add(widget);
            widgetsById.put(widget.id, widget);
            return widget;
        }
        finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public Widget changeWidget(UUID id, WidgetChanges changes) {
        rwLock.writeLock().lock();
        try {
            var widget = widgetsById.get(id);
            if (widget != null) {
                sortedZWidgets.remove(widget);
                WidgetChangesApplier.applyChanges(widget, changes);
                sortedZWidgets.add(widget);
                return widget;
            }
            return null;
        }
        finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public boolean deleteWidget(UUID id) {
        rwLock.writeLock().lock();
        try {
            var widget = widgetsById.get(id);
            if (widget != null) {
                sortedZWidgets.remove(widget);
                widgetsById.remove(widget.id);
                return true;
            }
            return false;
        }
        finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    @NonNull
    public Collection<Widget> getSortedZWidgets() {
        rwLock.readLock().lock();
        try {
            return sortedZWidgets.toCollection();
        }
        finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    @NonNull
    public Collection<Widget> getSortedZWidgets(PageInfo pageInfo) {
        rwLock.readLock().lock();
        try {
            return sortedZWidgets.toCollection(pageInfo);
        }
        finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public Widget getWidget(UUID id) {
        rwLock.readLock().lock();
        try {
            return widgetsById.get(id);
        }
        finally {
            rwLock.readLock().unlock();
        }
    }


}
