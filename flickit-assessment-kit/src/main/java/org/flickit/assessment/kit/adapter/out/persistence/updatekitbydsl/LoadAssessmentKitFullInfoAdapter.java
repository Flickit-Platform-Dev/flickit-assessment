package org.flickit.assessment.kit.adapter.out.persistence.updatekitbydsl;

import lombok.AllArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaRepository;
import org.flickit.assessment.data.jpa.kit.asnweroptionimpact.AnswerOptionImpactJpaRepository;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaRepository;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.flickit.assessment.kit.adapter.out.persistence.answeroption.AnswerOptionMapper;
import org.flickit.assessment.kit.adapter.out.persistence.answeroptionimpact.AnswerOptionImpactMapper;
import org.flickit.assessment.kit.adapter.out.persistence.attribute.AttributeMapper;
import org.flickit.assessment.kit.adapter.out.persistence.levelcompetence.MaturityLevelCompetenceMapper;
import org.flickit.assessment.kit.adapter.out.persistence.maturitylevel.MaturityLevelMapper;
import org.flickit.assessment.kit.adapter.out.persistence.question.QuestionMapper;
import org.flickit.assessment.kit.adapter.out.persistence.questionimpact.QuestionImpactMapper;
import org.flickit.assessment.kit.adapter.out.persistence.questionnaire.QuestionnaireMapper;
import org.flickit.assessment.kit.adapter.out.persistence.subject.SubjectMapper;
import org.flickit.assessment.kit.application.domain.*;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitFullInfoPort;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;

@Component
@AllArgsConstructor
public class LoadAssessmentKitFullInfoAdapter implements
    LoadAssessmentKitFullInfoPort {

    private final AssessmentKitJpaRepository repository;
    private final MaturityLevelJpaRepository maturityLevelRepository;
    private final LevelCompetenceJpaRepository levelCompetenceRepository;
    private final SubjectJpaRepository subjectRepository;
    private final AttributeJpaRepository attributeRepository;
    private final QuestionnaireJpaRepository questionnaireRepository;
    private final QuestionJpaRepository questionRepository;
    private final QuestionImpactJpaRepository questionImpactRepository;
    private final AnswerOptionImpactJpaRepository answerOptionImpactRepository;
    private final AnswerOptionJpaRepository answerOptionRepository;

    @Override
    public AssessmentKit load(Long kitId) {
        AssessmentKitJpaEntity entity = repository.findById(kitId).orElseThrow(
            () -> new ResourceNotFoundException(KIT_ID_NOT_FOUND));
        Long kitVersionId = entity.getKitVersionId();

        List<Subject> subjects = subjectRepository.findAllByKitVersionIdOrderByIndex(kitVersionId).stream()
            .map(e -> {
                List<Attribute> attributes = attributeRepository.findAllBySubjectIdAndKitVersionId(e.getId(), kitVersionId).stream()
                    .map(AttributeMapper::mapToDomainModel)
                    .toList();
                return SubjectMapper.mapToDomainModel(e, attributes);})
            .toList();

        List<MaturityLevel> levels = maturityLevelRepository.findAllByKitVersionIdOrderByIndex(kitVersionId).stream()
            .map(MaturityLevelMapper::mapToDomainModel)
            .toList();
        setLevelCompetences(levels, kitVersionId);

        List<Question> questions = questionRepository.findAllByKitVersionId(kitVersionId).stream()
            .map(QuestionMapper::mapToDomainModel)
            .toList();

        Map<Long, List<AnswerOptionJpaEntity>> answerRangeIdToAnswerOptionsMap = answerOptionRepository
            .findAllByKitVersionId(kitVersionId, Sort.by(AnswerOptionJpaEntity.Fields.index)).stream()
            .collect(Collectors.groupingBy(AnswerOptionJpaEntity::getAnswerRangeId, LinkedHashMap::new, Collectors.toList()));

        setQuestionImpacts(questions, kitVersionId, answerRangeIdToAnswerOptionsMap);
        setQuestionOptions(questions, answerRangeIdToAnswerOptionsMap);

        List<Questionnaire> questionnaires = questionnaireRepository.findAllByKitVersionIdOrderByIndex(kitVersionId).stream()
            .map(QuestionnaireMapper::mapToDomainModel)
            .toList();
        setQuestions(questionnaires, questions);

        return new AssessmentKit(
            kitId,
            entity.getCode(),
            entity.getTitle(),
            entity.getSummary(),
            entity.getAbout(),
            entity.getCreationTime(),
            entity.getLastModificationTime(),
            entity.getPublished(),
            entity.getIsPrivate(),
            entity.getExpertGroupId(),
            subjects,
            levels,
            questionnaires,
            kitVersionId);
    }

    private void setLevelCompetences(List<MaturityLevel> levels, Long kitVersionId) {
        levels.forEach(level -> level.setCompetences(
            levelCompetenceRepository.findByAffectedLevelIdAndKitVersionId(level.getId(), kitVersionId).stream()
                .map(MaturityLevelCompetenceMapper::mapToDomainModel)
                .toList()));
    }

    private void setQuestionImpacts(List<Question> questions, long kitVersionId, Map<Long, List<AnswerOptionJpaEntity>> answerRangeIdToAnswerOptionsMap) {
        questions.forEach(question -> question.setImpacts(
            questionImpactRepository.findAllByQuestionIdAndKitVersionId(question.getId(), kitVersionId).stream()
                .map(QuestionImpactMapper::mapToDomainModel)
                .map(impact -> setOptionImpacts(impact, answerRangeIdToAnswerOptionsMap.get(question.getAnswerRangeId())))
                .toList()
        ));
    }

    private void setQuestionOptions(List<Question> questions, Map<Long, List<AnswerOptionJpaEntity>> answerRangeIdToAnswerOptionsMap) {
        questions.forEach(q -> q.setOptions(answerRangeIdToAnswerOptionsMap.get(q.getAnswerRangeId()).stream()
            .map(AnswerOptionMapper::mapToDomainModel)
            .toList()));
    }

    private QuestionImpact setOptionImpacts(QuestionImpact impact, List<AnswerOptionJpaEntity> answerOptions) {
        Map<Long, Double> optionIdToValueMap = answerOptions.stream()
            .collect(toMap(AnswerOptionJpaEntity::getId, AnswerOptionJpaEntity::getValue));
        impact.setOptionImpacts(
            answerOptionImpactRepository.findAllByQuestionImpactIdAndKitVersionId(impact.getId(), impact.getKitVersionId()).stream()
                .map(entity -> AnswerOptionImpactMapper.mapToDomainModel(entity, optionIdToValueMap.get(entity.getOptionId())))
                .toList()
        );
        return impact;
    }

    private void setQuestions(List<Questionnaire> questionnaires, List<Question> questions) {
        Map<Long, List<Question>> groupedQuestions = questions.stream().collect(Collectors.groupingBy(Question::getQuestionnaireId));
        questionnaires.forEach(q -> q.setQuestions(groupedQuestions.get(q.getId())));
    }
}
