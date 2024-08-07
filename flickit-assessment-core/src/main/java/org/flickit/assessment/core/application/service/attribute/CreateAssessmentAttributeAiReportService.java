package org.flickit.assessment.core.application.service.attribute;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.config.OpenAiProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.in.attribute.CreateAssessmentAttributeAiReportUseCase;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.CreateAttributeAiInsightPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributePort;
import org.flickit.assessment.core.application.port.out.attributeinsight.CreateAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.LoadAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.UpdateAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.attribute.CreateAttributeScoresFilePort;
import org.flickit.assessment.core.application.port.out.attributevalue.LoadAttributeValuePort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.EXPORT_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.ASSESSMENT_ID_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.MessageKey.ASSESSMENT_ATTRIBUTE_AI_IS_DISABLED;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateAssessmentAttributeAiReportService implements CreateAssessmentAttributeAiReportUseCase {

    private final GetAssessmentPort getAssessmentPort;
    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final LoadAttributeValuePort loadAttributeValuePort;
    private final LoadMaturityLevelsPort loadMaturityLevelsPort;
    private final LoadAttributePort loadAttributePort;
    private final LoadAttributeInsightPort loadAttributeInsightPort;
    private final OpenAiProperties openAiProperties;
    private final CreateAttributeInsightPort createAttributeInsightPort;
    private final CreateAttributeScoresFilePort createAttributeScoresFilePort;
    private final UpdateAttributeInsightPort updateAttributeInsightPort;
    private final CreateAttributeAiInsightPort createAttributeAiInsightPort;

    @SneakyThrows
    @Override
    public Result createAttributeAiReport(Param param) {
        var assessment = getAssessmentPort.getAssessmentById(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND));

        if (!assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(assessment.getId())
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_ASSESSMENT_RESULT_NOT_FOUND));

        validateAssessmentResultPort.validate(param.getAssessmentId());

        AttributeValue attributeValue = loadAttributeValuePort.load(assessmentResult.getId(), param.getAttributeId());
        List<MaturityLevel> maturityLevels = loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId());

        var attribute = loadAttributePort.load(param.getAttributeId(), assessmentResult.getKitVersionId());
        var attributeInsight = loadAttributeInsightPort.loadAttributeAiInsight(assessmentResult.getId(), attribute.getId());

        return attributeInsight.map(insight -> handleExistingInsight(insight, assessmentResult, attribute, attributeValue, maturityLevels))
            .orElseGet(() -> handleNewInsight(attribute, assessmentResult, attributeValue, maturityLevels));
    }

    @SneakyThrows
    private Result handleExistingInsight(AttributeInsight attributeInsight, AssessmentResult assessmentResult,
                                         Attribute attribute, AttributeValue attributeValue, List<MaturityLevel> maturityLevels) {
        if (assessmentResult.getLastCalculationTime().isBefore(attributeInsight.getAiInsightTime()))
            return new Result(attributeInsight.getAiInsight());

        if (!openAiProperties.isEnabled())
            return new Result(MessageBundle.message(ASSESSMENT_ATTRIBUTE_AI_IS_DISABLED, attribute.getTitle()));

        try (var stream = createAttributeScoresFilePort.generateFile(attributeValue, maturityLevels)) {
            String aiInsight = createAttributeAiInsightPort.generateInsight(stream, attribute);
            updateAttributeInsightPort.updateAiInsight(toAttributeInsight(assessmentResult.getId(), attribute.getId(), aiInsight));
            return new Result(aiInsight);
        }
    }

    @SneakyThrows
    private Result handleNewInsight(Attribute attribute, AssessmentResult assessmentResult,
                                    AttributeValue attributeValue, List<MaturityLevel> maturityLevels) {
        if (!openAiProperties.isEnabled())
            return new Result(MessageBundle.message(ASSESSMENT_ATTRIBUTE_AI_IS_DISABLED, attribute.getTitle()));

        try (var stream = createAttributeScoresFilePort.generateFile(attributeValue, maturityLevels)) {
            String aiInsight = createAttributeAiInsightPort.generateInsight(stream, attribute);
            createAttributeInsightPort.persist(toAttributeInsight(assessmentResult.getId(), attribute.getId(), aiInsight));
            return new Result(aiInsight);
        }
    }

    private static AttributeInsight toAttributeInsight(UUID assessmentResultId, long attributeId, String aiInsight) {
        return new AttributeInsight(assessmentResultId,
            attributeId,
            aiInsight,
            null,
            LocalDateTime.now(),
            null,
            null);
    }
}
