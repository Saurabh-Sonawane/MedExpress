package com.heliosx.services;

import com.heliosx.entities.Consultation;
import com.heliosx.model.AnswerType;
import com.heliosx.model.ConsultationResultResponse;
import com.heliosx.model.ConsultationStatus;
import com.heliosx.model.Question;
import com.heliosx.model.QuestionsResponse;
import com.heliosx.model.SubmitAnswersRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsultationServiceImpl implements ConsultationService {
    private static final String MESSAGE = "User has had a previous adverse reaction to this medication.";
    private final ConsultationEvaluator evaluator;

    // In-memory question bank
    private final Map<String, List<Question>> questionBank = Map.of(
            "GENOVIAN_PEAR_ALLERGY", List.of(
                    new Question(1, "What is your age?", AnswerType.NUMBER, null),
                    new Question(2, "Have you previously had an adverse reaction to this medication?", AnswerType.BOOLEAN, null),
                    new Question(3, "Which symptoms do you experience?", AnswerType.MULTI_SELECT,
                            List.of("ITCHING", "RASH", "BREATHING_DIFFICULTY")),
                    new Question(4, "What is your gender?", AnswerType.SINGLE_SELECT,
                            List.of("MALE", "FEMALE", "OTHER")),
                    new Question(5, "Please list any medications you are currently taking.", AnswerType.TEXT, null),
                    new Question(6, "When was the last time you received treatment for this condition?", AnswerType.DATE, null)
            )
    );

    // In-memory storage of consultations
    private final Map<String, Consultation> consultationStore = new HashMap<>();

    @Override
    public QuestionsResponse getQuestions() {
        String defaultCondition = "GENOVIAN_PEAR_ALLERGY";
        List<Question> questions = questionBank.getOrDefault(defaultCondition, List.of());
        return new QuestionsResponse(questions);
    }

    @Override
    public ConsultationResultResponse submitAnswers(SubmitAnswersRequest request) {
        UUID consultationId = UUID.randomUUID();
        // Evaluate eligibility
        boolean eligible = evaluator.evaluate(request.getAnswers());
        String reason = eligible ? null : MESSAGE;
        ConsultationStatus status = eligible ? ConsultationStatus.PENDING_DOCTOR_REVIEW : ConsultationStatus.AUTO_REJECTED;

        // Store consultation
        Consultation consultation = new Consultation(
                consultationId,
                request.getUserId(),
                request.getAnswers(),
                eligible,
                reason,
                status,
                Instant.now(),
                Instant.now()
        );
        consultationStore.put(consultationId.toString(), consultation);

        return new ConsultationResultResponse(
                consultationId,
                eligible,
                reason,
                status
        );
    }
}
