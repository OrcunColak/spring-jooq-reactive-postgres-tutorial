package com.colak.springtutorial.jpa;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table(value = "hash_tags")
@Getter
@Setter
public class HashTag {

    @Id
    @Column("id")
    private UUID id;

    @Column("name")
    private String name;
}
