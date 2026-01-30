package com.heliosx.entities;

import com.heliosx.model.AnswerRequest;
import com.heliosx.model.ConsultationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Consultation {
    private UUID consultationId;
    private UUID userId;
    private List<AnswerRequest> answers;
    private boolean eligibleForPrescription;
    private String reason;
    private ConsultationStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}