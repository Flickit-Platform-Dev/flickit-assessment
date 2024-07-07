package org.flickit.assessment.kit.application.port.in.questionnaire;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_QUESTIONNAIRE_QUESTIONNAIRE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetKitQuestionnaireUseCaseParamTest {

    @Test
    void testGetKitQuestionnaire_QuestionnaireIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitQuestionnaireUseCase.Param(null, currentUserId));
        assertThat(throwable).hasMessage("questionnaireId: " + GET_KIT_QUESTIONNAIRE_QUESTIONNAIRE_ID_NOT_NULL);
    }

    @Test
    void testGetKitQuestionnaire_CurrentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitQuestionnaireUseCase.Param(1L, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
