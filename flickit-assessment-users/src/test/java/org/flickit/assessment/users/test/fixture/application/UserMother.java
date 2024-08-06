package org.flickit.assessment.users.test.fixture.application;

import org.flickit.assessment.users.application.domain.User;

import java.util.UUID;

public class UserMother {

    public static User createUserWithId(UUID id) {
        return new User (id, "test@test.ir", "Display name", "bio", "linkedIn", "path/to/picture");
    }

    public static User createUserWithIdWithoutPic(UUID id) {
        return new User (id, "test@test.ir", "Display name", "bio", "linkedIn", null);

    }
}
