package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitListUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CountKitListStatsPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadPublishedKitListPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadPublishedKitListPort.Result;
import org.flickit.assessment.kit.application.port.out.kittag.LoadKitListTagsListPort;
import org.flickit.assessment.kit.application.port.out.minio.CreateFileDownloadLinkPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitListService implements GetKitListUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    private final LoadPublishedKitListPort loadPublishedKitListPort;
    private final CountKitListStatsPort countKitStatsPort;
    private final LoadKitListTagsListPort loadKitTagsListPort;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Override
    public PaginatedResponse<KitListItem> getKitList(Param param) {
        PaginatedResponse<Result> kitsPage;
        if (Boolean.FALSE.equals(param.getIsPrivate())) {
            kitsPage = loadPublishedKitListPort.loadPublicKits(param.getPage(), param.getSize());
        } else {
            kitsPage = loadPublishedKitListPort.loadPrivateKit(param.getCurrentUserId(), param.getPage(), param.getSize());
        }
        var ids = kitsPage.getItems().stream().map((Result t) -> t.kit().getId()).toList();

        var idToStatsMap = countKitStatsPort.countKitsStats(ids).stream()
            .collect(Collectors.toMap(CountKitListStatsPort.Result::id, Function.identity()));

        var idToKitTagsMap = loadKitTagsListPort.loadByKitIds(ids).stream()
            .collect(Collectors.groupingBy(LoadKitListTagsListPort.Result::kitId));

        var items = kitsPage.getItems().stream().map(k -> new KitListItem(
            k.kit().getId(),
            k.kit().getTitle(),
            k.kit().getSummary(),
            k.kit().isPrivate(),
            idToStatsMap.get(k.kit().getId()).likes(),
            idToStatsMap.get(k.kit().getId()).assessmentCounts(),
            toExpertGroup(k.expertGroup()),
            idToKitTagsMap.get(k.kit().getId()).stream()
                .flatMap(result -> result.kitTags().stream())
                .toList()
        )).toList();

        return new PaginatedResponse<>(
            items,
            kitsPage.getPage(),
            kitsPage.getSize(),
            kitsPage.getSort(),
            kitsPage.getOrder(),
            kitsPage.getTotal()
        );
    }

    private KitListItem.ExpertGroup toExpertGroup(ExpertGroup expertGroup) {
        return new KitListItem.ExpertGroup(expertGroup.getId(),
            expertGroup.getTitle(),
            createFileDownloadLinkPort.createDownloadLink(expertGroup.getPicture(), EXPIRY_DURATION));
    }
}
