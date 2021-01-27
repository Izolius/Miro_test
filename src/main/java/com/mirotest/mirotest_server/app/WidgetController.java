package com.mirotest.mirotest_server.app;

import com.mirotest.mirotest_server.common.Widget;
import com.mirotest.mirotest_server.common.WidgetChanges;
import com.mirotest.mirotest_server.common.WrongWidgetField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.Collection;
import java.util.UUID;

@RestController
public class WidgetController {

    private final WidgetDesk widgetDesk;
    WidgetController(@Autowired WidgetDesk widgetDesk) {
        this.widgetDesk = widgetDesk;
    }
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Widget createWidget(@RequestBody CreateWidgetParams request) throws Exception {
        return widgetDesk.addWidget(new Widget(request));
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Collection<Widget> widgetList(@RequestBody Endpoint endpoint) {
        if (endpoint == null || endpoint.pageInfo == null && endpoint.filter == null)
            return widgetDesk.getWidgets();
        else {
            if (endpoint.pageInfo != null && endpoint.filter == null) {
                return widgetDesk.getWidgets(endpoint.pageInfo);
            } else if (endpoint.pageInfo == null) {
                return widgetDesk.getWidgets(endpoint.filter);
            } else {
                return widgetDesk.getWidgets(endpoint.filter, endpoint.pageInfo);
            }
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

    @PostMapping(value = "/change", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Widget changeWidget(@RequestParam("id") UUID id, @RequestBody WidgetChanges changes) {
        return widgetDesk.changeWidget(id, changes);
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<Object> handleAll(Exception ex) {
        if (ex instanceof WrongWidgetField) {
            return new ResponseEntity<>(ex.getLocalizedMessage(), HttpStatus.EXPECTATION_FAILED);
        }
        return new ResponseEntity<>(ex.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
