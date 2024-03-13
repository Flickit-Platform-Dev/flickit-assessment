package org.flickit.assessment.kit.adapter.out.persistence.expertgroupaccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.expertgroupaccess.*;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("kitExpertGroupAccessPersistenceJpaAdapter")
@RequiredArgsConstructor
public class ExpertGroupAccessPersistenceJpaAdapter implements
    CheckExpertGroupAccessPort {

    private final ExpertGroupAccessJpaRepository repository;

    @Override
    public boolean checkIsMember(long expertGroupId, UUID userId) {
        return repository.existsByExpertGroupIdAndUserId(expertGroupId, userId);
    }
}
