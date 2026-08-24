package com.research.notes.services;

import org.springframework.stereotype.Component;

/**
 * Placeholder until questions are answered by an LLM.
 */
@Component
public class FakeAnswerGenerator implements AnswerGenerator {

    @Override
    public String generate(String question) {
        return "Researched answer for: %s".formatted(question);
    }
}
