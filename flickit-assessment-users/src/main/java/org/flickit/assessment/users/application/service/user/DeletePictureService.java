package org.flickit.assessment.users.application.service.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.port.in.user.DeletePictureUseCase;
import org.flickit.assessment.users.application.port.out.minio.DeleteFilePort;
import org.flickit.assessment.users.application.port.out.user.LoadUserPort;
import org.flickit.assessment.users.application.port.out.user.UpdateUserPicturePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeletePictureService implements DeletePictureUseCase {

    private final LoadUserPort loadUserPort;
    private final UpdateUserPicturePort updateUserPicturePort;
    private final DeleteFilePort deleteFilePort;

    @Override
    public void delete(Param param) {
        var user = loadUserPort.loadUser(param.getUserId());
        var picture = user.getPicturePath();
        if (picture != null && !picture.isBlank()) {
            updateUserPicturePort.updatePicture(user.getId(), null);
            deleteFilePort.deletePicture(picture);
        }
    }
}
