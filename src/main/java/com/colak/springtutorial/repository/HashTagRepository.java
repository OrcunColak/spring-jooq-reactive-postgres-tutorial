package com.colak.springtutorial.repository;

import com.colak.springtutorial.jpa.HashTag;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

interface HashTagRepository extends R2dbcRepository<HashTag, UUID> {
}
