package com.colak.springtutorial.repository;

import com.colak.springtutorial.jpa.Comment;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

interface CommentRepository extends R2dbcRepository<Comment, UUID> {
}
