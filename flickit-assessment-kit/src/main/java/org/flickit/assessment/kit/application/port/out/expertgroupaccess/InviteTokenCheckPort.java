package org.flickit.assessment.kit.application.port.out.expertgroupaccess;

import java.util.UUID;

public interface InviteTokenCheckPort {

    boolean getInviteToken(UUID inviteToke);
}
