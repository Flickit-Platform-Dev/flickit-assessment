package org.flickit.assessment.kit.application.service.subject;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.subject.UpdateSubjectPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateSubjectService implements UpdateSubjectUseCase {

    private final UpdateSubjectPort updateSubjectPort;
    private final LoadAssessmentKitPort loadAssessmentKitPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Override
    public void updateSubject(Param param) {
        AssessmentKit kit = loadAssessmentKitPort.load(param.getKitId());
        Long kitVersionId = kit.getActiveVersionId();
        long expertGroupId = kit.getExpertGroupId();
        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId);
        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        String code = Subject.generateSlugCode(param.getTitle());
        updateSubjectPort.update(new UpdateSubjectPort.Param(param.getSubjectId(),
            kitVersionId,
            code,
            param.getTitle(),
            param.getIndex(),
            param.getDescription(),
            param.getWeight(),
            LocalDateTime.now(),
            param.getCurrentUserId()));
    }
}