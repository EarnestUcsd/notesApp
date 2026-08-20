package com.research.notes.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.research.notes.models.dtos.CreateQuestionRequest;
import com.research.notes.models.dtos.QuestionResponse;
import com.research.notes.models.entities.QuestionEntity;
import com.research.notes.models.entities.QuestionStatus;
import com.research.notes.models.entities.UserEntity;
import com.research.notes.repositories.QuestionRepository;
import com.research.notes.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private QuestionService questionService;

    @Test
    void createDefaultsStatusToPending() {
        UserEntity author = UserEntity.builder().id(UUID.randomUUID()).build();
        when(userRepository.findById(author.getId())).thenReturn(Optional.of(author));
        when(questionRepository.saveAndFlush(any(QuestionEntity.class))).thenAnswer(i -> i.getArgument(0));

        QuestionResponse response = questionService.create(
                new CreateQuestionRequest("Why is the sky blue?", author.getId()));

        ArgumentCaptor<QuestionEntity> saved = ArgumentCaptor.forClass(QuestionEntity.class);
        org.mockito.Mockito.verify(questionRepository).saveAndFlush(saved.capture());
        assertThat(saved.getValue().getStatus()).isEqualTo(QuestionStatus.PENDING);
        assertThat(response.status()).isEqualTo(QuestionStatus.PENDING);
    }

    @Test
    void getReturnsStatus() {
        UserEntity author = UserEntity.builder().id(UUID.randomUUID()).build();
        QuestionEntity question = QuestionEntity.builder()
                .id(UUID.randomUUID())
                .question("Why is the sky blue?")
                .status(QuestionStatus.PROCESSING)
                .createdBy(author)
                .build();
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));

        assertThat(questionService.get(question.getId()).status()).isEqualTo(QuestionStatus.PROCESSING);
    }

    @Test
    void createRejectsUnknownUser() {
        UUID unknown = UUID.randomUUID();
        when(userRepository.findById(unknown)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> questionService.create(new CreateQuestionRequest("q", unknown)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404");
    }
}
