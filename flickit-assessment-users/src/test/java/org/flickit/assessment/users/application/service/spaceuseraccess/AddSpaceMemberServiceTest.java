package org.flickit.assessment.users.application.service.spaceuseraccess;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.domain.SpaceUserAccess;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.AddSpaceMemberUseCase;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CreateSpaceUserAccessPort;
import org.flickit.assessment.users.application.port.out.user.LoadUserPort;
import org.flickit.assessment.users.application.service.spaceuseraccess.notification.AddSpaceMemberNotificationCmd;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.common.ErrorMessageKey.ADD_SPACE_MEMBER_SPACE_USER_DUPLICATE;
import static org.flickit.assessment.users.common.ErrorMessageKey.USER_BY_EMAIL_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddSpaceMemberServiceTest {

    @InjectMocks
    AddSpaceMemberService service;

    @Mock
    private LoadUserPort loadUserPort;

    @Mock
    private CheckSpaceAccessPort checkSpaceAccessPort;

    @Mock
    private CreateSpaceUserAccessPort createSpaceUserAccessPort;

    @Test
    void testAddSpaceMember_validParameters_successful() {
        long spaceId = 0;
        String email = "admin@asta.org";
        UUID currentUserId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        var param = new AddSpaceMemberUseCase.Param(spaceId, email, currentUserId);
        var notificationData = new AddSpaceMemberNotificationCmd(userId,
            param.getSpaceId(),
            currentUserId);

        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(true);
        when(loadUserPort.loadUserIdByEmail(email)).thenReturn(Optional.of(userId));
        when(checkSpaceAccessPort.checkIsMember(spaceId, userId)).thenReturn(false);
        doNothing().when(createSpaceUserAccessPort).persist(isA(SpaceUserAccess.class));

        var result = assertDoesNotThrow(() -> service.addMember(param));

        AddSpaceMemberNotificationCmd cmd = (AddSpaceMemberNotificationCmd) result.notificationCmd();

        assertEquals(notificationData.targetUserId(), cmd.targetUserId());
        assertEquals(notificationData.spaceId(), cmd.spaceId());
        assertEquals(notificationData.inviterUserId(), cmd.inviterUserId());

        verify(checkSpaceAccessPort, times(2)).checkIsMember(anyLong(), any(UUID.class));
        verify(loadUserPort).loadUserIdByEmail(email);
        verify(loadUserPort).loadUserIdByEmail(email);
        verify(createSpaceUserAccessPort).persist(any(SpaceUserAccess.class));
    }

    @Test
    void testAddSpaceMember_inviterIsNotSpaceMember_AccessDeniedExceptionException() {
        long spaceId = 0;
        String email = "admin@asta.org";
        UUID currentUserId = UUID.randomUUID();
        var param = new AddSpaceMemberUseCase.Param(spaceId, email, currentUserId);

        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.addMember(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(checkSpaceAccessPort).checkIsMember(spaceId, currentUserId);
        verifyNoInteractions(loadUserPort);
        verifyNoInteractions(createSpaceUserAccessPort);
    }

    @Test
    void testAddSpaceMember_inviteeIsNotFlickitUser_ResourceNotFoundException() {
        long spaceId = 0;
        String email = "admin@asta.org";
        UUID currentUserId = UUID.randomUUID();
        var param = new AddSpaceMemberUseCase.Param(spaceId, email, currentUserId);

        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(true);
        when(loadUserPort.loadUserIdByEmail(email)).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.addMember(param));
        assertEquals(USER_BY_EMAIL_NOT_FOUND, throwable.getMessage());

        verify(checkSpaceAccessPort).checkIsMember(spaceId, currentUserId);
        verify(loadUserPort).loadUserIdByEmail(email);
        verifyNoInteractions(createSpaceUserAccessPort);
    }

    @Test
    void testAddSpaceMember_inviteeIsMember_ResourceAlreadyExistsException() {
        long spaceId = 0;
        String email = "admin@asta.org";
        UUID currentUserId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        var param = new AddSpaceMemberUseCase.Param(spaceId, email, currentUserId);

        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(true);
        when(loadUserPort.loadUserIdByEmail(email)).thenReturn(Optional.of(userId));
        when(checkSpaceAccessPort.checkIsMember(spaceId, userId)).thenReturn(true);

        var throwable = assertThrows(ResourceAlreadyExistsException.class, () -> service.addMember(param));
        assertEquals(ADD_SPACE_MEMBER_SPACE_USER_DUPLICATE, throwable.getMessage());

        verify(checkSpaceAccessPort, times(2)).checkIsMember(anyLong(), any(UUID.class));
        verify(loadUserPort).loadUserIdByEmail(email);
        verifyNoInteractions(createSpaceUserAccessPort);
    }
}
