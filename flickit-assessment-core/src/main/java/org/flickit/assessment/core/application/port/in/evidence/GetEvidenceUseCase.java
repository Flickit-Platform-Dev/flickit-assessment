package org.flickit.assessment.core.application.port.in.evidence;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_EVIDENCE_ID_NOT_NULL;

public interface GetEvidenceUseCase {

    Result getEvidence(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull (message = GET_EVIDENCE_ID_NOT_NULL)
        UUID id;

        @NotNull (message =  COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID id, UUID currentUserId) {
            this.id = id;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(UUID id,
                  String description,
                  EvidenceQuestionnaire evidenceQuestionnaire,
                  EvidenceQuestion evidenceQuestion,
                  EvidenceAnswer evidenceAnswer,
                  String createdBy,
                  LocalDateTime creationTime,
                  LocalDateTime lastModificationTime){

        public record EvidenceQuestion(Long id, String title, Integer index){}
        public record EvidenceQuestionnaire(Long id, String title){}
        public record EvidenceAnswer(EvidenceAnswerOption evidenceAnswerOption, EvidenceConfidenceLevel evidenceConfidenceLevel, Boolean isNotApplicable){}
        public record EvidenceAnswerOption(Long id, String title, Integer index){}
        public record EvidenceConfidenceLevel(Integer id, String title){}
    }
}
