package org.flickit.assessment.users.application.service.spaceuseraccess.notification;

import org.flickit.assessment.common.application.domain.notification.NotificationPayload;

import java.util.UUID;

public record AddSpaceMemberNotificationPayload(SpaceModel spaceMode, UserModel inviter) implements NotificationPayload {

    public record SpaceModel(Long id, String title) {
    }

    public record UserModel(UUID id, String displayName) {
    }
}
