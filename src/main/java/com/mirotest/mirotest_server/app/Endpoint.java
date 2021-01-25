package com.mirotest.mirotest_server.app;

import com.mirotest.mirotest_server.common.PageInfo;
import com.mirotest.mirotest_server.common.Shape;

public class Endpoint {
    PageInfo pageInfo;
    Shape filter;

    public void setFilter(Shape filter) {
        this.filter = filter;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }
}
