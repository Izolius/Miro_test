package com.mirotest.mirotest_server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;

@RestController
public class WidgetController {

    private WidgetDesk widgetDesk;
    WidgetController(@Autowired WidgetDesk widgetDesk) {
        this.widgetDesk = widgetDesk;
    }
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Widget createWidget(@RequestBody CreateWidgetParams request) {
        return widgetDesk.addWidget(new Widget(request));
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Collection<Widget> widgetList() {
        return widgetDesk.getWidgets();
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
