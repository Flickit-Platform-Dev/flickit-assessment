package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import org.flickit.assessment.kit.application.domain.KitTag;

import java.util.List;

public record EditKitInfoResponseDto(String title,
                                     String summary,
                                     Boolean isActive,
                                     Boolean isPrivate,
                                     Double price,
                                     String about,
                                     List<KitTag> tags) {
}


