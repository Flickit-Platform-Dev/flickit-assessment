package org.flickit.assessment.kit.application.service.questionnaire;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.port.in.questionnaire.GetKitQuestionnaireUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupByKitVersionIdPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadActiveKitVersionIdByQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairePort;
import org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.QUESTIONNAIRE_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetKitQuestionnaireServiceTest {

    @InjectMocks
    private GetKitQuestionnaireService service;

    @Mock
    private LoadQuestionnairePort loadQuestionnairePort;

    @Mock
    private LoadActiveKitVersionIdByQuestionnairePort loadActiveKitVersionIdPort;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    private LoadKitExpertGroupByKitVersionIdPort loadKitExpertGroupPort;

    @Test
    void testGetKitQuestionnaire_ValidInput_ValidResult() {
        long kitVersionId = 3L;
        long questionnaireId = 2L;
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();
        UUID currentUserId = UUID.randomUUID();

        GetKitQuestionnaireUseCase.Param param = new GetKitQuestionnaireUseCase.Param(questionnaireId,
            currentUserId);

        var expectedResult = new Questionnaire(5L,
            "QUESTIONNAIRE",
            "questionnaire",
            1,
            "description",
            LocalDateTime.now(),
            LocalDateTime.now());

        when(loadActiveKitVersionIdPort.loadKitVersionId(questionnaireId)).thenReturn(kitVersionId);
        when(loadKitExpertGroupPort.loadKitExpertGroupByKitVersionId(kitVersionId)).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), currentUserId)).thenReturn(true);
        when(loadQuestionnairePort.loadById(questionnaireId, kitVersionId)).thenReturn(expectedResult);

        GetKitQuestionnaireUseCase.Result actualResult = service.getKitQuestionnaire(param);

        assertNotNull(actualResult);
        assertEquals(expectedResult.getId(), actualResult.id());
        assertEquals(expectedResult.getTitle(), actualResult.title());
    }

    @Test
    void testGetKitQuestionnaire_CurrentUserIsNotMemberOfExpertGroup_ThrowsException() {
        long questionnaireId = 2L;
        long kitVersionId = 3L;
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();
        UUID currentUserId = UUID.randomUUID();

        GetKitQuestionnaireUseCase.Param param = new GetKitQuestionnaireUseCase.Param(questionnaireId,
            currentUserId);

        when(loadActiveKitVersionIdPort.loadKitVersionId(questionnaireId)).thenReturn(kitVersionId);
        when(loadKitExpertGroupPort.loadKitExpertGroupByKitVersionId(kitVersionId)).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), currentUserId)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> service.getKitQuestionnaire(param));
    }

    @Test
    void testGetKitQuestionnaire_QuestionnaireWithGivenIdAndKitIdDoesNotExist_ThrowsException() {
        long kitVersionId = 3L;
        long questionnaireId = 2L;
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();
        UUID currentUserId = UUID.randomUUID();

        GetKitQuestionnaireUseCase.Param param = new GetKitQuestionnaireUseCase.Param(questionnaireId,
            currentUserId);

        when(loadActiveKitVersionIdPort.loadKitVersionId(questionnaireId)).thenReturn(kitVersionId);
        when(loadKitExpertGroupPort.loadKitExpertGroupByKitVersionId(kitVersionId)).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), currentUserId)).thenReturn(true);
        when(loadQuestionnairePort.loadById(questionnaireId, kitVersionId))
            .thenThrow(new ResourceNotFoundException(QUESTIONNAIRE_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getKitQuestionnaire(param));
        assertEquals(QUESTIONNAIRE_ID_NOT_FOUND, throwable.getMessage());
    }
}
