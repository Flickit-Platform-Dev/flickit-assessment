package org.flickit.assessment.core.adapter.out.report;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.domain.report.AssessmentReport;
import org.flickit.assessment.core.application.domain.report.AssessmentReport.AttributeReportItem;
import org.flickit.assessment.core.application.domain.report.AssessmentReport.SubjectReportItem;
import org.flickit.assessment.core.application.domain.report.AssessmentReport.SubjectReportItem.SubjectMaturityLevel;
import org.flickit.assessment.core.application.domain.report.AssessmentReport.AssessmentReportItem.AssessmentMaturityLevel;
import org.flickit.assessment.core.application.domain.report.AssessmentReport.AssessmentReportItem.AssessmentKitItem;
import org.flickit.assessment.core.application.domain.report.AssessmentReport.AssessmentReportItem.AssessmentKitItem.ExpertGroup;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentReportInfoPort;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.attributevalue.QualityAttributeValueJpaEntity;
import org.flickit.assessment.data.jpa.core.attributevalue.QualityAttributeValueJpaRepository;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaEntity;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaRepository;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Slf4j
@Component
@AllArgsConstructor
public class LoadAssessmentReportInfoAdapter implements LoadAssessmentReportInfoPort {

    private final AssessmentResultJpaRepository assessmentResultRepo;
    private final SubjectValueJpaRepository subjectValueRepo;
    private final AssessmentKitJpaRepository assessmentKitJpaRepository;
    private final ExpertGroupJpaRepository expertGroupJpaRepository;
    private final MaturityLevelJpaRepository maturityLevelJpaRepository;
    private final SubjectJpaRepository subjectJpaRepository;
    private final QualityAttributeValueJpaRepository qualityAttributeValueJpaRepository;
    private final AttributeJpaRepository attributeJpaRepository;

    @Override
    public AssessmentReport load(UUID assessmentId) {
        AssessmentResultJpaEntity assessmentResultEntity = assessmentResultRepo.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(REPORT_ASSESSMENT_ASSESSMENT_RESULT_NOT_FOUND));

        AssessmentJpaEntity assessment = assessmentResultEntity.getAssessment();
        AssessmentKitJpaEntity assessmentKitEntity = assessmentKitJpaRepository.findById(assessment.getAssessmentKitId())
            .orElseThrow(() -> new ResourceNotFoundException(REPORT_ASSESSMENT_ASSESSMENT_KIT_NOT_FOUND));

        ExpertGroupJpaEntity expertGroupEntity = expertGroupJpaRepository.findById(assessmentKitEntity.getExpertGroupId())
            .orElseThrow(() -> new ResourceNotFoundException(REPORT_ASSESSMENT_EXPERT_GROUP_NOT_FOUND));

        long kitVersionId = assessmentResultEntity.getKitVersionId();
        List<MaturityLevelJpaEntity> maturityLevelEntities = maturityLevelJpaRepository.findAllByKitVersionIdOrderByIndex(kitVersionId);

        Map<Long, MaturityLevelJpaEntity> idToMaturityLevelEntities = maturityLevelEntities.stream()
            .collect(toMap(MaturityLevelJpaEntity::getId, Function.identity()));

        AssessmentReport.AssessmentReportItem assessmentReportItem = new AssessmentReport.AssessmentReportItem(assessmentId,
            assessment.getTitle(),
            buildAssessmentKitItem(expertGroupEntity, assessmentKitEntity, maturityLevelEntities),
            buildAssessmentMaturityLevel(assessmentResultEntity, idToMaturityLevelEntities),
            assessmentResultEntity.getConfidenceValue(),
            assessmentResultEntity.getIsCalculateValid(),
            assessmentResultEntity.getIsConfidenceValid(),
            AssessmentColor.valueOfById(assessment.getColorId()),
            assessment.getLastModificationTime());

        UUID assessmentResultId = assessmentResultEntity.getId();
        List<AttributeReportItem> attributes = buildAttributeReportItems(assessmentResultId, idToMaturityLevelEntities);
        List<MaturityLevel> maturityLevels = maturityLevelEntities.stream()
            .map(e -> new MaturityLevel(e.getId(), e.getIndex(), e.getValue(), null))
            .toList();
        List<SubjectReportItem> subjects = buildSubjectReportItems(assessmentResultId, idToMaturityLevelEntities);

        return new AssessmentReport(assessmentReportItem, attributes, maturityLevels, subjects);
    }

    private static AssessmentKitItem buildAssessmentKitItem(ExpertGroupJpaEntity expertGroupEntity,
                                                            AssessmentKitJpaEntity assessmentKitEntity,
                                                            List<MaturityLevelJpaEntity> maturityLevelJpaEntities) {
        ExpertGroup expertGroup =
            new AssessmentReport.AssessmentReportItem.AssessmentKitItem.ExpertGroup(expertGroupEntity.getId(),
                expertGroupEntity.getTitle());

        return new AssessmentKitItem(assessmentKitEntity.getId(),
            assessmentKitEntity.getTitle(),
            assessmentKitEntity.getSummary(),
            maturityLevelJpaEntities.size(),
            expertGroup);
    }

    private static AssessmentMaturityLevel buildAssessmentMaturityLevel(AssessmentResultJpaEntity assessmentResultEntity,
                                                                        Map<Long, MaturityLevelJpaEntity> idToMaturityLevelEntities) {

        MaturityLevelJpaEntity maturityLevelEntity = idToMaturityLevelEntities.get(assessmentResultEntity.getMaturityLevelId());
        return new AssessmentMaturityLevel(maturityLevelEntity.getId(),
            maturityLevelEntity.getTitle(),
            maturityLevelEntity.getValue(),
            maturityLevelEntity.getIndex());
    }

    private List<AttributeReportItem> buildAttributeReportItems(UUID assessmentResultId,
                                                                Map<Long, MaturityLevelJpaEntity> idToMaturityLevelEntities) {
        List<QualityAttributeValueJpaEntity> attributeValueEntities = qualityAttributeValueJpaRepository.findByAssessmentResultId(assessmentResultId);
        Set<UUID> attrRefNums = attributeValueEntities.stream()
            .map(QualityAttributeValueJpaEntity::getAttributeRefNum)
            .collect(Collectors.toSet());
        Map<UUID, Long> attributeRefNumToMaturityLevelId = attributeValueEntities.stream()
            .collect(toMap(QualityAttributeValueJpaEntity::getAttributeRefNum, QualityAttributeValueJpaEntity::getMaturityLevelId));
        List<AttributeJpaEntity> attributeEntities = attributeJpaRepository.findAllByRefNumIn(attrRefNums);
        return attributeEntities.stream()
            .map(e -> {
                Long maturityLevelId = attributeRefNumToMaturityLevelId.get(e.getRefNum());
                Integer index = idToMaturityLevelEntities.get(maturityLevelId).getIndex();
                return new AssessmentReport.AttributeReportItem(e.getId(), e.getTitle(), index);
            })
            .toList();
    }

    private List<SubjectReportItem> buildSubjectReportItems(UUID assessmentResultId,
                                                            Map<Long, MaturityLevelJpaEntity> idToMaturityLevelEntities) {
        List<SubjectValueJpaEntity> subjectValueEntities = subjectValueRepo.findByAssessmentResultId(assessmentResultId);
        Set<UUID> refNums = subjectValueEntities.stream()
            .map(SubjectValueJpaEntity::getSubjectRefNum)
            .collect(Collectors.toSet());

        Map<UUID, Long> subjectRefNumToMaturityLevelId = subjectValueEntities.stream()
            .collect(toMap(SubjectValueJpaEntity::getSubjectRefNum, SubjectValueJpaEntity::getMaturityLevelId));

        List<SubjectJpaEntity> subjectEntities = subjectJpaRepository.findAllByRefNumIn(refNums);

        return subjectEntities.stream()
            .map(e -> {
                Long maturityLevelId = subjectRefNumToMaturityLevelId.get(e.getRefNum());
                MaturityLevelJpaEntity maturityLevelEntity = idToMaturityLevelEntities.get(maturityLevelId);
                SubjectMaturityLevel subjectMaturityLevel =
                    new AssessmentReport.SubjectReportItem.SubjectMaturityLevel(maturityLevelEntity.getId(),
                        maturityLevelEntity.getTitle());
                return new AssessmentReport.SubjectReportItem(e.getId(),
                    e.getTitle(),
                    e.getIndex(),
                    e.getDescription(),
                    subjectMaturityLevel);
            }).toList();
    }
}
