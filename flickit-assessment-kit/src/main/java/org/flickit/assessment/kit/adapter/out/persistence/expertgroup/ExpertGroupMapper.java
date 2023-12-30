package org.flickit.assessment.kit.adapter.out.persistence.expertgroup;

import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupWithAssessmentKitCountView;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupListUseCase;

public class ExpertGroupMapper {
    public static ExpertGroup mapToDomainModel(ExpertGroupJpaEntity entity) {
        return new ExpertGroup(entity.getId());
    }

    public static GetExpertGroupListUseCase.ExpertGroupListItem mapToExpertGroupListItem(ExpertGroupWithAssessmentKitCountView entity) {
        return new GetExpertGroupListUseCase.ExpertGroupListItem(
            entity.getId(),
            entity.getName(),
            entity.getBio(),
            entity.getPicture(),
            null, entity.getPublishedKitsCount(),
            entity.getOwnerId());
    }
}
