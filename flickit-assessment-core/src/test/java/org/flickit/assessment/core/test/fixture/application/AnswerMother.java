package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.AnswerOption;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;

import java.util.List;
import java.util.UUID;

public class AnswerMother {

    public static Answer answer(AnswerOption option) {
        return new Answer(UUID.randomUUID(), option, 1L, ConfidenceLevel.getDefault().getId(), Boolean.FALSE);
    }

    public static Answer fullScoreOnLevels23(long attributeId) {
        AnswerOption selectedOption = AnswerOptionMother.withImpacts(List.of(
            AnswerOptionImpactMother.onLevelTwoOfAttributeId(1, attributeId),
            AnswerOptionImpactMother.onLevelThreeOfAttributeId(1, attributeId)));
        return new Answer(UUID.randomUUID(),
            selectedOption,
            1L,
            ConfidenceLevel.COMPLETELY_UNSURE.getId(),
            Boolean.FALSE);
    }

    public static Answer partialScoreOnLevel2AndFullScoreOnLevel3(long attributeId) {
        AnswerOption selectedOption = AnswerOptionMother.withImpacts(List.of(
            AnswerOptionImpactMother.onLevelTwoOfAttributeId(0.7, attributeId),
            AnswerOptionImpactMother.onLevelThreeOfAttributeId(1, attributeId)));
        return new Answer(UUID.randomUUID(),
            selectedOption,
            1L,
            ConfidenceLevel.COMPLETELY_UNSURE.getId(),
            Boolean.FALSE);
    }

    public static Answer fullScoreOnLevels24(long attributeId) {
        AnswerOption selectedOption = AnswerOptionMother.withImpacts(List.of(
            AnswerOptionImpactMother.onLevelTwoOfAttributeId(1, attributeId),
            AnswerOptionImpactMother.onLevelFourOfAttributeId(1, attributeId)));
        return new Answer(UUID.randomUUID(),
            selectedOption,
            1L,
            ConfidenceLevel.SOMEWHAT_UNSURE.getId(),
            Boolean.FALSE);
    }

    public static Answer noScoreOnLevel4(long attributeId) {
        AnswerOption selectedOption = AnswerOptionMother.withImpacts(List.of(
            AnswerOptionImpactMother.onLevelFourOfAttributeId(0, attributeId)));
        return new Answer(UUID.randomUUID(),
            selectedOption,
            1L,
            ConfidenceLevel.FAIRLY_SURE.getId(),
            Boolean.FALSE);
    }

    public static Answer fullScoreOnLevel4AndNoScoreOnLevel5(long attributeId) {
        AnswerOption selectedOption = AnswerOptionMother.withImpacts(List.of(
            AnswerOptionImpactMother.onLevelFourOfAttributeId(1, attributeId),
            AnswerOptionImpactMother.onLevelFiveOfAttributeId(0, attributeId)));
        return new Answer(UUID.randomUUID(),
            selectedOption,
            1L,
            ConfidenceLevel.FAIRLY_SURE.getId(),
            Boolean.FALSE);
    }

    public static Answer fullScoreOnLevels34(long attributeId) {
        AnswerOption selectedOption = AnswerOptionMother.withImpacts(List.of(
            AnswerOptionImpactMother.onLevelThreeOfAttributeId(1, attributeId),
            AnswerOptionImpactMother.onLevelFourOfAttributeId(1, attributeId)));
        return new Answer(UUID.randomUUID(),
            selectedOption,
            1L,
            ConfidenceLevel.FAIRLY_SURE.getId(),
            Boolean.FALSE);
    }

    public static Answer fullScoreOnLevels45(long attributeId) {
        AnswerOption selectedOption = AnswerOptionMother.withImpacts(List.of(
            AnswerOptionImpactMother.onLevelFourOfAttributeId(1, attributeId),
            AnswerOptionImpactMother.onLevelFiveOfAttributeId(1, attributeId)));
        return new Answer(UUID.randomUUID(),
            selectedOption,
            1L,
            ConfidenceLevel.COMPLETELY_SURE.getId(),
            Boolean.FALSE);
    }

    public static Answer answerWithNullNotApplicable(AnswerOption option) {
        return new Answer(UUID.randomUUID(), option, 1L, ConfidenceLevel.getDefault().getId(), null);
    }

    public static Answer answerWithNotApplicableFalse(AnswerOption option) {
        return new Answer(UUID.randomUUID(), option, 1L, ConfidenceLevel.getDefault().getId(), Boolean.FALSE);
    }

    public static Answer answerWithNotApplicableTrue(AnswerOption option) {
        Integer confidenceLevelId = ConfidenceLevel.getDefault().getId();
        return new Answer(UUID.randomUUID(), option, 1L, confidenceLevelId, Boolean.TRUE);
    }

    public static Answer answerWithQuestionIdAndNotApplicableTrue(long questionId) {
        return new Answer(UUID.randomUUID(), null, questionId, ConfidenceLevel.getDefault().getId(), Boolean.TRUE);
    }

    public static Answer answerWithConfidenceLevel(int confidenceLevelId, Long questionId, long attributeId) {
        AnswerOption selectedOption = AnswerOptionMother.withImpacts(List.of(
            AnswerOptionImpactMother.onLevelFiveOfAttributeId(1, attributeId)));
        return new Answer(UUID.randomUUID(),
            selectedOption,
            questionId,
            confidenceLevelId,
            Boolean.FALSE);
    }

    public static Answer fullScoreOnLevel3AndAnotherAttributeLevel4(long attributeId, long anotherAttributeId) {
        AnswerOption selectedOption = AnswerOptionMother.withImpacts(List.of(
            AnswerOptionImpactMother.onLevelThreeOfAttributeId(1, attributeId),
            AnswerOptionImpactMother.onLevelFourOfAttributeId(1, anotherAttributeId)));
        return new Answer(UUID.randomUUID(),
            selectedOption,
            1L,
            ConfidenceLevel.COMPLETELY_SURE.getId(),
            Boolean.FALSE);
    }

    public static Answer noScoreOnLevel3AndFullScoreOnAnotherAttributeLevel4(long attributeId, long anotherAttributeId) {
        AnswerOption selectedOption = AnswerOptionMother.withImpacts(List.of(
            AnswerOptionImpactMother.onLevelThreeOfAttributeId(0, attributeId),
            AnswerOptionImpactMother.onLevelFourOfAttributeId(1, anotherAttributeId)));
        return new Answer(UUID.randomUUID(),
            selectedOption,
            1L,
            ConfidenceLevel.COMPLETELY_SURE.getId(),
            Boolean.FALSE);
    }
}
