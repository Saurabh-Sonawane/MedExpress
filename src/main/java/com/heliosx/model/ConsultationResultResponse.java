package com.heliosx.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ConsultationResultResponse {
    private UUID consultationId;
    private boolean eligibleForPrescription;
    private String reason;
    private ConsultationStatus status;
}
