package com.colak.springtutorial.service;

import com.colak.jooq.tables.records.PostsTagsRecord;
import com.colak.springtutorial.dto.CreatePostCommand;
import com.colak.springtutorial.dto.PaginatedResult;
import com.colak.springtutorial.dto.PostSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.UUID;

import static com.colak.jooq.Tables.HASH_TAGS;
import static com.colak.jooq.Tables.POSTS_TAGS;
import static com.colak.jooq.tables.Posts.POSTS;
import static org.jooq.impl.DSL.multiset;
import static org.jooq.impl.DSL.select;
import static org.jooq.meta.jaxb.RegexFlag.COMMENTS;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final DSLContext dslContext;

    public Flux<PostSummary> findAll() {
        var p = POSTS;
        var pt = POSTS_TAGS;
        var t = HASH_TAGS;
        var c = COMMENTS;
        var sql = dslContext.select(
                        p.ID,
                        p.TITLE,
                        DSL.field("count(comments.id)", SQLDataType.BIGINT),
                        multiset(select(t.NAME)
                                .from(t)
                                .join(pt).on(t.ID.eq(pt.TAG_ID))
                                .where(pt.POST_ID.eq(p.ID))
                        ).as("tags")
                )
                .from(p.leftJoin(c).on(c.POST_ID.eq(p.ID)))
                .groupBy(p.ID)
                .orderBy(p.CREATED_AT);
        return Flux.from(sql)
                .map(r -> new PostSummary(r.value1(), r.value2(), r.value3(), r.value4().map(Record1::value1)));
    }

    public Mono<PaginatedResult> findByKeyword(String keyword, int offset, int limit) {
        var p = POSTS;
        var pt = POSTS_TAGS;
        var t = HASH_TAGS;
        var c = COMMENTS;

        Condition where = DSL.trueCondition();
        if (StringUtils.hasText(keyword)) {
            where = where.and(p.TITLE.likeIgnoreCase("%" + keyword + "%"));
        }
        var dataSql = dslContext.select(
                        p.ID,
                        p.TITLE,
                        DSL.field("count(comments.id)", SQLDataType.BIGINT),
                        multiset(select(t.NAME)
                                .from(t)
                                .join(pt).on(t.ID.eq(pt.TAG_ID))
                                .where(pt.POST_ID.eq(p.ID))
                        ).as("tags")
                )
                .from(p.leftJoin(c).on(c.POST_ID.eq(p.ID)))
                .where(where)
                .groupBy(p.ID)
                .orderBy(p.CREATED_AT)
                .limit(offset, limit);

        val countSql = dslContext.select(DSL.field("count(*)", SQLDataType.BIGINT))
                .from(p)
                .where(where);

        return Mono
                .zip(
                        Flux.from(dataSql)
                                .map(r -> new PostSummary(r.value1(), r.value2(), r.value3(), r.value4().map(Record1::value1)))
                                .collectList(),
                        Mono.from(countSql)
                                .map(Record1::value1)
                )
                .map(it -> new PaginatedResult(it.getT1(), it.getT2()));
    }

    public Mono<UUID> create(CreatePostCommand data) {
        var p = POSTS;
        var pt = POSTS_TAGS;
        var sqlInsertPost = dslContext.insertInto(p)
                .columns(p.TITLE, p.CONTENT)
                .values(data.title(), data.content())
                .returningResult(p.ID);
        return Mono.from(sqlInsertPost)
                .flatMap(id -> {
                            Collection<?> tags = data.tagId().stream().map(tag -> {
                                PostsTagsRecord r = pt.newRecord();
                                r.setPostId(id.value1());
                                r.setTagId(tag);
                                return r;
                            }).toList();
                            return Mono.from(dslContext.insertInto(pt)
                                            .columns(pt.POST_ID, pt.TAG_ID)
                                            .values(tags)
                                    )
                                    .map(r -> {
                                        log.debug("inserted tags:: {}", r);
                                        return id;
                                    });
                        }
                )
                .map(Record1::value1);
    }
}
