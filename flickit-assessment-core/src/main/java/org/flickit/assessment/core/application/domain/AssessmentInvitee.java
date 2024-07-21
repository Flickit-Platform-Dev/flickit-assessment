package org.flickit.assessment.core.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class AssessmentInvitee {

    private final UUID id;
    private final UUID assessmentId;
    private final String email;
    private final int roleId;
    private final LocalDateTime expirationTime;
    private final LocalDateTime inviteTime;

    public boolean isNotExpired() {
        return expirationTime.isAfter(LocalDateTime.now());
    }
}