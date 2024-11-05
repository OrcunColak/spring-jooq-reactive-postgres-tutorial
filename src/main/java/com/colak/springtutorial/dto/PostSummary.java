package com.colak.springtutorial.dto;

import java.util.List;
import java.util.UUID;

public record PostSummary(UUID id, String title, Long countOfComments, List<String> tags) {
}
