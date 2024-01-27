package org.flickit.assessment.kit.application.service.kitdsl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitDownloadLinkUseCase;
import org.flickit.assessment.kit.application.port.out.kitdsl.CheckIsMemberPort;
import org.flickit.assessment.kit.application.port.out.kitdsl.CreateFileDownloadLinkPort;
import org.flickit.assessment.kit.application.port.out.kitdsl.LoadDslFilePathPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_DSL_FILE_PATH_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitDslDownloadLinkService implements GetKitDownloadLinkUseCase {

    private final LoadDslFilePathPort loadDslFilePathPort;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;
    private final CheckIsMemberPort checkIsMemberPort;
    private final Duration EXPIRY_DURATION = Duration.ofHours(1);

    @SneakyThrows
    @Override
    public String getKitLink(Param param) {

        if (!(checkIsMemberPort.checkIsMemberByKitId(param.getKitId(), param.getCurrentUserId())))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        String filePath = loadDslFilePathPort.loadDslFilePath(param.getKitId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_KIT_DSL_FILE_PATH_NOT_FOUND));

        return createFileDownloadLinkPort.createDownloadLink(filePath, EXPIRY_DURATION);
    }
}
