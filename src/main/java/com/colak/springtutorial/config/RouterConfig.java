package com.colak.springtutorial.config;

import com.colak.springtutorial.handler.PostHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
class RouterConfig {

    @Bean
    RouterFunction<ServerResponse> routerFunction(PostHandler handler) {
        return RouterFunctions.route()
                .GET("/posts", handler::getAll)
                .POST("/posts", handler::create)
                .build();
    }
}
