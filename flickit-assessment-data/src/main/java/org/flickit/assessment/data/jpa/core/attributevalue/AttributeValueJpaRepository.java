package org.flickit.assessment.data.jpa.core.attributevalue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface AttributeValueJpaRepository extends JpaRepository<AttributeValueJpaEntity, UUID> {

    List<AttributeValueJpaEntity> findByAssessmentResultId(UUID assessmentResultId);

    List<AttributeValueJpaEntity> findByAssessmentResult_assessment_IdAndAttributeIdIn(UUID assessmentId, Collection<Long> attributeIds);

    AttributeValueJpaEntity findByAttributeIdAndAssessmentResultId(@Param(value = "attributeId") Long attributeId,
                                                                   @Param(value = "assessmentResultId") UUID assessmentResultId);

    @Query("""
        SELECT av
        FROM AttributeValueJpaEntity av
        LEFT JOIN AttributeJpaEntity att ON av.attributeId = att.id
        WHERE att.subjectId = :subjectId AND av.assessmentResult.id = :assessmentResultId
            AND att.kitVersionId = av.assessmentResult.kitVersionId
        """)
    List<AttributeValueJpaEntity> findByAssessmentResultIdAndSubjectId(@Param(value = "assessmentResultId") UUID assessmentResultId,
                                                                       @Param(value = "subjectId") Long subjectId);

    @Query("""
            SELECT av as attributeValue,
                att.subjectId as subjectId,
                att as attribute
            FROM AttributeValueJpaEntity av
            LEFT JOIN AttributeJpaEntity att ON av.attributeId = att.id
                and av.assessmentResult.kitVersionId = att.kitVersionId
                and av.assessmentResult.id = :assessmentResultId
            WHERE att.subjectId IN :subjectIds
        """)
    List<SubjectIdAttributeValueView> findByAssessmentResultIdAndSubjectIdIn(@Param(value = "assessmentResultId") UUID assessmentResultId,
                                                                                 @Param(value = "subjectIds") Collection<Long> subjectIds);

    @Modifying
    @Query("update AttributeValueJpaEntity a set a.maturityLevelId = :maturityLevelId where a.id = :id")
    void updateMaturityLevelById(@Param(value = "id") UUID id,
                                 @Param(value = "maturityLevelId") Long maturityLevelId);

    @Modifying
    @Query("update AttributeValueJpaEntity a set a.confidenceValue = :confidenceValue where a.id = :id")
    void updateConfidenceValueById(@Param(value = "id") UUID id,
                                   @Param(value = "confidenceValue") Double confidenceValue);
}
