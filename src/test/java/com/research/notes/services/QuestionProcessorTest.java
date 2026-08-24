package com.research.notes.services;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.research.notes.models.dtos.QuestionResponse;
import com.research.notes.models.entities.QuestionStatus;

@ExtendWith(MockitoExtension.class)
class QuestionProcessorTest {

    @Mock
    private QuestionService questionService;

    @Mock
    private AnswerGenerator answerGenerator;

    @InjectMocks
    private QuestionProcessor questionProcessor;

    @Test
    void processingMarksProcessingThenCompletesWithGeneratedAnswer() {
        UUID id = UUID.randomUUID();
        when(questionService.markProcessing(id)).thenReturn(processing(id));
        when(answerGenerator.generate("Why is the sky blue?")).thenReturn("Rayleigh scattering");

        questionProcessor.process(id);

        InOrder inOrder = Mockito.inOrder(questionService);
        inOrder.verify(questionService).markProcessing(id);
        inOrder.verify(questionService).complete(id, "Rayleigh scattering");
        verify(questionService, never()).fail(id);
    }

    @Test
    void generatorFailureMarksQuestionFailed() {
        UUID id = UUID.randomUUID();
        when(questionService.markProcessing(id)).thenReturn(processing(id));
        when(answerGenerator.generate("Why is the sky blue?")).thenThrow(new IllegalStateException("boom"));

        questionProcessor.process(id);

        verify(questionService).fail(id);
        verify(questionService, never()).complete(Mockito.any(), Mockito.any());
    }

    private QuestionResponse processing(UUID id) {
        return new QuestionResponse(id, "Why is the sky blue?", QuestionStatus.PROCESSING, null,
                UUID.randomUUID(), null);
    }
}
