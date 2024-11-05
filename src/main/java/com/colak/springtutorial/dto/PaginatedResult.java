package com.colak.springtutorial.dto;

import java.util.List;

public record PaginatedResult(List<?> data, Long count) {
}
