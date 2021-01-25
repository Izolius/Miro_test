package com.mirotest.mirotest_server.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mirotest.mirotest_server.app.CreateWidgetParams;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.awt.*;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
class WidgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createWidget() throws Exception {
        var req = new CreateWidgetParams();
        req.coord = new Point(1,2);
        req.zIndex = 1;
        req.height = 2;
        req.width = 3;

        this.mockMvc.perform(post("/create").content(asJsonString(req)).contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json("{zIndex = 1, height = 2, width = 3, coord={x=1, y=2}}", false));
    }

    @Test
    void pagination() throws Exception {
        var req = new CreateWidgetParams();
        req.coord = new Point(1,2);
        req.zIndex = 1;
        req.height = 2;
        req.width = 3;

        mockMvc.perform(post("/create").content(asJsonString(req)).contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(post("/create").content(asJsonString(req)).contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(post("/create").content(asJsonString(req)).contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/list").param("page","2").param("itemsPerPage","2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[{zIndex=3}]", false));
    }

    //TODO: and other
}