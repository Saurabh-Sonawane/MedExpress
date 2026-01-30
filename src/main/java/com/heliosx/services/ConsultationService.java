package com.heliosx.services;

import com.heliosx.model.ConsultationResultResponse;
import com.heliosx.model.QuestionsResponse;
import com.heliosx.model.SubmitAnswersRequest;

public interface ConsultationService {
    QuestionsResponse getQuestions();

    ConsultationResultResponse submitAnswers(SubmitAnswersRequest request);
}
