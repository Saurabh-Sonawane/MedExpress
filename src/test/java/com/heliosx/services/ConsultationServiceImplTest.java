package com.heliosx.services;

import com.heliosx.model.AnswerRequest;
import com.heliosx.model.AnswerType;
import com.heliosx.model.ConsultationResultResponse;
import com.heliosx.model.ConsultationStatus;
import com.heliosx.model.Question;
import com.heliosx.model.QuestionsResponse;
import com.heliosx.model.SubmitAnswersRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultationServiceImplTest {

    @Mock
    private ConsultationEvaluator evaluator;

    @InjectMocks
    private ConsultationServiceImpl service;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    void getQuestions_shouldReturnDefaultQuestions() {
        QuestionsResponse response = service.getQuestions();

        assertNotNull(response);
        assertEquals(6, response.getQuestions().size());

        Question q1 = response.getQuestions().get(0);
        assertEquals(1, q1.getId());
        assertEquals(AnswerType.NUMBER, q1.getType());
    }

    @Test
    void submitAnswers_shouldReturnEligibleConsultation_whenEvaluatorReturnsTrue() {
        SubmitAnswersRequest request = new SubmitAnswersRequest(
                userId,
                List.of(new AnswerRequest(1, null, null))
        );

        when(evaluator.evaluate(request.getAnswers())).thenReturn(true);

        ConsultationResultResponse result = service.submitAnswers(request);

        assertNotNull(result.getConsultationId());
        assertTrue(result.isEligibleForPrescription());
        assertNull(result.getReason());
        assertEquals(ConsultationStatus.PENDING_DOCTOR_REVIEW, result.getStatus());
    }

    @Test
    void submitAnswers_shouldReturnAutoRejectedConsultation_whenEvaluatorReturnsFalse() {
        SubmitAnswersRequest request = new SubmitAnswersRequest(
                userId,
                List.of(new AnswerRequest(2, null, null))
        );

        when(evaluator.evaluate(request.getAnswers())).thenReturn(false);

        ConsultationResultResponse result = service.submitAnswers(request);

        assertNotNull(result.getConsultationId());
        assertFalse(result.isEligibleForPrescription());
        assertEquals("User has had a previous adverse reaction to this medication.", result.getReason());
        assertEquals(ConsultationStatus.AUTO_REJECTED, result.getStatus());

    }
}