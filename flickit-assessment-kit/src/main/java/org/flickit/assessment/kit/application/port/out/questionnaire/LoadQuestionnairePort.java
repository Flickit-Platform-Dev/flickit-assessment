package org.flickit.assessment.kit.application.port.out.questionnaire;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.Questionnaire;

public interface LoadQuestionnairePort {

    /**
     * Loads questionnaire associated with an ID and kit's version id,
     * ordered by their index.
     *
     * @param id The ID of the questionnaires are to be loaded.
     * @return A questionnaires associated with the given ID.
     * @throws ResourceNotFoundException if the Questionnaire ID is not found.
     */
    Questionnaire loadById(Long id, Long kitVersionId);
}
