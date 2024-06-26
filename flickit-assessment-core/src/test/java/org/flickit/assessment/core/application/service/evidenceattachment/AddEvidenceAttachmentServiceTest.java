package org.flickit.assessment.core.application.service.evidenceattachment;

import org.flickit.assessment.common.config.FileProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.Evidence;
import org.flickit.assessment.core.application.port.in.evidenceattachment.AddEvidenceAttachmentUseCase.Param;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencePort;
import org.flickit.assessment.core.application.port.out.evidenceattachment.CountEvidenceAttachmentsPort;
import org.flickit.assessment.core.application.port.out.evidenceattachment.UploadEvidenceAttachmentPort;
import org.flickit.assessment.core.application.port.out.evidenceattachment.SaveEvidenceAttachmentPort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.junit.jupiter.api.DisplayName;
import org.springframework.util.unit.DataSize;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.flickit.assessment.core.common.ErrorMessageKey.ADD_EVIDENCE_ATTACHMENT_ATTACHMENT_COUNT_MAX;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.flickit.assessment.core.common.ErrorMessageKey.EVIDENCE_ID_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class AddEvidenceAttachmentServiceTest {

    @InjectMocks
    private AddEvidenceAttachmentService service;

    @Mock
    private LoadEvidencePort loadEvidencePort;

    @Mock
    private UploadEvidenceAttachmentPort uploadEvidenceAttachmentPort;

    @Mock
    private SaveEvidenceAttachmentPort saveEvidenceAttachmentPort;

    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Mock
    private CountEvidenceAttachmentsPort countEvidenceAttachmentsPort;

    @Mock
    private FileProperties fileProperties;

    @Test
    @DisplayName("Adding an attachment to non-existent evidence should return NotFoundException error.")
    void addEvidenceAttachment_NonExistEvidence_notFoundException() {
        var evidenceId = UUID.randomUUID();
        var description = "Some description";
        MockMultipartFile attachment = new MockMultipartFile("attachment", "attachment.txt", "text/plain", "attachment.txt".getBytes());
        var currentUserId = UUID.randomUUID();
        var param = new Param(evidenceId, attachment, description, currentUserId);

        when(loadEvidencePort.loadNotDeletedEvidence(evidenceId)).thenThrow(new ResourceNotFoundException(EVIDENCE_ID_NOT_FOUND));
        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.addAttachment(param));

        assertEquals(EVIDENCE_ID_NOT_FOUND, throwable.getMessage(), "Should return NotFoundException error");
        verify(loadEvidencePort).loadNotDeletedEvidence(evidenceId);
        verifyNoInteractions(uploadEvidenceAttachmentPort, saveEvidenceAttachmentPort, createFileDownloadLinkPort);
    }

    @Test
    @DisplayName("Adding an attachment for an 'evidence' should be done by the creator of the evidence.")
    void addEvidenceAttachment_CurrentUserIsNotEvidenceCreator_ValidationException() {
        var evidenceId = UUID.randomUUID();
        MockMultipartFile attachment = new MockMultipartFile("attachment", "attachment.txt", "text/plain", "attachment.txt".getBytes());
        var currentUserId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var time = LocalDateTime.now();
        var param = new Param(evidenceId, attachment, null, currentUserId);
        var evidence = new Evidence(evidenceId, "des", userId, userId, UUID.randomUUID(),
            0L, 1, time, time, false);

        when(loadEvidencePort.loadNotDeletedEvidence(evidenceId)).thenReturn(evidence);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.addAttachment(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage(), "Should return ValidationException error");

        verify(loadEvidencePort).loadNotDeletedEvidence(evidenceId);
        verifyNoInteractions(uploadEvidenceAttachmentPort, saveEvidenceAttachmentPort, createFileDownloadLinkPort);
    }

    @Test
    @DisplayName("Adding an attachment for an 'evidence' with valid parameters should cause saving the attachment")
    void addEvidenceAttachment_ValidParameters_savingTheAttachment() {
        var evidenceId = UUID.randomUUID();
        var attachmentId = UUID.randomUUID();
        var description = "Some description";
        var filePath = "path/to/attachment.txt";
        var link = "http://link/to/attachment.txt/whith/expiray/date";
        var uniqueFileNme = UUID.randomUUID();
        MockMultipartFile attachment = new MockMultipartFile("attachment", "attachment.txt", "text/plain", "attachment.txt".getBytes());
        var currentUserId = UUID.randomUUID();
        var time = LocalDateTime.now();
        var param = new Param(evidenceId, attachment, description, currentUserId);
        var evidence = new Evidence(evidenceId, "des", currentUserId, currentUserId, uniqueFileNme,
            0L, 1, time, time, false);

        ArgumentCaptor<LocalDateTime> timeArgumentCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

        when(loadEvidencePort.loadNotDeletedEvidence(evidenceId)).thenReturn(evidence);
        when(fileProperties.getAttachmentMaxCount()).thenReturn(5);
        when(countEvidenceAttachmentsPort.countAttachments(evidenceId)).thenReturn(4);
        when(fileProperties.getAttachmentMaxSize()).thenReturn(DataSize.ofMegabytes(5));
        when(fileProperties.getAttachmentContentTypes()).thenReturn(List.of("text/plain"));
        when(uploadEvidenceAttachmentPort.uploadAttachment(attachment)).thenReturn(filePath);
        when(saveEvidenceAttachmentPort.saveAttachment(eq(evidenceId), eq(filePath), eq(description), eq(currentUserId), timeArgumentCaptor.capture())).thenReturn(attachmentId);
        when(createFileDownloadLinkPort.createDownloadLink(filePath, Duration.ofDays(1))).thenReturn(link);

        var result = service.addAttachment(param);
        assertEquals(attachmentId, result.attachmentId(), "Attachment id should match");
        assertEquals(link, result.attachmentLink(), "Link should match");

        verify(loadEvidencePort).loadNotDeletedEvidence(evidenceId);
        verify(uploadEvidenceAttachmentPort).uploadAttachment(attachment);
        verify(createFileDownloadLinkPort).createDownloadLink(filePath, Duration.ofDays(1));
        verify(saveEvidenceAttachmentPort).saveAttachment(eq(evidenceId), eq(filePath), eq(description), eq(currentUserId), timeArgumentCaptor.capture());
    }

    @Test
    @DisplayName("Adding an attachment for an evidence should be bound to the maximum number of attachments.")
    void addEvidenceAttachment_exceedMaxCountAttachments_ValidationError() {
        var evidenceId = UUID.randomUUID();
        var description = "Some description";
        MockMultipartFile attachment = new MockMultipartFile("attachment", "attachment.txt", "text/plain", "attachment.txt".getBytes());
        var currentUserId = UUID.randomUUID();
        var time = LocalDateTime.now();
        var param = new Param(evidenceId, attachment, description, currentUserId);
        var evidence = new Evidence(evidenceId, "des", currentUserId, currentUserId, UUID.randomUUID(),
            0L, 1, time, time, false);

        when(loadEvidencePort.loadNotDeletedEvidence(evidenceId)).thenReturn(evidence);
        when(fileProperties.getAttachmentMaxCount()).thenReturn(5);
        when(countEvidenceAttachmentsPort.countAttachments(evidenceId)).thenReturn(5);

        var throwable = assertThrows(ValidationException.class, () -> service.addAttachment(param),
            "When the attachments are more than the predefined maximum count, adding an attachment should fail with ValidationException.");

        assertEquals(ADD_EVIDENCE_ATTACHMENT_ATTACHMENT_COUNT_MAX, throwable.getMessage());
        verifyNoInteractions(uploadEvidenceAttachmentPort, saveEvidenceAttachmentPort, createFileDownloadLinkPort);
    }

    @Test
    @DisplayName("Adding an attachment for an 'evidence' should be bound to the maximum size of attachments.")
    void addEvidenceAttachment_exceedMaxMaxSize_ValidationError() {
        var evidenceId = UUID.randomUUID();
        var description = "Some description";
        MockMultipartFile attachment = new MockMultipartFile("attachment", "attachment.txt", "text/plain", new byte[6 * 1024 * 1024]);
        var currentUserId = UUID.randomUUID();
        var time = LocalDateTime.now();
        var param = new Param(evidenceId, attachment, description, currentUserId);
        var evidence = new Evidence(evidenceId, "des", currentUserId, currentUserId, UUID.randomUUID(),
            0L, 1, time, time, false);

        when(loadEvidencePort.loadNotDeletedEvidence(evidenceId)).thenReturn(evidence);
        when(fileProperties.getAttachmentMaxCount()).thenReturn(5);
        when(countEvidenceAttachmentsPort.countAttachments(evidenceId)).thenReturn(4);
        when(fileProperties.getAttachmentMaxSize()).thenReturn(DataSize.ofMegabytes(5));

        var throwable = assertThrows(ValidationException.class, () -> service.addAttachment(param),
            "When the attachments are more than the predefined maximum file size, adding an attachment should fail with ValidationException");

        assertEquals(UPLOAD_FILE_SIZE_MAX, throwable.getMessage());
        verifyNoInteractions(uploadEvidenceAttachmentPort, saveEvidenceAttachmentPort, createFileDownloadLinkPort);
    }

    @Test
    @DisplayName("Adding an attachment for an 'evidence' should be bound to predefined content types.")
    void addEvidenceAttachment_InvalidContentType_ValidationError() {
        var evidenceId = UUID.randomUUID();
        var description = "Some description";
        MockMultipartFile attachment = new MockMultipartFile("attachment", "attachment.txt", "video/mp4", "attachment.txt".getBytes());
        var currentUserId = UUID.randomUUID();
        var time = LocalDateTime.now();
        var param = new Param(evidenceId, attachment, description, currentUserId);
        var evidence = new Evidence(evidenceId, "des", currentUserId, currentUserId, UUID.randomUUID(),
            0L, 1, time, time, false);

        when(loadEvidencePort.loadNotDeletedEvidence(evidenceId)).thenReturn(evidence);
        when(fileProperties.getAttachmentMaxCount()).thenReturn(5);
        when(countEvidenceAttachmentsPort.countAttachments(evidenceId)).thenReturn(4);
        when(fileProperties.getAttachmentMaxSize()).thenReturn(DataSize.ofMegabytes(5));
        when(fileProperties.getAttachmentContentTypes()).thenReturn(List.of("text/plain"));

        var throwable = assertThrows(ValidationException.class, () -> service.addAttachment(param),
            "When an attachment does not have a valid content type, adding an attachment should fail with ValidationException.");

        assertEquals(UPLOAD_FILE_FORMAT_NOT_VALID, throwable.getMessage());
        verifyNoInteractions(uploadEvidenceAttachmentPort, saveEvidenceAttachmentPort, createFileDownloadLinkPort);
    }
}
