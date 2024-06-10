package org.flickit.assessment.data.jpa.core.attributevalue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface QualityAttributeValueJpaRepository extends JpaRepository<QualityAttributeValueJpaEntity, UUID> {

    @Query("""
        SELECT qav FROM QualityAttributeValueJpaEntity qav
        LEFT JOIN AttributeJpaEntity a ON a.id = :attributeId AND qav.attributeRefNum = a.refNum
        WHERE qav.assessmentResult.id = :assessmentResultId
        """)
    QualityAttributeValueJpaEntity findByAttributeIdAndAssessmentResult_Id(@Param(value = "attributeId") Long attributeId,
                                                                           @Param(value = "assessmentResultId") UUID assessmentResultId);

    List<QualityAttributeValueJpaEntity> findByAssessmentResult_assessment_IdAndAttributeRefNumIn(@Param(value = "assessmentId") UUID assessmentId,
                                                                                                  @Param(value = "attributeRefNums") List<UUID> attributeRefNums);

    @Query("""
        SELECT av
        FROM QualityAttributeValueJpaEntity av
        LEFT JOIN AttributeJpaEntity att ON av.attributeRefNum = att.refNum
            and av.assessmentResult.kitVersionId = att.subject.kitVersionId
            and av.assessmentResult.id = :assessmentResultId
        WHERE att.subject.id = :subjectId
        """)
    List<QualityAttributeValueJpaEntity> findByAssessmentResultIdAndSubjectId(@Param(value = "assessmentResultId") UUID assessmentResultId,
                                                                              @Param(value = "subjectId") Long subjectId);

    @Query("""
            SELECT av as attributeValue,
                att.subject.refNum as subjectRefNum,
                att as attribute
            FROM QualityAttributeValueJpaEntity av
            LEFT JOIN AttributeJpaEntity att ON av.attributeRefNum = att.refNum
                and av.assessmentResult.kitVersionId = att.subject.kitVersionId
                and av.assessmentResult.id = :assessmentResultId
            WHERE att.subject.refNum IN :subjectRefNums
        """)
    List<SubjectRefNumAttributeValueView> findByAssessmentResultIdAndSubjectRefNumIn(@Param(value = "assessmentResultId") UUID assessmentResultId,
                                                                                     @Param(value = "subjectRefNums") Collection<UUID> subjectRefNums);

    @Modifying
    @Query("update QualityAttributeValueJpaEntity a set a.maturityLevelId = :maturityLevelId where a.id = :id")
    void updateMaturityLevelById(@Param(value = "id") UUID id,
                                 @Param(value = "maturityLevelId") Long maturityLevelId);

    @Modifying
    @Query("update QualityAttributeValueJpaEntity a set a.confidenceValue = :confidenceValue where a.id = :id")
    void updateConfidenceValueById(@Param(value = "id") UUID id,
                                   @Param(value = "confidenceValue") Double confidenceValue);

    List<QualityAttributeValueJpaEntity> findByAssessmentResultId(@Param(value = "assessmentResultId") UUID assessmentResultId);
}