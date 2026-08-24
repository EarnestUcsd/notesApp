package com.research.notes.services;

import java.util.UUID;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.research.notes.models.dtos.QuestionResponse;
import com.research.notes.models.events.QuestionCreatedEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * In-process background processing; the created question is only picked up once
 * its transaction has committed.
 */
@Service
@AllArgsConstructor
@Slf4j
public class QuestionProcessor {

    private final QuestionService questionService;
    private final AnswerGenerator answerGenerator;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onQuestionCreated(QuestionCreatedEvent event) {
        process(event.questionId());
    }

    public void process(UUID questionId) {
        try {
            QuestionResponse question = questionService.markProcessing(questionId);
            questionService.complete(questionId, answerGenerator.generate(question.question()));
        } catch (Exception e) {
            log.error("Processing question {} failed", questionId, e);
            markFailed(questionId);
        }
    }

    private void markFailed(UUID questionId) {
        try {
            questionService.fail(questionId);
        } catch (Exception e) {
            log.error("Could not mark question {} as failed", questionId, e);
        }
    }
}
