package com.research.notes.services;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.research.notes.models.dtos.CreateQuestionRequest;
import com.research.notes.models.dtos.PageResponse;
import com.research.notes.models.dtos.QuestionResponse;
import com.research.notes.models.entities.QuestionEntity;
import com.research.notes.models.entities.UserEntity;
import com.research.notes.repositories.QuestionRepository;
import com.research.notes.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    @Transactional
    public QuestionResponse create(CreateQuestionRequest request) {
        UserEntity author = userRepository.findById(request.createdBy())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User %s not found".formatted(request.createdBy())));

        QuestionEntity question = QuestionEntity.builder()
                .question(request.question())
                .createdBy(author)
                .build();

        // flush so Hibernate populates the generated createdTime before mapping
        return QuestionResponse.from(questionRepository.saveAndFlush(question));
    }

    @Transactional(readOnly = true)
    public PageResponse<QuestionResponse> listByUser(UUID userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User %s not found".formatted(userId));
        }
        return PageResponse.from(questionRepository.findByCreatedById(userId, pageable).map(QuestionResponse::from));
    }

    /**
     * Internal lifecycle transitions; not reachable from the public API.
     */
    @Transactional
    public QuestionResponse markProcessing(UUID id) {
        QuestionEntity question = require(id);
        question.markProcessing();
        return QuestionResponse.from(questionRepository.saveAndFlush(question));
    }

    @Transactional
    public QuestionResponse complete(UUID id, String answer) {
        QuestionEntity question = require(id);
        question.markCompleted(answer);
        return QuestionResponse.from(questionRepository.saveAndFlush(question));
    }

    @Transactional
    public QuestionResponse fail(UUID id) {
        QuestionEntity question = require(id);
        question.markFailed();
        return QuestionResponse.from(questionRepository.saveAndFlush(question));
    }

    @Transactional(readOnly = true)
    public QuestionResponse get(UUID id) {
        return QuestionResponse.from(require(id));
    }

    private QuestionEntity require(UUID id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Question %s not found".formatted(id)));
    }
}
