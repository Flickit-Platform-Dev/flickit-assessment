package org.flickit.flickitassessmentcore.application.port.out.assessment;

import java.util.UUID;

public interface LoadAssessmentResultIdByAssessmentPort {
    UUID loadAssessmentResultIdByAssessmentId(UUID assessmentId);
}
