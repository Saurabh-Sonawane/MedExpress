package com.heliosx.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heliosx.model.AnswerRequest;
import com.heliosx.model.AnswerType;
import com.heliosx.model.SubmitAnswersRequest;
import com.heliosx.services.ConsultationEvaluator;
import com.heliosx.services.ConsultationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ConsultationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConsultationEvaluator evaluator;

    @Autowired
    private ConsultationServiceImpl service;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    void getQuestions_shouldReturnQuestionsResponse() throws Exception {

        mockMvc.perform(get("/med-express-api/v1/consultations/questions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.questions", hasSize(6)))
                .andExpect(jsonPath("$.questions[0].id", is(1)))
                .andExpect(jsonPath("$.questions[0].type", is("NUMBER")))
                .andExpect(jsonPath("$.questions[0].question", not(emptyString())))
                .andExpect(jsonPath("$.questions[0].options", nullValue()));

    }

    @Test
    void submitAnswers_shouldReturnAutoRejectedConsultation() throws Exception {
        SubmitAnswersRequest request = new SubmitAnswersRequest(
                userId,
                List.of(
                        new AnswerRequest(1, AnswerType.NUMBER, objectMapper.valueToTree(25)),
                        new AnswerRequest(2, AnswerType.BOOLEAN, objectMapper.valueToTree(true))
                )
        );


        mockMvc.perform(post("/med-express-api/v1/consultations/submit-answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.consultationId").exists())
                .andExpect(jsonPath("$.eligibleForPrescription", is(false)))
                .andExpect(jsonPath("$.reason", is("User has had a previous adverse reaction to this medication.")))
                .andExpect(jsonPath("$.status", is("AUTO_REJECTED")));
    }

    @Test
    void submitAnswers_shouldReturnEligibleConsultation() throws Exception {
        SubmitAnswersRequest request = new SubmitAnswersRequest(
                userId,
                List.of(
                        new AnswerRequest(1, AnswerType.NUMBER, objectMapper.valueToTree(25)),
                        new AnswerRequest(2, AnswerType.BOOLEAN, objectMapper.valueToTree(false)),
                        new AnswerRequest(3, AnswerType.MULTI_SELECT, objectMapper.valueToTree(List.of("ITCHING")))
                )
        );

        mockMvc.perform(post("/med-express-api/v1/consultations/submit-answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.consultationId").exists())
                .andExpect(jsonPath("$.eligibleForPrescription", is(true)))
                .andExpect(jsonPath("$.reason").doesNotExist())
                .andExpect(jsonPath("$.status", is("PENDING_DOCTOR_REVIEW")));
    }

}