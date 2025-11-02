package pl.crystalek.budgetweb.filter.condition;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.exception.BudgetAppException;
import pl.crystalek.budgetweb.filter.AdvancedFilter;
import pl.crystalek.budgetweb.filter.AdvancedFilterFacade;
import pl.crystalek.budgetweb.filter.condition.request.SaveConditionGroupRequest;
import pl.crystalek.budgetweb.filter.condition.request.SaveFilterConditionRequest;
import pl.crystalek.budgetweb.filter.condition.response.SaveFilterConditionResponse;
import pl.crystalek.budgetweb.filter.condition.validator.SaveConditionValidator;
import pl.crystalek.budgetweb.receipt.Receipt;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class SaveCondition {
    AdvancedFilterFacade advancedFilterFacade;
    @Qualifier("receiptRepository")
    JpaSpecificationExecutor<Receipt> receiptJpaSpecificationExecutor;

    void saveCondition(final SaveFilterConditionRequest saveFilterConditionRequest, final long requesterId) {
        final Long advancedFilterId = saveFilterConditionRequest.advancedFilterId();
        final Optional<AdvancedFilter> advancedFilterOptional = advancedFilterFacade.getAdvancedFilter(advancedFilterId, requesterId);
        if (advancedFilterOptional.isEmpty()) {
            throw new BudgetAppException(SaveFilterConditionResponse.FILTER_NOT_EXISTS);
        }

        AdvancedFilter advancedFilter = advancedFilterOptional.get();
        final List<SaveConditionGroupRequest> conditionGroupRequests = saveFilterConditionRequest.conditionGroups();
        final List<SaveConditionValidator> validators = SaveConditionValidator.getValidators(conditionGroupRequests, advancedFilter);
        validators.forEach(SaveConditionValidator::validate);
        final ConditionAssembler conditionAssembler = new ConditionAssembler(advancedFilter, saveFilterConditionRequest);

        advancedFilter = conditionAssembler.createAdvancedFilter();
        advancedFilterFacade.saveAdvancedFilter(advancedFilter);
        final Specification<Receipt> receiptSpecification = new FilterSpecificationBuilder(advancedFilter).buildSpecification();
        final List<Receipt> all = receiptJpaSpecificationExecutor.findAll(receiptSpecification);
        System.out.println(all);
    }
}
