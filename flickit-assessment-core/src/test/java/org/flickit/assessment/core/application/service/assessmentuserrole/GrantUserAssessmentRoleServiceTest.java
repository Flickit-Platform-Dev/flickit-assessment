package org.flickit.assessment.core.application.service.assessmentuserrole;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.SpaceAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GrantUserAssessmentRoleUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.GrantUserAssessmentRolePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GRANT_USER_ASSESSMENT_ROLE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GRANT_ASSESSMENT_USER_ROLE_USER_ID_NOT_MEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrantUserAssessmentRoleServiceTest {

    @InjectMocks
    private GrantUserAssessmentRoleService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private SpaceAccessChecker spaceAccessChecker;

    @Mock
    private GrantUserAssessmentRolePort grantUserAssessmentRolePort;

    @Test
    void testGrantAssessmentUserRole_CurrentUserIsNotAuthorized_ThrowsException() {
        Param param = new Param(UUID.randomUUID(), UUID.randomUUID(), 1, UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE))
            .thenReturn(false);

        var exception = assertThrows(AccessDeniedException.class, () -> service.grantAssessmentUserRole(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(spaceAccessChecker, grantUserAssessmentRolePort);
    }

    @Test
    void testGrantAssessmentUserRole_UserIsNotSpaceMember_ThrowsException() {
        Param param = new Param(UUID.randomUUID(), UUID.randomUUID(), 1, UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE))
            .thenReturn(true);

        when(spaceAccessChecker.hasAccess(param.getAssessmentId(), param.getUserId())).thenReturn(false);

        var exception = assertThrows(ValidationException.class, () -> service.grantAssessmentUserRole(param));
        assertEquals(GRANT_ASSESSMENT_USER_ROLE_USER_ID_NOT_MEMBER, exception.getMessage());

        verifyNoInteractions(grantUserAssessmentRolePort);
    }

    @Test
    void testGrantAssessmentUserRole_ValidParam_GrantAccess() {
        Param param = new Param(UUID.randomUUID(), UUID.randomUUID(), 1, UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE))
            .thenReturn(true);

        when(spaceAccessChecker.hasAccess(param.getAssessmentId(), param.getUserId())).thenReturn(true);

        doNothing().when(grantUserAssessmentRolePort)
            .persist(param.getAssessmentId(), param.getUserId(), param.getRoleId());

        service.grantAssessmentUserRole(param);

        verify(grantUserAssessmentRolePort, times(1))
            .persist(param.getAssessmentId(), param.getUserId(), param.getRoleId());
    }
}
