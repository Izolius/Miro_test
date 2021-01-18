package com.mirotest.mirotest_server;

import com.mirotest.mirotest_server.create_widget.createWidgetRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;

@RestController
public class WidgetController {

    private WidgetDesk widgetDesk;
    WidgetController(@Autowired WidgetDesk widgetDesk) {
        this.widgetDesk = widgetDesk;
    }
    @PostMapping(value = "/create", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Widget createWidget(@RequestBody createWidgetRequest request) {
        var widget = new Widget();
        widget.coord = request.coord;
        widget.width = request.width;
        widget.height = request.height;
        widget.zIndex = request.zIndex;

        return widgetDesk.addWidget(widget);
    }

    @GetMapping(value = "/list", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Collection<Widget> widgetList() {
        return widgetDesk.getWidgets();
    }
}
