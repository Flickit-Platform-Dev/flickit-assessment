package org.flickit.assessment.kit.adapter.in.rest.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.questionnaire.GetKitQuestionnaireUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetKitQuestionnaireRestController {

    private final GetKitQuestionnaireUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessment-kits/questionnaires/{questionnaireId}")
    public ResponseEntity<GetKitQuestionnaireResponseDto> getKitQuestionnaireDetail(@PathVariable("questionnaireId") Long questionnaireId) {
        UUID currentUserId = userContext.getUser().id();
        var response = useCase.getKitQuestionnaire(toParam(questionnaireId, currentUserId));
        GetKitQuestionnaireResponseDto responseDto = toResponseDto(response);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    private GetKitQuestionnaireUseCase.Param toParam(Long questionnaireId, UUID currentUserId) {
        return new GetKitQuestionnaireUseCase.Param(questionnaireId, currentUserId);
    }

    private GetKitQuestionnaireResponseDto toResponseDto(GetKitQuestionnaireUseCase.Result result) {
        return new GetKitQuestionnaireResponseDto(result.id(), result.title());
    }
}
