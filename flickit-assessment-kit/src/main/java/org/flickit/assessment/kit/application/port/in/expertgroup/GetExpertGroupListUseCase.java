package org.flickit.assessment.kit.application.port.in.expertgroup;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

import java.util.List;
import java.util.UUID;

public interface GetExpertGroupListUseCase {

    PaginatedResponse<ExpertGroupListItemFinal> getExpertGroupList(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @Min(value = 1, message = GET_EXPERT_GROUP_LIST_SIZE_MIN)
        @Max(value = 100, message = GET_EXPERT_GROUP_LIST_SIZE_MAX)
        int size;

        @Min(value = 0, message = GET_EXPERT_GROUP_LIST_PAGE_MIN)
        int page;

        UUID currentUserID;

        public Param(int size, int page, UUID currentUserID) {
            this.size = size;
            this.page = page;
            this.currentUserID = currentUserID;
            this.validateSelf();
        }
    }

    record ExpertGroupListItem(Long id, String title, String bio, String picture, Integer publishedKitsCount,
                               Integer membersCount, List<Member> members, UUID ownerId, Boolean editable) {}

    record ExpertGroupListItemFinal(Long id, String title, String bio, String picture, Integer publishedKitsCount,
                               Integer membersCount, List<Member> members, Boolean editable) {}

    record Member(String displayName){}
}
