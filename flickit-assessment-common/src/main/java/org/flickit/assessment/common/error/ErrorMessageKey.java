package org.flickit.assessment.common.error;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessageKey {

    public static final String COMMON_CURRENT_USER_NOT_ALLOWED = "common.currentUser.notAllowed";
    public static final String COMMON_CURRENT_USER_ID_NOT_NULL = "common.currentUserId.notNull";
    public static final String COMMON_HEADER_AUTHORIZATION_NOT_NULL = "common.header.authorization.notNull";
    public static final String COMMON_CURRENT_USER_NOT_FOUND = "common.currentUser.notFound";

    public static final String COMMON_ASSESSMENT_NOT_FOUND = "common.assessment.notFound";
    public static final String COMMON_ASSESSMENT_RESULT_NOT_FOUND = "common.assessmentResult.notFound";
    public static final String COMMON_ASSESSMENT_RESULT_NOT_VALID = "common.assessmentResult.notValid";

    public static final String UPLOAD_FILE_PICTURE_SIZE_MAX = "upload-file.picture-size.max";
    public static final String UPLOAD_FILE_DSL_SIZE_MAX = "upload-file.dsl-size.max";
    public static final String UPLOAD_FILE_SIZE_MAX = "upload-file.size.max";
    public static final String UPLOAD_FILE_FORMAT_NOT_VALID = "upload-file.format.notValid";

    public static final String FILE_STORAGE_FILE_NOT_FOUND = "file-storage.file.notFound";

    public static final String COMMON_SPACE_ID_NOT_FOUND = "common.space.notFound";
}
