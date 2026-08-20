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

    @Transactional(readOnly = true)
    public QuestionResponse get(UUID id) {
        return questionRepository.findById(id)
                .map(QuestionResponse::from)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Question %s not found".formatted(id)));
    }
}
