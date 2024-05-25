package org.flickit.assessment.core.application.service.evidence;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceListUseCase;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceListUseCase.EvidenceListItem;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceListUseCase.Param;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencesPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_EVIDENCE_LIST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetEvidenceListServiceTest {

    @InjectMocks
    private GetEvidenceListService service;

    @Mock
    private LoadEvidencesPort loadEvidencesPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Test
    void testGetEvidenceList_ResultsFound_2ItemsReturned() {
        Long question1Id = 1L;
        EvidenceListItem evidence1Q1 = createEvidence();
        EvidenceListItem evidence2Q1 = createEvidence();
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_EVIDENCE_LIST)).thenReturn(true);
        when(loadEvidencesPort.loadNotDeletedEvidences(question1Id, assessmentId, 0, 10))
            .thenReturn(new PaginatedResponse<>(
                List.of(evidence1Q1, evidence2Q1),
                0,
                2,
                "lastModificationTime",
                "DESC",
                2));

        PaginatedResponse<EvidenceListItem> result = service.getEvidenceList(new Param(question1Id, assessmentId, 10, 0, currentUserId));

        assertEquals(2, result.getItems().size());
    }

    @Test
    void testGetEvidenceList_ResultsFound_NoItemReturned() {
        Long QUESTION2_ID = 2L;
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_EVIDENCE_LIST)).thenReturn(true);
        when(loadEvidencesPort.loadNotDeletedEvidences(QUESTION2_ID, assessmentId, 0, 10))
            .thenReturn(new PaginatedResponse<>(
                new ArrayList<>(),
                0,
                0,
                "lastModificationTime",
                "DESC",
                0));

        PaginatedResponse<EvidenceListItem> result = service.getEvidenceList(new Param(QUESTION2_ID, assessmentId, 10, 0, currentUserId));

        assertEquals(0, result.getItems().size());
    }

    private EvidenceListItem createEvidence() {
        return new EvidenceListItem(
            UUID.randomUUID(),
            "desc",
            "type",
            LocalDateTime.now(),
            new GetEvidenceListUseCase.User(UUID.randomUUID(), "user1")
        );
    }
}
