package org.flickit.assessment.users.application.service.user;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.port.in.user.DeleteUserPictureUseCase.Param;
import org.flickit.assessment.users.application.port.out.minio.DeleteFilePort;
import org.flickit.assessment.users.application.port.out.user.LoadUserPort;
import org.flickit.assessment.users.application.port.out.user.UpdateUserPicturePort;
import org.flickit.assessment.users.test.fixture.application.UserMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.USER_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserPictureServiceTest {

    @InjectMocks
    DeleteUserPictureService service;

    @Mock
    LoadUserPort loadUserPort;

    @Mock
    UpdateUserPicturePort updateUserPicturePort;

    @Mock
    DeleteFilePort deleteFilePort;

    @Test
    void testDeleteUserPicture_UserNotFound_ReturnNotFoundException() {
        var userId = UUID.randomUUID();
        var param = new Param(userId);

        when(loadUserPort.loadUser(any(UUID.class))).thenThrow(new ResourceNotFoundException(USER_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.delete(param));
        assertEquals(USER_ID_NOT_FOUND, throwable.getMessage());

        verify(loadUserPort).loadUser(userId);
        verifyNoInteractions(updateUserPicturePort, deleteFilePort);
    }

    @Test
    void testDeleteUserPicture_ValidParameters_Successful() {
        var userId = UUID.randomUUID();
        var param = new Param(userId);
        var user = UserMother.createUserWithId(userId);

        when(loadUserPort.loadUser(any(UUID.class))).thenReturn(user);
        doNothing().when(updateUserPicturePort).updatePicture(userId, null);
        doNothing().when(deleteFilePort).deletePicture(user.getPicturePath());


        assertDoesNotThrow(() -> service.delete(param));

        verify(loadUserPort).loadUser(userId);
        verify(updateUserPicturePort).updatePicture(userId, null);
        verify(deleteFilePort).deletePicture(user.getPicturePath());
    }

    @Test
    void testDeleteUserPicture_ValidParametersWithoutPicture_Successful() {
        var userId = UUID.randomUUID();
        var param = new Param(userId);
        var user = UserMother.createUserWithIdWithoutPic(userId);

        when(loadUserPort.loadUser(any(UUID.class))).thenReturn(user);

        assertDoesNotThrow(() -> service.delete(param));

        verify(loadUserPort).loadUser(userId);
        verifyNoInteractions(updateUserPicturePort, deleteFilePort);
    }
}
