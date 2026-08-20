package com.research.notes.models.dtos;

import com.research.notes.models.entities.QuestionEntity;
import com.research.notes.models.entities.QuestionStatus;

import java.time.Instant;
import java.util.UUID;

public record QuestionResponse(
        UUID id,
        String question,
        QuestionStatus status,
        UUID createdBy,
        Instant createdTime) {

    public static QuestionResponse from(QuestionEntity question) {
        return new QuestionResponse(
                question.getId(),
                question.getQuestion(),
                question.getStatus(),
                question.getCreatedBy().getId(),
                question.getCreatedTime());
    }
}
