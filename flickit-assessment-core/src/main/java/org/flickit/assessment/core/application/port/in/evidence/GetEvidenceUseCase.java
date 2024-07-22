package org.flickit.assessment.core.application.port.in.evidence;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_EVIDENCE_ID_NOT_NULL;

public interface GetEvidenceUseCase {

    Result getEvidence(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_EVIDENCE_ID_NOT_NULL)
        UUID id;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID id, UUID currentUserId) {
            this.id = id;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(ResultEvidence evidence, ResultQuestion question) {
        public record ResultEvidence(
            UUID id,
            String description,
            String type,
            String createdBy,
            LocalDateTime creationTime,
            LocalDateTime lastModificationTime) {
        }

        public record ResultQuestion(Long id, String title, int index, List<QuestionOptions> options,
                                     QuestionQuestionnaire questionnaire, ResultQuestionAnswer answer) {
            public record QuestionOptions(Long id, int index, String title) {
            }

            public record QuestionQuestionnaire(Long id, String title) {
            }

            public record ResultQuestionAnswer(SelectedOption selectedOption, ResultConfidenceLevel confidenceLevel, Boolean isNotApplicable) {
                public record SelectedOption(Long id) {
                }

                public record ResultConfidenceLevel(int id, String title) {
                }
            }
        }
    }
}