package org.flickit.assessment.common.adapter.out.novu;

import co.novu.api.events.requests.TriggerEventRequest;
import org.flickit.assessment.common.application.domain.notification.NotificationEnvelope;
import org.flickit.assessment.common.application.domain.notification.NotificationPayload;

public interface NovuRequestConverter {

    TriggerEventRequest convert(NotificationEnvelope envelope);

    NotificationType notificationType();

    Class<? extends NotificationPayload> payloadClass();
}
