package org.flickit.assessment.users.adapter.in.rest.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.user.DeleteUserPictureUseCase;
import org.flickit.assessment.users.application.port.in.user.DeleteUserPictureUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteUserPictureRestController {

    private final DeleteUserPictureUseCase useCase;
    private final UserContext userContext;

    @DeleteMapping("/users/picture")
    public ResponseEntity<Void> deleteUserProfilePicture() {
        var currentUserId = userContext.getUser().id();
        useCase.delete(toParam(currentUserId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    Param toParam(UUID currentUserId) {
        return new Param(currentUserId);
    }
}
