package com.colak.springtutorial.repository;

import com.colak.springtutorial.jpa.PostTagRelation;

import java.util.UUID;

interface PostTagRelationRepository extends R2dbcRepository<PostTagRelation, UUID> {
}
