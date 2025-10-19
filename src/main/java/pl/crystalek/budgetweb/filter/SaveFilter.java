package pl.crystalek.budgetweb.filter;

import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.filter.request.SaveFilterRequest;
import pl.crystalek.budgetweb.filter.response.BaseFilterResponseMessage;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.model.User;

import java.util.Optional;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class SaveFilter {
    AdvancedFilterRepository advancedFilterRepository;
    EntityManager entityManager;

    ResponseAPI<BaseFilterResponseMessage> saveFilter(final SaveFilterRequest saveFilterRequest, final long requesterId) {
        final AdvancedFilter advancedFilter;
        if (saveFilterRequest.id() != null) {
            final Optional<AdvancedFilter> advancedFilterOptional = advancedFilterRepository.findById(saveFilterRequest.id());
            if (advancedFilterOptional.isEmpty()) {
                return new ResponseAPI<>(false, BaseFilterResponseMessage.FILTER_NOT_EXISTS);
            }

            advancedFilter = updateFilter(advancedFilterOptional.get(), saveFilterRequest);
        } else {
            advancedFilter = createFilter(saveFilterRequest, requesterId);
        }

        advancedFilterRepository.save(advancedFilter);

        return new ResponseAPI<>(true, BaseFilterResponseMessage.SUCCESS);
    }

    private AdvancedFilter updateFilter(final AdvancedFilter advancedFilter, final SaveFilterRequest saveFilterRequest) {
        advancedFilter.update(saveFilterRequest.name(), saveFilterRequest.description());
        return advancedFilter;
    }

    private AdvancedFilter createFilter(final SaveFilterRequest saveFilterRequest, final long requesterId) {
        final User user = entityManager.getReference(User.class, requesterId);
        return new AdvancedFilter(saveFilterRequest.name(), saveFilterRequest.description(), user, saveFilterRequest.advancedFilterEntityType());
    }
}
