package org.flickit.assessment.users.adapter.out.persistence.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.domain.Space;
import org.flickit.assessment.users.application.port.out.LoadSpacePort;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.flickit.assessment.users.adapter.out.persistence.space.SpaceMapper.mapToDomainModel;
import static org.flickit.assessment.users.common.ErrorMessageKey.SPACE_ID_NOT_FOUND;


@Component
@RequiredArgsConstructor
public class SpacePersistenceJpaAdapter implements LoadSpacePort {

    private final SpaceJpaRepository repository;

    @Override
    public Space loadSpace(long id, UUID currentUserId) {
        var entity = repository.findById(id, currentUserId)
            .orElseThrow(() -> new ResourceNotFoundException(SPACE_ID_NOT_FOUND));
        return mapToDomainModel(entity);
    }
}
