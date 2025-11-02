package pl.crystalek.budgetweb.filter;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.filter.request.DuplicateFilterRequest;
import pl.crystalek.budgetweb.filter.request.FilterIdRequest;
import pl.crystalek.budgetweb.filter.request.SaveFilterRequest;
import pl.crystalek.budgetweb.filter.response.BaseFilterResponseMessage;
import pl.crystalek.budgetweb.share.ResponseAPI;

import java.util.Optional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AdvancedFilterFacade {
    AdvancedFilterRepository advancedFilterRepository;
    SaveFilter saveFilter;
    ActivateFilter activateFilter;
    DeleteFilter deleteFilter;
    DuplicateFilter duplicateFilter;

    public ResponseAPI<BaseFilterResponseMessage> saveFilter(final SaveFilterRequest saveFilterRequest, final long requesterId) {
        return saveFilter.saveFilter(saveFilterRequest, requesterId);
    }

    public ResponseAPI<BaseFilterResponseMessage> activateFilter(final FilterIdRequest filterIdRequest, final long requesterId) {
        return activateFilter.activateFilter(filterIdRequest, requesterId);
    }

    public ResponseAPI<BaseFilterResponseMessage> deleteFilter(final long filterId, final long requesterId) {
        return deleteFilter.deleteFilter(filterId, requesterId);
    }

    public ResponseAPI<BaseFilterResponseMessage> duplicateFilter(final DuplicateFilterRequest duplicateFilterRequest, final long requesterId) {
        return duplicateFilter.duplicateFilter(duplicateFilterRequest, requesterId);
    }

    public Optional<AdvancedFilter> getAdvancedFilter(final long id, final long requesterId) {
        return advancedFilterRepository.findByIdAndUser_Id(id, requesterId);
    }

    public void saveAdvancedFilter(final AdvancedFilter advancedFilter) {
        advancedFilterRepository.save(advancedFilter);
    }
}
