package pl.crystalek.budgetweb.filter.condition;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.filter.condition.request.SaveFilterConditionRequest;
import pl.crystalek.budgetweb.filter.condition.response.ConditionGroupResponse;

import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ConditionFacade {
    SaveCondition saveCondition;
    ConditionGroupGetter conditionGroupGetter;

    public void saveCondition(final SaveFilterConditionRequest saveFilterConditionRequest, final long userId) {
        saveCondition.saveCondition(saveFilterConditionRequest, userId);
    }

    public List<ConditionGroupResponse> getConditionGroupResponse(final long advanccedFilterId, final long requesterId) {
        return conditionGroupGetter.getConditionGroups(advanccedFilterId, requesterId);
    }
}
