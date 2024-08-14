package org.flickit.assessment.users.application.service.spaceuseraccess.notification;

import org.flickit.assessment.common.application.domain.notification.NotificationCmd;

import java.util.UUID;

public record AddSpaceMemberNotificationCmd(UUID targetUserId,
                                            Long spaceId,
                                            UUID inviterUserId) implements NotificationCmd {
}
