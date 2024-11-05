package com.colak.springtutorial.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {
    private final DSLContext dslContext;

    @EventListener(classes = ApplicationReadyEvent.class)
    public void init() {
        Mono.from(dslContext.insertInto(POSTS)
                        .columns(POSTS.TITLE, POSTS.CONTENT)
                        .values("jooq test", "content of Jooq test")
                        .returningResult(POSTS.ID)
                )
                .flatMapMany(id -> dslContext.insertInto(COMMENTS)
                        .columns(COMMENTS.POST_ID, COMMENTS.CONTENT)
                        .values(id.component1(), "test comments")
                        .values(id.component1(), "test comments 2")
                )
                .flatMap(it -> dslContext.select(POSTS.TITLE,
                                        POSTS.CONTENT,
                                        multiset(select(COMMENTS.CONTENT)
                                                .from(COMMENTS)
                                                .where(COMMENTS.POST_ID.eq(POSTS.ID))
                                        ).as("comments")
                                )
                                .from(POSTS)
                                .orderBy(POSTS.CREATED_AT)

                )
                .subscribe(
                        data -> log.debug("saving data: {}", data.formatJSON()),
                        error -> log.debug("error: " + error),
                        () -> log.debug("done")
                );
    }
}
