package com.heliosx.controllers;

import com.heliosx.exception.InvalidRequestException;
import com.heliosx.model.ConsultationResultResponse;
import com.heliosx.model.QuestionsResponse;
import com.heliosx.model.SubmitAnswersRequest;
import com.heliosx.services.ConsultationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/med-express-api/v1/consultations")
public class ConsultationController {

    private final ConsultationService consultationService;

    public ConsultationController(ConsultationService consultationService) {
        this.consultationService = consultationService;
    }

    @GetMapping("/questions")
    public ResponseEntity<QuestionsResponse> getQuestions() {
        QuestionsResponse response = consultationService.getQuestions();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/submit-answers")
    public ResponseEntity<ConsultationResultResponse> submitAnswers(
            @RequestBody SubmitAnswersRequest request) {
        validateSubmitRequest(request);
        return ResponseEntity.ok(consultationService.submitAnswers(request));
    }

    private void validateSubmitRequest(SubmitAnswersRequest request) {
        if (request.getUserId() == null) {
            throw new InvalidRequestException("userId is required");
        }

        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new InvalidRequestException("answers cannot be empty");
        }
    }

}
