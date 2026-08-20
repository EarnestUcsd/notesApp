package com.research.notes.controllers;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.research.notes.models.entities.UserEntity;
import com.research.notes.repositories.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createdQuestionIsPendingAndStatusIsReturnedOnGet() throws Exception {
        UUID authorId = createUser().getId();

        MvcResult created = mockMvc.perform(post("/v1/question")
                .contentType("application/json")
                .content("""
                        {"question":"Why is the sky blue?","createdBy":"%s"}
                        """.formatted(authorId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andReturn();

        String id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(get("/v1/question/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("PENDING")));
    }

    @Test
    void listedQuestionsIncludeStatus() throws Exception {
        UUID authorId = createUser().getId();

        mockMvc.perform(post("/v1/question")
                .contentType("application/json")
                .content("""
                        {"question":"Why is the sea salty?","createdBy":"%s"}
                        """.formatted(authorId)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/v1/user/{userId}/questions", authorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status", is("PENDING")));
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
