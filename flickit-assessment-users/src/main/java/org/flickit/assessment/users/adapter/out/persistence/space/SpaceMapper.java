package org.flickit.assessment.users.adapter.out.persistence.space;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.users.space.SpaceWithDetailsView;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceListPort;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaEntity;
import org.flickit.assessment.users.application.domain.Space;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpaceMapper {

    public static SpaceJpaEntity mapToJpaEntity(Space space) {
        return new SpaceJpaEntity(
            null,
            space.getCode(),
            space.getTitle(),
            space.getOwnerId(),
            space.getCreationTime(),
            space.getLastModificationTime(),
            space.getCreatedBy(),
            space.getLastModifiedBy());
    }

    public static LoadSpaceListPort.Result mapToPortResult(SpaceWithDetailsView entity) {
        return new LoadSpaceListPort.Result(
            entity.getId(),
            entity.getTitle(),
            entity.getTitle(),
            entity.getOwnerId(),
            entity.getLastModificationTime(),
            entity.getMembersCount(),
            entity.getAssessmentsCount());
    }
}
