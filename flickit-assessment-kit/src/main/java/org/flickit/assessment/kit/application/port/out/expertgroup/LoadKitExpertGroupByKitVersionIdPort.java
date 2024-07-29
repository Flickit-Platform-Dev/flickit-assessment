package org.flickit.assessment.kit.application.port.out.expertgroup;

import org.flickit.assessment.kit.application.domain.ExpertGroup;

public interface LoadKitExpertGroupByKitVersionIdPort {

    ExpertGroup loadKitExpertGroupByKitVersionId(Long kitVersionId);
}
