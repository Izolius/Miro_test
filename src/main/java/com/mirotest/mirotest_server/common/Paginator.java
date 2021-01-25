package com.mirotest.mirotest_server.common;

import java.util.ArrayList;
import java.util.List;

public class Paginator {
    public static List<Widget> paginate(List<Widget> widgets, PageInfo pageInfo) {
        int startIndex = (pageInfo.currentPage - 1) * pageInfo.itemsPerPage;
        int endIndex = Math.min(startIndex + pageInfo.itemsPerPage, widgets.size());
        return new ArrayList<>(widgets.subList(startIndex, endIndex));
    }
}
