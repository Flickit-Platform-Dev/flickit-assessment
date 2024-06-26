package org.flickit.assessment.core.application.service.evidenceattachment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.FileProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.port.in.evidenceattachment.AddEvidenceAttachmentUseCase;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencePort;
import org.flickit.assessment.core.application.port.out.evidenceattachment.CountEvidenceAttachmentsPort;
import org.flickit.assessment.core.application.port.out.evidenceattachment.UploadEvidenceAttachmentPort;
import org.flickit.assessment.core.application.port.out.evidenceattachment.SaveEvidenceAttachmentPort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.flickit.assessment.core.common.ErrorMessageKey.ADD_EVIDENCE_ATTACHMENT_ATTACHMENT_COUNT_MAX;

@Service
@Transactional
@RequiredArgsConstructor
public class AddEvidenceAttachmentService implements AddEvidenceAttachmentUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    private final FileProperties fileProperties;
    private final LoadEvidencePort loadEvidencePort;
    private final SaveEvidenceAttachmentPort saveEvidenceAttachmentPort;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;
    private final CountEvidenceAttachmentsPort countEvidenceAttachmentsPort;
    private final UploadEvidenceAttachmentPort uploadEvidenceAttachmentPort;

    @Override
    public Result addAttachment(Param param) {
        var evidence = loadEvidencePort.loadNotDeletedEvidence(param.getEvidenceId());
        if (!evidence.getCreatedById().equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        validateAttachment(param.getAttachment(), param.getEvidenceId());

        String path = uploadEvidenceAttachmentPort.uploadAttachment(param.getAttachment());

        var attachmentId = saveEvidenceAttachmentPort.saveAttachment(
            param.getEvidenceId(), path, param.getDescription(), param.getCurrentUserId(), LocalDateTime.now());
        var attachmentLink = createFileDownloadLinkPort.createDownloadLink(path, EXPIRY_DURATION);

        return new Result(attachmentId, attachmentLink);
    }

    private void validateAttachment(MultipartFile attachment, UUID evidenceId) {
        if (countEvidenceAttachmentsPort.countAttachments(evidenceId) >= fileProperties.getAttachmentMaxCount())
            throw new ValidationException(ADD_EVIDENCE_ATTACHMENT_ATTACHMENT_COUNT_MAX);

        if (attachment.getSize() > fileProperties.getAttachmentMaxSize().toBytes())
            throw new ValidationException(UPLOAD_FILE_SIZE_MAX);

        if (!fileProperties.getAttachmentContentTypes().contains(attachment.getContentType()))
            throw new ValidationException(UPLOAD_FILE_FORMAT_NOT_VALID);
    }
}
