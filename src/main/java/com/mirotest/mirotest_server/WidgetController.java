package com.mirotest.mirotest_server;

import com.mirotest.mirotest_server.common.PageInfo;
import com.mirotest.mirotest_server.common.Widget;
import com.mirotest.mirotest_server.common.WidgetChanges;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;

@RestController
public class WidgetController {

    private final WidgetDesk widgetDesk;
    WidgetController(@Autowired WidgetDesk widgetDesk) {
        this.widgetDesk = widgetDesk;
    }
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Widget createWidget(@RequestBody CreateWidgetParams request) {
        return widgetDesk.addWidget(new Widget(request));
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Collection<Widget> widgetList(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "itemsPerPage", required = false) Integer itemsPerPage) {
        if (page == null && itemsPerPage == null)
            return widgetDesk.getWidgets();
        else {
            PageInfo pageInfo = new PageInfo();
            if (page != null)
                pageInfo.currentPage = page;
            if (itemsPerPage != null)
                pageInfo.itemsPerPage = itemsPerPage;
            return widgetDesk.getWidgets(pageInfo);
        }
    }

    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public Widget getWidget(@RequestParam("id") UUID id) {
        return widgetDesk.getWidget(id);
    }

    @PostMapping("/remove")
    public void removeWidget(@RequestParam("id") UUID id) {
        widgetDesk.removeWidget(id);
    }

    @GetMapping(value = "/change", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Widget changeWidget(@RequestParam("id") UUID id, @RequestBody WidgetChanges changes) {
        return widgetDesk.changeWidget(id, changes);
    }
}
