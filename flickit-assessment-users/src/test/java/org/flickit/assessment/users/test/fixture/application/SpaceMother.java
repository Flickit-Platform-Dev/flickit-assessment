package org.flickit.assessment.users.test.fixture.application;

import org.flickit.assessment.users.application.domain.Space;

import java.time.LocalDateTime;
import java.util.UUID;

public class SpaceMother {

    private static Long id = 134L;

    public static Space createSpace(UUID ownerId) {
        return new Space(
            id++,
            "Code" + id,
            "Title",
            ownerId,
            LocalDateTime.now(),
            LocalDateTime.now(),
            UUID.randomUUID(),
            UUID.randomUUID()
        );
    }
}
