package com.heliosx.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heliosx.model.AnswerRequest;
import com.heliosx.model.ConsultationResultResponse;
import com.heliosx.model.QuestionsResponse;
import com.heliosx.model.SubmitAnswersRequest;
import com.heliosx.services.ConsultationService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultationController.class)
class ConsultationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConsultationService consultationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getQuestions_shouldReturnQuestionsResponse() throws Exception {
        QuestionsResponse mockResponse = new QuestionsResponse(List.of());
        when(consultationService.getQuestions()).thenReturn(mockResponse);

        mockMvc.perform(get("/med-express-api/v1/consultations/questions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.questions").isArray());

        verify(consultationService, times(1)).getQuestions();
    }

    @Test
    void submitAnswers_shouldReturnConsultationResult() throws Exception {
        SubmitAnswersRequest request = new SubmitAnswersRequest(
                UUID.randomUUID(),
                List.of(new AnswerRequest(1, null, null))
        );
        UUID consultationId = UUID.randomUUID();
        ConsultationResultResponse mockResult = new ConsultationResultResponse(
                consultationId,
                true,
                null,
                null
        );

        when(consultationService.submitAnswers(ArgumentMatchers.any(SubmitAnswersRequest.class)))
                .thenReturn(mockResult);

        mockMvc.perform(post("/med-express-api/v1/consultations/submit-answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.consultationId").value(consultationId.toString()))
                .andExpect(jsonPath("$.eligibleForPrescription").value(true));

        verify(consultationService, times(1)).submitAnswers(ArgumentMatchers.any(SubmitAnswersRequest.class));
    }

    @Test
    void submitAnswers_shouldReturnBadRequestWhenUserIdMissing() throws Exception {
        SubmitAnswersRequest request = new SubmitAnswersRequest(null,
                List.of(new AnswerRequest(1, null, null))
        );

        mockMvc.perform(post("/med-express-api/v1/consultations/submit-answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("userId is required"));

        verify(consultationService, never()).submitAnswers(any());
    }

    @Test
    void submitAnswers_shouldReturnBadRequestWhenAnswersEmpty() throws Exception {
        SubmitAnswersRequest request = new SubmitAnswersRequest(
                UUID.randomUUID(),
                List.of()
        );

        mockMvc.perform(post("/med-express-api/v1/consultations/submit-answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("answers cannot be empty"));

        verify(consultationService, never()).submitAnswers(any());
    }
}