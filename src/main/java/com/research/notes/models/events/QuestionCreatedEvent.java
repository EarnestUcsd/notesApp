package com.research.notes.models.events;

import java.util.UUID;

public record QuestionCreatedEvent(UUID questionId) {
}
