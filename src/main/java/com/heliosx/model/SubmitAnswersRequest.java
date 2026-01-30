package com.heliosx.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitAnswersRequest {
    private UUID userId;
    private List<AnswerRequest> answers;
}
