package com.research.notes.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.research.notes.models.dtos.CreateQuestionRequest;
import com.research.notes.models.dtos.QuestionResponse;
import com.research.notes.models.entities.QuestionStatus;
import com.research.notes.models.entities.UserEntity;
import com.research.notes.repositories.QuestionRepository;
import com.research.notes.repositories.UserRepository;

/**
 * Deliberately not @Transactional: the processor only runs after the creating
 * transaction commits.
 */
@SpringBootTest
class QuestionProcessingIntegrationTest {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    private UUID authorId;

    @AfterEach
    void cleanUp() {
        if (authorId != null) {
            questionRepository.deleteAll(questionRepository.findByCreatedById(authorId,
                    org.springframework.data.domain.Pageable.unpaged()).getContent());
            userRepository.deleteById(authorId);
        }
    }

    @Test
    void createdQuestionIsAnsweredInTheBackground() {
        authorId = createUser().getId();

        QuestionResponse created = questionService.create(
                new CreateQuestionRequest("Why is the sky blue?", authorId));

        assertThat(created.status()).isEqualTo(QuestionStatus.PENDING);
        assertThat(created.answer()).isNull();

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            QuestionResponse processed = questionService.get(created.id());
            assertThat(processed.status()).isEqualTo(QuestionStatus.COMPLETED);
            assertThat(processed.answer()).contains("Why is the sky blue?");
        });
    }

    private UserEntity createUser() {
        String unique = UUID.randomUUID().toString().substring(0, 8);
        return userRepository.saveAndFlush(UserEntity.builder()
                .username("user-" + unique)
                .email(unique + "@example.com")
                .password("x")
                .build());
    }
}
