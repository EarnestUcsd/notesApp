package com.research.notes.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateQuestionRequest(
        @NotBlank @Size(max = 1000) String question,
        @NotNull UUID createdBy) {
}
