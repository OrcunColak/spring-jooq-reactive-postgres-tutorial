package com.colak.springtutorial.handler;

import com.colak.springtutorial.dto.CreatePostCommand;
import com.colak.springtutorial.dto.PostSummary;
import com.colak.springtutorial.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.server.ServerResponse.created;


@Component
@RequiredArgsConstructor
public class PostHandler {
    private final PostService postService;

    public Mono<ServerResponse> getAll(ServerRequest req) {
        return ServerResponse.ok().body(this.postService.findAll(), PostSummary.class);
    }

    public Mono<ServerResponse> create(ServerRequest req) {
        return req.bodyToMono(CreatePostCommand.class)
                .flatMap(this.postService::create)
                .flatMap(id -> created(URI.create("/posts/" + id)).build());
    }

}
