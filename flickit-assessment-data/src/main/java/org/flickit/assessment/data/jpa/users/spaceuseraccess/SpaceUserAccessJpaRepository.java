package org.flickit.assessment.data.jpa.users.spaceuseraccess;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpaceUserAccessJpaRepository extends JpaRepository<SpaceUserAccessJpaEntity, Long> {

    boolean existsByUserIdAndSpaceId(Long spaceId, UUID userId);
}
