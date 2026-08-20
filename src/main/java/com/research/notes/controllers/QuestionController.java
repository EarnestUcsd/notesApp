package com.research.notes.controllers;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.research.notes.models.dtos.CreateQuestionRequest;
import com.research.notes.models.dtos.PageResponse;
import com.research.notes.models.dtos.QuestionResponse;
import com.research.notes.services.QuestionService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@RequestMapping("/v1")
@Slf4j
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/question")
    public ResponseEntity<QuestionResponse> create(@Valid @RequestBody CreateQuestionRequest request) {
        QuestionResponse created = questionService.create(request);
        return ResponseEntity.created(URI.create("/v1/question/" + created.id())).body(created);
    }

    @GetMapping("/question/{id}")
    public ResponseEntity<QuestionResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(questionService.get(id));
    }

    @GetMapping("/user/{userId}/questions")
    public ResponseEntity<PageResponse<QuestionResponse>> listByUser(
            @PathVariable UUID userId,
            @PageableDefault(size = 20, sort = "createdTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(questionService.listByUser(userId, pageable));
    }
}
