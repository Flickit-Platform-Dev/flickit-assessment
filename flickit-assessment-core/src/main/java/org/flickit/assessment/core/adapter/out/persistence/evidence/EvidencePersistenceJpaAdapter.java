package org.flickit.assessment.core.adapter.out.persistence.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.evidence.GetAttributeEvidenceListUseCase.AttributeEvidenceListItem;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceListUseCase.EvidenceListItem;
import org.flickit.assessment.core.application.port.out.evidence.*;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.evidence.EvidenceJpaEntity;
import org.flickit.assessment.data.jpa.core.evidence.EvidenceJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;
import org.flickit.assessment.data.jpa.users.user.UserJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.flickit.assessment.core.adapter.out.persistence.evidence.EvidenceMapper.toEvidenceListItem;
import static org.flickit.assessment.core.common.ErrorMessageKey.SUBMIT_ANSWER_QUESTION_ID_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.ADD_EVIDENCE_ASSESSMENT_ID_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.ADD_EVIDENCE_QUESTION_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class EvidencePersistenceJpaAdapter implements
    CreateEvidencePort,
    LoadEvidencesPort,
    UpdateEvidencePort,
    DeleteEvidencePort,
    CheckEvidenceExistencePort,
    LoadAttributeEvidencesPort {

    private final EvidenceJpaRepository repository;
    private final QuestionJpaRepository questionRepository;
    private final UserJpaRepository userRepository;
    private final AssessmentResultJpaRepository assessmentResultRepository;

    @Override
    public UUID persist(CreateEvidencePort.Param param) {
        var assessmentKitVersionId = assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(param.assessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(ADD_EVIDENCE_ASSESSMENT_ID_NOT_FOUND))
            .getKitVersionId();
        var question = questionRepository.findById(param.questionId())
            .orElseThrow(() -> new ResourceNotFoundException(ADD_EVIDENCE_QUESTION_ID_NOT_FOUND)); // TODO: This query must be changed after question id deletion
        if (!Objects.equals(assessmentKitVersionId, question.getKitVersionId()))
            throw new ResourceNotFoundException(ADD_EVIDENCE_QUESTION_ID_NOT_FOUND);
        var unsavedEntity = EvidenceMapper.mapCreateParamToJpaEntity(param, question.getRefNum());
        EvidenceJpaEntity entity = repository.save(unsavedEntity);
        return entity.getId();
    }

    @Override
    public PaginatedResponse<EvidenceListItem> loadNotDeletedEvidences(Long questionId, UUID assessmentId, int page, int size) {
        var pageResult = repository.findByQuestionIdAndAssessmentIdAndDeletedFalseOrderByLastModificationTimeDesc(
            questionId, assessmentId, PageRequest.of(page, size));
        var userIds = pageResult.getContent().stream()
            .map(EvidenceJpaEntity::getCreatedBy)
            .toList();
        var userIdToUserMap = userRepository.findAllById(userIds).stream()
            .collect(Collectors.toMap(UserJpaEntity::getId, Function.identity()));
        var items = pageResult.getContent().stream()
            .map(e -> toEvidenceListItem(e, userIdToUserMap.get(e.getCreatedBy())))
            .toList();
        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            EvidenceJpaEntity.Fields.LAST_MODIFICATION_TIME,
            Sort.Direction.DESC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }

    @Override
    public UpdateEvidencePort.Result update(UpdateEvidencePort.Param param) {
        repository.update(
            param.id(),
            param.description(),
            param.type(),
            param.lastModificationTime(),
            param.lastModifiedById()
        );
        return new UpdateEvidencePort.Result(param.id());
    }

    @Override
    public void deleteById(UUID id) {
        repository.delete(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return repository.existsByIdAndDeletedFalse(id);
    }

    @Override
    public PaginatedResponse<AttributeEvidenceListItem> loadAttributeEvidences(UUID assessmentId, Long attributeId,
                                                                               Integer type, int page, int size) {
        var pageResult = repository.findAssessmentAttributeEvidencesByTypeOrderByLastModificationTimeDesc(
            assessmentId, attributeId, type, PageRequest.of(page, size));

        var items = pageResult.getContent().stream()
            .map(AttributeEvidenceListItem::new)
            .toList();
        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            EvidenceJpaEntity.Fields.LAST_MODIFICATION_TIME,
            Sort.Direction.DESC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }
}
