package org.flickit.assessment.advice.adapter.in.rest.adviceitem;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.in.adviceitem.UpdateAdviceItemUseCase;
import org.flickit.assessment.advice.application.port.in.adviceitem.UpdateAdviceItemUseCase.*;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateAdviceItemRestController {

    private final UserContext userContext;
    private final UpdateAdviceItemUseCase useCase;

    @PutMapping("/assessments/{assessmentId}/advice-items/{adviceItemId}")
    ResponseEntity<Void> updateAdviceItem(@PathVariable("assessmentId") UUID assessmentId,
                                          @PathVariable("adviceItemId") UUID adviceItemId,
                                          @RequestBody UpdateAdviceItemRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.updateAdviceItem(toParam(assessmentId, adviceItemId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, UUID adviceItemId, UpdateAdviceItemRequestDto requestDto, UUID currentUserId) {
        return new Param(adviceItemId,
            assessmentId,
            requestDto.title(),
            requestDto.description(),
            requestDto.cost(),
            requestDto.priority(),
            requestDto.impact(),
            currentUserId);
    }
}
