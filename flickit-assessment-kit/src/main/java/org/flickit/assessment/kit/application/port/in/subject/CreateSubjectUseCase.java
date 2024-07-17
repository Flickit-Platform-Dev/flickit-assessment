package org.flickit.assessment.kit.application.port.in.subject;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface CreateSubjectUseCase {

    long createSubject(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CREATE_SUBJECT_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = CREATE_SUBJECT_INDEX_NOT_NULL)
        Integer index;

        @NotBlank(message = CREATE_SUBJECT_TITLE_NOT_BLANK)
        String title;

        @NotBlank(message = CREATE_SUBJECT_DESCRIPTION_NOT_BLANK)
        String description;

        @NotNull(message = CREATE_SUBJECT_WEIGHT_NOT_NULL)
        Integer weight;

        @NotNull(message = CREATE_SUBJECT_EXPERT_GROUP_ID_NOT_NULL)
        Long expertGroupId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long kitVersionId,
                     Integer index,
                     String title,
                     String description,
                     Integer weight,
                     Long expertGroupId,
                     UUID currentUserId) {

            this.kitVersionId = kitVersionId;
            this.index = index;
            this.title = title;
            this.description = description;
            this.weight = weight;
            this.expertGroupId = expertGroupId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
