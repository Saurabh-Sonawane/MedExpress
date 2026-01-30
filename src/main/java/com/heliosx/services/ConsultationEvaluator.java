package com.heliosx.services;

import com.heliosx.model.AnswerRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
public class ConsultationEvaluator {

    public boolean evaluate(List<AnswerRequest> answers) {
        // Only simple rule: previous adverse reaction blocks eligibility
        return answers.stream()
                .filter(a -> a.getQuestionId() == 2)
                .findFirst()
                .map(a -> !a.getValue().booleanValue())
                .orElse(true); // if no answer, assume eligible
    }
}
