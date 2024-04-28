package org.flickit.assessment.users.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Space {

    private final long id;
    private final String code;
    private final String title;
    private final UUID ownerId;
    private final LocalDateTime lastModificationTime;
    private final int membersCount;
    private final int assessmentsCount;
}
