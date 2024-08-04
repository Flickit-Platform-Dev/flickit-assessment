package org.flickit.assessment.core.application.port.out.attributeinsight;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateAttributeInsightPort {

    void persist(Param param);

    record Param(UUID assessmentResultId,
                 Long attributeId,
                 String attributeTitle,
                 String aiInsight,
                 String assessorInsight,
                 LocalDateTime aiInsightTime,
                 LocalDateTime assessorInsightTime,
                 String aiInputPath) {
    }
}
