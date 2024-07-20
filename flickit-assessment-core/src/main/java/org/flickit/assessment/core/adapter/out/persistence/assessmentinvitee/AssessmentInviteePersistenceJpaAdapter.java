package org.flickit.assessment.core.adapter.out.persistence.assessmentinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentInvitee;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.port.out.assessmentinvitee.InviteAssessmentUserPort;
import org.flickit.assessment.core.application.port.out.assessmentinvitee.LoadAssessmentInviteeListPort;
import org.flickit.assessment.data.jpa.core.assessmentinvitee.AssessmentInviteeJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentinvitee.AssessmentInviteeJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.flickit.assessment.core.adapter.out.persistence.assessmentinvitee.AssessmentInviteeMapper.mapToJpaEntity;
import static org.flickit.assessment.core.common.ErrorMessageKey.INVITE_ASSESSMENT_USER_ROLE_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class AssessmentInviteePersistenceJpaAdapter implements
    LoadAssessmentInviteeListPort,
    InviteAssessmentUserPort {

    private final AssessmentInviteeJpaRepository repository;

    @Override
    public PaginatedResponse<AssessmentInvitee> loadByAssessmentId(UUID assessmentId, int size, int page) {
        var order = AssessmentInviteeJpaEntity.Fields.creationTime;
        var sort = Sort.Direction.DESC;

        var pageResult = repository.findByAssessmentId(assessmentId,
            PageRequest.of(page, size, sort, order));

        var items = pageResult.getContent().stream()
            .map(AssessmentInviteeMapper::mapToDomainModel)
            .toList();

        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            order,
            sort.name().toLowerCase(),
            (int) pageResult.getTotalElements());
    }

    @Override
    public void invite(InviteAssessmentUserPort.Param param) {
        if (!AssessmentUserRole.isValidId(param.roleId()))
            throw new ResourceNotFoundException(INVITE_ASSESSMENT_USER_ROLE_ID_NOT_FOUND);

        var invitation = repository.findByAssessmentIdAndEmail(param.assessmentId(), param.email());

        AssessmentInviteeJpaEntity entity;
        entity = invitation.map(assessmentInviteeJpaEntity -> mapToJpaEntity(invitation.get().getId(), param))
            .orElseGet(() -> mapToJpaEntity(null, param));

        repository.save(entity);
    }
}
