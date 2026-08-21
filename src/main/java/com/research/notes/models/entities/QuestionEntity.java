package com.research.notes.models.entities;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import java.util.UUID;
import java.sql.Types;
import java.time.Instant;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionEntity {

    @Id
    @GeneratedValue
    @JdbcTypeCode(Types.VARCHAR)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 1000)
    private String question;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private QuestionStatus status = QuestionStatus.PENDING;

    @Column(columnDefinition = "text")
    private String answer;

    // Foreign key to User (creator and modifier)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private UserEntity createdBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdTime;

    public void markProcessing() {
        requireStatus(QuestionStatus.PENDING);
        status = QuestionStatus.PROCESSING;
    }

    public void markCompleted(String answer) {
        if (answer == null || answer.isBlank()) {
            throw new IllegalArgumentException("A completed question requires an answer");
        }
        requireStatus(QuestionStatus.PENDING, QuestionStatus.PROCESSING);
        this.answer = answer;
        status = QuestionStatus.COMPLETED;
    }

    public void markFailed() {
        requireStatus(QuestionStatus.PENDING, QuestionStatus.PROCESSING);
        status = QuestionStatus.FAILED;
    }

    private void requireStatus(QuestionStatus... allowed) {
        for (QuestionStatus candidate : allowed) {
            if (status == candidate) {
                return;
            }
        }
        throw new IllegalStateException(
                "Question %s cannot transition from %s".formatted(id, status));
    }

}
