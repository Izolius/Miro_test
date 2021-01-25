package com.mirotest.mirotest_server.datasources.inmem;

import com.mirotest.mirotest_server.common.*;
import com.mirotest.mirotest_server.datasources.IWidgetDataSource;
import com.mirotest.mirotest_server.datasources.inmem.rtree.RTree;
import org.springframework.lang.NonNull;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class InMemoryWidgetSource implements IWidgetDataSource {
    final private SortedZWidgets sortedZWidgets = new SortedZWidgets();
    final private HashMap<UUID, Widget> widgetsById = new HashMap<>();
    final private ReadWriteLock rwLock = new ReentrantReadWriteLock(true);
    final private RTree<Widget> rTree = new RTree<>(10, 3);

    @Override
    @NonNull
    public Widget addWidget(Widget widget) {
        rwLock.writeLock().lock();
        try {
            sortedZWidgets.add(widget);
            widgetsById.put(widget.id, widget);
            rTree.insert(widget, widget);
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
                rTree.delete(widget, widget);
                WidgetChangesApplier.applyChanges(widget, changes);
                rTree.insert(widget, widget);
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
                rTree.delete(widget, widget);
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
            return sortedZWidgets.toList();
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
            return Paginator.paginate(sortedZWidgets.toList(), pageInfo);
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

    @Override
    @NonNull
    public Collection<Widget> getSortedZWidgets(Shape filter) {
        rwLock.readLock().lock();
        try {
            // Time complexity is log(n) for filtering (O(n) in worse case)
            var result = rTree.search(filter);
            // Time complexity is m*log(m). Can be optimized to m*log(k), k<m if store sorted by Z-index widgets in RTree nodes
            result.sort(Comparator.comparingInt(o -> o.zIndex));
            return result;
        }
        finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public Collection<Widget> getSortedZWidgets(Shape filter, PageInfo pageInfo) {
        rwLock.readLock().lock();
        try {
            var result = rTree.search(filter);
            // Time complexity is m*log(m). Can be optimized to m*log(k), k<m. Price is memory
            // Sorting can be optimized with page Info (for first pages) if store sorted by Z-index widgets in RTree nodes, but in strange use case
            result.sort(Comparator.comparingInt(o -> o.zIndex));
            return Paginator.paginate(result, pageInfo);
        }
        finally {
            rwLock.readLock().unlock();
        }
    }


}
