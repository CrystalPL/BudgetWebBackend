package pl.crystalek.budgetweb.filter;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.filter.request.DuplicateFilterRequest;
import pl.crystalek.budgetweb.filter.response.BaseFilterResponseMessage;
import pl.crystalek.budgetweb.share.ResponseAPI;

import java.util.Optional;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class DuplicateFilter {
    AdvancedFilterRepository advancedFilterRepository;

    ResponseAPI<BaseFilterResponseMessage> duplicateFilter(final DuplicateFilterRequest duplicateFilterRequest, final long requesterId) {
        final Optional<AdvancedFilter> advancedFilterOptional = advancedFilterRepository.findByIdAndUser_Id(duplicateFilterRequest.id(), requesterId);
        if (advancedFilterOptional.isEmpty()) {
            return new ResponseAPI<>(false, BaseFilterResponseMessage.FILTER_NOT_EXISTS);
        }

        final AdvancedFilter duplicateAdvancedFilter = advancedFilterOptional.get().clone();
        duplicateAdvancedFilter.update(duplicateFilterRequest.name(), duplicateAdvancedFilter.getFilterName());

        advancedFilterRepository.save(duplicateAdvancedFilter);

        return new ResponseAPI<>(true, BaseFilterResponseMessage.SUCCESS);
    }
}
