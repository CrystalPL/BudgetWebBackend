package pl.crystalek.budgetweb.filter;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.filter.response.BaseFilterResponseMessage;
import pl.crystalek.budgetweb.share.ResponseAPI;

import java.util.Optional;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class DeleteFilter {
    AdvancedFilterRepository advancedFilterRepository;

    ResponseAPI<BaseFilterResponseMessage> deleteFilter(final long filterId, final long requesterId) {
        final Optional<AdvancedFilter> filterOptional = advancedFilterRepository.findByIdAndUser_Id(filterId, requesterId);
        if (filterOptional.isEmpty()) {
            return new ResponseAPI<>(false, BaseFilterResponseMessage.FILTER_NOT_EXISTS);
        }

        advancedFilterRepository.delete(filterOptional.get());
        return new ResponseAPI<>(true, BaseFilterResponseMessage.SUCCESS);
    }
}
