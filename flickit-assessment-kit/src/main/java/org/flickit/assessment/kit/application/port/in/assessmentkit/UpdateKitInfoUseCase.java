package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface UpdateKitInfoUseCase {

    void updateKitInfo(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_KIT_INFO_KIT_ID_NOT_NULL)
        Long kitId;

        @Size(min = 3, message = UPDATE_KIT_INFO_TITLE_SIZE_MIN)
        @Size(max = 50, message = UPDATE_KIT_INFO_TITLE_SIZE_MAX)
        String title;

        @Size(min = 3, message = UPDATE_KIT_INFO_SUMMARY_SIZE_MIN)
        @Size(max = 200, message = UPDATE_KIT_INFO_SUMMARY_SIZE_MAX)
        String summary;

        Boolean published;

        Boolean isPrivate;

        Double price;

        @Size(min = 3, message = UPDATE_KIT_INFO_ABOUT_SIZE_MIN)
        @Size(max = 1000, message = UPDATE_KIT_INFO_ABOUT_SIZE_MAX)
        String about;

        @Size(min = 1, message = UPDATE_KIT_INFO_TAGS_SIZE_MIN)
        List<Long> tags;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long kitId, String title, String summary, Boolean published, Boolean isPrivate, Double price, String about, List<Long> tags, UUID currentUserId) {
            this.kitId = kitId;
            this.currentUserId = currentUserId;
            this.title = title;
            this.summary = summary;
            this.published = published;
            this.isPrivate = isPrivate;
            this.price = price;
            this.about = about;
            this.tags = tags;
            this.validateSelf();
        }
    }
}
