package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.hibernate.validator.constraints.URL;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;


public interface CreateAssessmentAttributeAiReportUseCase {

    Result create(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_ASSESSMENT_ID_NOT_NULL)
        UUID id;

        @NotNull(message = CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_FILE_LINK_NOT_NULL)
        @URL(message = CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_FILE_LINK_NOT_URL)
        String fileLink;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID id, String fileLink, UUID currentUserId) {
            this.id = id;
            this.fileLink = fileLink;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(String content) {
    }
}
