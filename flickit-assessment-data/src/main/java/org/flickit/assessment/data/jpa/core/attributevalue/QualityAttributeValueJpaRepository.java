package org.flickit.assessment.data.jpa.core.attributevalue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface QualityAttributeValueJpaRepository extends JpaRepository<QualityAttributeValueJpaEntity, UUID> {

    List<QualityAttributeValueJpaEntity> findByAssessmentResultId(UUID resultId);

    @Query("""
        SELECT qav
        FROM QualityAttributeValueJpaEntity qav
        JOIN AssessmentResultJpaEntity asmr
        ON asmr.id = qav.assessmentResult.id
        JOIN AssessmentJpaEntity asm
        ON asm.id = asmr.assessment.id
        WHERE asm.id = :assessmentId
        AND qav.qualityAttributeId = :attributeId
        AND qav.maturityLevelId = :maturityLevelId
        """)
    QualityAttributeValueJpaEntity findByAssessmentIdAndAttributeIdAndMaturityLevelId(UUID assessmentId,
                                                                                      Long attributeId,
                                                                                      Long maturityLevelId);

    @Query("""
        SELECT av
        FROM QualityAttributeValueJpaEntity av
        LEFT JOIN AttributeJpaEntity att ON av.qualityAttributeId = att.id and av.assessmentResult.id = :resultId
        WHERE att.subject.id = :subjectId
        """)
    List<QualityAttributeValueJpaEntity> findByAssessmentResultIdAndSubjectId(UUID resultId, Long subjectId);

    @Modifying
    @Query("update QualityAttributeValueJpaEntity a set a.maturityLevelId = :maturityLevelId where a.id = :id")
    void updateMaturityLevelById(@Param(value = "id") UUID id,
                                 @Param(value = "maturityLevelId") Long maturityLevelId);

    @Modifying
    @Query("update QualityAttributeValueJpaEntity a set a.confidenceValue = :confidenceValue where a.id = :id")
    void updateConfidenceValueById(@Param(value = "id") UUID id,
                                   @Param(value = "confidenceValue") Double confidenceValue);

}
