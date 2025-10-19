package pl.crystalek.budgetweb.filter;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.filter.request.FilterIdRequest;
import pl.crystalek.budgetweb.filter.response.BaseFilterResponseMessage;
import pl.crystalek.budgetweb.share.ResponseAPI;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class ActivateFilter {
    AdvancedFilterRepository advancedFilterRepository;

    ResponseAPI<BaseFilterResponseMessage> activateFilter(final FilterIdRequest filterIdRequest, final long requesterId) {
        final boolean activateFilter = advancedFilterRepository.activateFilter(filterIdRequest.filterId(), requesterId);
        final BaseFilterResponseMessage message = activateFilter ? BaseFilterResponseMessage.SUCCESS : BaseFilterResponseMessage.FILTER_NOT_EXISTS;

        return new ResponseAPI<>(activateFilter, message);
    }
}
