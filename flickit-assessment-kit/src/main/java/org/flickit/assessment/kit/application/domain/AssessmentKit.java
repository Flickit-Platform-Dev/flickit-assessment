package org.flickit.assessment.kit.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AssessmentKit {

    private List<MaturityLevel> maturityLevels;

    private boolean hasError;
}
