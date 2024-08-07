package org.flickit.assessment.core.adapter.out.novu;

import co.novu.api.common.SubscriberRequest;
import co.novu.api.events.requests.TriggerEventRequest;
import org.flickit.assessment.common.adapter.out.novu.NovuRequestConverter;
import org.flickit.assessment.core.application.service.assessmentuserrole.notification.GrantAssessmentUserRoleNotificationContent;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.common.adapter.out.novu.NotificationType.GRANT_USER_ASSESSMENT_ROLE;

@Component
public class GrantAssessmentUserRoleNovuRequestConverter
    implements NovuRequestConverter<GrantAssessmentUserRoleNotificationContent> {

    @Override
    public TriggerEventRequest convert(UUID targetUserId, GrantAssessmentUserRoleNotificationContent content) {
        var triggerEvent = new TriggerEventRequest();
        triggerEvent.setName(GRANT_USER_ASSESSMENT_ROLE.getCode());
        triggerEvent.setTo(createSubscriberRequest(targetUserId));
        triggerEvent.setPayload(Map.of("data", content));
        return triggerEvent;
    }

    private SubscriberRequest createSubscriberRequest(UUID targetUserId) {
        var subscriber = new SubscriberRequest();
        subscriber.setSubscriberId(targetUserId.toString());
        return subscriber;
    }

    @Override
    public Class<GrantAssessmentUserRoleNotificationContent> contentClass() {
        return GrantAssessmentUserRoleNotificationContent.class;
    }
}
