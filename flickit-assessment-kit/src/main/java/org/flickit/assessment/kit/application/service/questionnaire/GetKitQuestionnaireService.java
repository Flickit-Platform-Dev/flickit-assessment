package org.flickit.assessment.kit.application.service.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.port.in.questionnaire.GetKitQuestionnaireUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupByKitVersionIdPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadActiveKitVersionIdByQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitQuestionnaireService implements GetKitQuestionnaireUseCase {

    private final LoadQuestionnairePort loadQuestionnairePort;
    private final LoadActiveKitVersionIdByQuestionnairePort loadActiveKitVersionIdPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadKitExpertGroupByKitVersionIdPort loadKitExpertGroupPort;

    @Override
    public Result getKitQuestionnaire(Param param) {
        var kitVersionId = loadActiveKitVersionIdPort.loadKitVersionId(param.getQuestionnaireId());
        ExpertGroup expertGroup = loadKitExpertGroupPort.loadKitExpertGroupByKitVersionId(kitVersionId);

        if (!checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var questionnaire = loadQuestionnairePort.loadById(param.getQuestionnaireId(), kitVersionId);
        return new Result(questionnaire.getId(), questionnaire.getTitle());
    }
}
