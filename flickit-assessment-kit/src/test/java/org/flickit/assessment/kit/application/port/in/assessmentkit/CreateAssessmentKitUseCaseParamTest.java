package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateAssessmentKitUseCaseParamTest {

    @Test
    void testCreateAssessmentKitUseCaseParam_titleParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(null)));
        assertThat(throwable).hasMessage("title: " + CREATE_ASSESSMENT_KIT_TITLE_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title("  ab  ")));
        assertThat(throwable).hasMessage("title: " + CREATE_ASSESSMENT_KIT_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(RandomStringUtils.randomAlphanumeric(101))));
        assertThat(throwable).hasMessage("title: " + CREATE_ASSESSMENT_KIT_TITLE_SIZE_MAX);
    }


    @Test
    void testCreateAssessmentKitUseCaseParam_summaryParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.summary(null)));
        assertThat(throwable).hasMessage("summary: " + CREATE_ASSESSMENT_KIT_SUMMARY_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.summary("  ab  ")));
        assertThat(throwable).hasMessage("summary: " + CREATE_ASSESSMENT_KIT_SUMMARY_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.summary(RandomStringUtils.randomAlphanumeric(1001))));
        assertThat(throwable).hasMessage("summary: " + CREATE_ASSESSMENT_KIT_SUMMARY_SIZE_MAX);
    }

    @Test
    void testCreateAssessmentKitUseCaseParam_aboutParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.about(null)));
        assertThat(throwable).hasMessage("about: " + CREATE_ASSESSMENT_KIT_ABOUT_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.about("  ab  ")));
        assertThat(throwable).hasMessage("about: " + CREATE_ASSESSMENT_KIT_ABOUT_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.about(RandomStringUtils.randomAlphanumeric(1001))));
        assertThat(throwable).hasMessage("about: " + CREATE_ASSESSMENT_KIT_ABOUT_SIZE_MAX);
    }

    @Test
    void testCreateAssessmentKitUseCaseParam_isPrivateIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.isPrivate(null)));
        assertThat(throwable).hasMessage("isPrivate: " + CREATE_ASSESSMENT_KIT_IS_PRIVATE_NOT_NULL);
    }

    @Test
    void testCreateAssessmentKitUseCaseParam_expertGroupIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.expertGroupId(null)));
        assertThat(throwable).hasMessage("expertGroupId: " + CREATE_ASSESSMENT_KIT_EXPERT_GROUP_ID_NOT_NULL);
    }

    @Test
    void testCreateAssessmentKitUseCaseParam_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<CreateAssessmentKitUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private CreateAssessmentKitUseCase.Param.ParamBuilder paramBuilder() {
        return CreateAssessmentKitUseCase.Param.builder()
            .title("Enterprise")
            .summary("summary")
            .about("about")
            .isPrivate(true)
            .expertGroupId(123L)
            .currentUserId(UUID.randomUUID());
    }
}