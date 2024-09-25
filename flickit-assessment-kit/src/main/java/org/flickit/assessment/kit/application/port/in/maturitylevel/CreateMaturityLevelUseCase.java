package org.flickit.assessment.kit.application.port.in.maturitylevel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface CreateMaturityLevelUseCase {

    long createMaturityLevel(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CREATE_MATURITY_LEVEL_KIT_ID_NOT_NULL)
        Long kitId;

        @NotNull(message = CREATE_MATURITY_LEVEL_INDEX_NOT_NULL)
        Integer index;

        @NotBlank(message = CREATE_MATURITY_LEVEL_TITLE_NOT_BLANK)
        String title;

        @NotBlank(message = CREATE_MATURITY_LEVEL_DESCRIPTION_NOT_BLANK)
        String description;

        @NotNull(message = CREATE_MATURITY_LEVEL_VALUE_NOT_NULL)
        Integer value;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long kitId,
                     Integer index,
                     String title,
                     String description,
                     Integer value,
                     UUID currentUserId) {
            this.kitId = kitId;
            this.index = index;
            this.title = title;
            this.description = description;
            this.value = value;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}