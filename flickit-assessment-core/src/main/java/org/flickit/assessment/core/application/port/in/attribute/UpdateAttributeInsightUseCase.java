package org.flickit.assessment.core.application.port.in.attribute;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;


public interface UpdateAttributeInsightUseCase {

    void updateAttributeInsight(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_ATTRIBUTE_INSIGHT_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = UPDATE_ATTRIBUTE_INSIGHT_ATTRIBUTE_ID_NOT_NULL)
        Long attributeId;

        @NotNull(message = UPDATE_ATTRIBUTE_INSIGHT_ASSESSOR_INSIGHT_NOT_NULL)
        String assessorInsight;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID assessmentId, Long attributeId, String assessorInsight, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.attributeId = attributeId;
            this.assessorInsight = assessorInsight;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}