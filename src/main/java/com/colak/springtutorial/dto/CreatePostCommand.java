package com.colak.springtutorial.dto;

import java.util.List;
import java.util.UUID;

public record CreatePostCommand(String title, String content, List<UUID> tagId) {
}
