package org.flickit.assessment.kit.adapter.out.persistence.kitversion;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.kitversion.KitVersionJpaEntity;
import org.flickit.assessment.kit.adapter.out.persistence.assessmentkit.AssessmentKitMapper;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;
import org.flickit.assessment.kit.application.port.out.kitversion.CreateKitVersionPort;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KitVersionMapper {

    public static KitVersionJpaEntity createParamToJpaEntity(AssessmentKitJpaEntity kit, CreateKitVersionPort.Param param) {
        return new KitVersionJpaEntity(
            null,
            kit,
            param.status().getId(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            param.createdBy(),
            param.createdBy()
        );
    }

    public static KitVersion mapToDomainModel(KitVersionJpaEntity entity) {
        return new KitVersion(entity.getId(),
            AssessmentKitMapper.mapToDomainModel(entity.getKit()),
            KitVersionStatus.valueOfById(entity.getStatus()),
            entity.getCreationTime(),
            entity.getLastModificationTime(),
            entity.getCreatedBy(),
            entity.getLastModifiedBy());
    }
}
