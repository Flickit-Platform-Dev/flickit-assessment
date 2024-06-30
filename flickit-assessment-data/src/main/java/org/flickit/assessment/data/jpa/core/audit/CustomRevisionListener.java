package org.flickit.assessment.data.jpa.core.audit;

import org.hibernate.envers.RevisionListener;

import java.time.Instant;
import java.util.UUID;

//@Service
public class CustomRevisionListener implements RevisionListener {
    @Override
    public void newRevision(Object revisionEntity) {
        CustomRevisionEntity customRevisionEntity = (CustomRevisionEntity) revisionEntity;
        // Set the user who made the change
        // Replace `getCurrentUserId` with your actual implementation to retrieve the current user
        customRevisionEntity.setChangedBy(getCurrentUserId());
        customRevisionEntity.setChangedAt(Instant.now().toEpochMilli());
    }

    private UUID getCurrentUserId() {
        // Implement this method to retrieve the current user's UUID
        // Example: from the security context or session
        return UUID.randomUUID(); // Replace with actual implementation
    }
}
