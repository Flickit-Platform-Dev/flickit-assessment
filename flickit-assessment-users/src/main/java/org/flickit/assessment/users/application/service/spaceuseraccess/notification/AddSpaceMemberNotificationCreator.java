package org.flickit.assessment.users.application.service.spaceuseraccess.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.domain.notification.NotificationCreator;
import org.flickit.assessment.common.application.domain.notification.NotificationEnvelope;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceDetailsPort;
import org.flickit.assessment.users.application.service.spaceuseraccess.notification.AddSpaceMemberNotificationPayload.*;
import org.flickit.assessment.users.application.port.out.user.LoadUserPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddSpaceMemberNotificationCreator implements
    NotificationCreator<AddSpaceMemberNotificationCmd> {

    private final LoadSpaceDetailsPort loadSpaceDetailsPort;
    private final LoadUserPort loadUserPort;

    @Override
    public List<NotificationEnvelope> create(AddSpaceMemberNotificationCmd cmd) {
        var user = loadUserPort.loadUser(cmd.inviterUserId());
        var space = loadSpaceDetailsPort.loadSpace(cmd.spaceId());

        if (user == null || space == null) {
            log.error("User or space are not found");
            return List.of();
        }
        return List.of(new NotificationEnvelope(cmd.targetUserId(), new AddSpaceMemberNotificationPayload(
            new SpaceModel(space.space().getId(), space.space().getTitle()),
            new UserModel(user.getId(), user.getDisplayName())
        )));
    }

    @Override
    public Class<AddSpaceMemberNotificationCmd> cmdClass() {
        return AddSpaceMemberNotificationCmd.class;
    }
}
