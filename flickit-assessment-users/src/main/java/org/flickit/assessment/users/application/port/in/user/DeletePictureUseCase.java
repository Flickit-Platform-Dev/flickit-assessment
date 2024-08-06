package org.flickit.assessment.users.application.port.in.user;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

public interface DeletePictureUseCase {

    void delete(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull
        UUID userId;

        public Param(UUID userId) {

            this.userId = userId;
            this.validateSelf();
        }
    }
}
