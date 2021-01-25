package com.mirotest.mirotest_server.app;

import com.mirotest.mirotest_server.datasources.IWidgetDataSource;
import com.mirotest.mirotest_server.datasources.inmem.InMemoryWidgetSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MirotestServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MirotestServerApplication.class, args);
    }

    @Bean
    public IWidgetDataSource getDataSource() {
        return new InMemoryWidgetSource();
    }

}
