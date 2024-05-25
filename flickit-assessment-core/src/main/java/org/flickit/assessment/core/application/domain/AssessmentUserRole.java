package org.flickit.assessment.core.application.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.*;
import static org.flickit.assessment.core.application.domain.AssessmentUserRole.PermissionGroup.*;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AssessmentUserRole {

    VIEWER("Viewer", VIEWER_PERMISSIONS),
    COMMENTER("Commenter", VIEWER_PERMISSIONS, COMMENTER_PERMISSIONS),
    ASSESSOR("Assessor", VIEWER_PERMISSIONS, COMMENTER_PERMISSIONS, ASSESSOR_PERMISSIONS),
    MANAGER("Manager", VIEWER_PERMISSIONS, COMMENTER_PERMISSIONS, ASSESSOR_PERMISSIONS, MANAGER_PERMISSIONS);

    private final String title;
    private final Set<AssessmentPermission> permissions;

    AssessmentUserRole(String title, PermissionGroup... permissionsGroups) {
        this.title = title;
        this.permissions = Arrays.stream(permissionsGroups)
            .flatMap(x -> x.getPermissions().stream())
            .collect(Collectors.toUnmodifiableSet());
    }

    public int getId() {
        return this.ordinal();
    }

    public static AssessmentUserRole valueOfById(int id) {
        if (!isValidId(id))
            return null;
        return values()[id];
    }

    public static boolean isValidId(int id) {
        return id >= 0 && id < AssessmentUserRole.values().length;
    }

    public boolean hasAccess(AssessmentPermission permission) {
        return this.getPermissions().contains(permission);
    }

    @Getter
    @RequiredArgsConstructor
    enum PermissionGroup {

        VIEWER_PERMISSIONS(Set.of(
            VIEW_REPORT_ASSESSMENT,
            CALCULATE_ASSESSMENT,
            CALCULATE_CONFIDENCE,
            VIEW_ASSESSMENT_LIST,
            VIEW_ASSESSMENT_PROGRESS,
            VIEW_ASSESSMENT,
            VIEW_SUBJECT_PROGRESS,
            VIEW_SUBJECT_REPORT)),
        COMMENTER_PERMISSIONS(Set.of(
            VIEW_ANSWER,
            ADD_EVIDENCE,
            DELETE_EVIDENCE,
            VIEW_EVIDENCE,
            VIEW_ATTRIBUTE_EVIDENCE_LIST,
            VIEW_EVIDENCE_LIST,
            UPDATE_EVIDENCE,
            VIEW_ASSESSMENT_QUESTIONNAIRE_LIST,
            VIEW_QUESTIONNAIRES_PROGRESS,
            VIEW_QUESTIONNAIRE_QUESTIONS)),
        ASSESSOR_PERMISSIONS(Set.of(
            ANSWER_QUESTION,
            VIEW_ATTRIBUTE_SCORE_DETAIL,
            CREATE_ADVICE)),
        MANAGER_PERMISSIONS(Set.of(
            CREATE_ASSESSMENT,
            DELETE_ASSESSMENT,
            UPDATE_ASSESSMENT,
            GRANT_USER_ASSESSMENT_ROLE,
            UPDATE_USER_ASSESSMENT_ROLE,
            GET_ASSESSMENT_PRIVILEGED_USERS));

        private final Set<AssessmentPermission> permissions;
    }
}
