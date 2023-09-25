package org.flickit.flickitassessmentcore.application.service.assessment;

import org.flickit.flickitassessmentcore.application.port.in.assessment.DeleteAssessmentUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CheckAssessmentExistencePort;
import org.flickit.flickitassessmentcore.application.port.out.assessment.DeleteAssessmentPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.flickitassessmentcore.application.service.constant.AssessmentConstants.NOT_DELETED_DELETION_TIME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteAssessmentServiceTest {

    @InjectMocks
    private DeleteAssessmentService service;

    @Mock
    private DeleteAssessmentPort deleteAssessmentPort;

    @Mock
    private CheckAssessmentExistencePort checkAssessmentExistencePort;

    @Test
    void deleteAssessment_ValidAssessmentId_DeleteSuccessfully() {
        UUID id = UUID.randomUUID();
        DeleteAssessmentUseCase.Param param = new DeleteAssessmentUseCase.Param(id);
        when(checkAssessmentExistencePort.existsById(id)).thenReturn(true);
        doNothing().when(deleteAssessmentPort).setDeletionTimeById(any(), any());

        service.deleteAssessment(param);

        ArgumentCaptor<UUID> portIdParam = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Long> portDeletionTimeParam = ArgumentCaptor.forClass(Long.class);
        verify(deleteAssessmentPort).setDeletionTimeById(portIdParam.capture(), portDeletionTimeParam.capture());

        assertEquals(id, portIdParam.getValue());
        assertThat(portDeletionTimeParam.getValue(), not(equalTo(NOT_DELETED_DELETION_TIME)));
        long now = System.currentTimeMillis();
        assertThat(portDeletionTimeParam.getValue(), lessThanOrEqualTo(now));
        verify(checkAssessmentExistencePort, times(1)).existsById(any());
        verify(deleteAssessmentPort, times(1)).setDeletionTimeById(any(), any());
    }

    @Test
    void deleteAssessment_InvalidAssessmentId_ThrowsException() {
        UUID id = UUID.randomUUID();
        DeleteAssessmentUseCase.Param param = new DeleteAssessmentUseCase.Param(id);
        when(checkAssessmentExistencePort.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.deleteAssessment(param));

        ArgumentCaptor<UUID> portIdParam = ArgumentCaptor.forClass(UUID.class);
        verify(checkAssessmentExistencePort).existsById(portIdParam.capture());

        assertEquals(id, portIdParam.getValue());
        verify(checkAssessmentExistencePort, times(1)).existsById(any());
        verify(deleteAssessmentPort, never()).setDeletionTimeById(any(), any());
    }
}
