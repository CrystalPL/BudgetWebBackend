package pl.crystalek.budgetweb.filter.condition;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.Specification;
import pl.crystalek.budgetweb.filter.AdvancedFilter;
import pl.crystalek.budgetweb.filter.AdvancedFilterField;
import pl.crystalek.budgetweb.filter.condition.model.Condition;
import pl.crystalek.budgetweb.filter.condition.model.ConditionGroup;
import pl.crystalek.budgetweb.filter.condition.model.FilterLogicalOperator;
import pl.crystalek.budgetweb.filter.condition.model.FilterOperator;
import pl.crystalek.budgetweb.filter.data.type.FilterDataType;
import pl.crystalek.budgetweb.receipt.Receipt;
import pl.crystalek.budgetweb.receipt.items.ReceiptItem;
import pl.crystalek.budgetweb.utils.DateParserUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FilterSpecificationBuilder {

    AdvancedFilter filter;

    public Specification<Receipt> buildSpecification() {
        return (root, query, criteriaBuilder) -> {
            List<ConditionGroup> groups = filter.getConditionGroups();
            if (groups == null || groups.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return buildGroupsPredicate(groups, root, criteriaBuilder);
        };
    }

    private Predicate buildGroupsPredicate(List<ConditionGroup> groups, Root<?> root, CriteriaBuilder cb) {
        if (groups.isEmpty()) {
            return cb.conjunction();
        }

        List<Predicate> groupPredicates = new ArrayList<>();

        for (ConditionGroup group : groups) {
            Predicate groupPredicate = buildConditionsPredicate(group.getConditions(), root, cb);
            groupPredicates.add(groupPredicate);
        }

        // Połącz grupy operatorem OR (każda grupa w nawiasach)
        if (groupPredicates.size() == 1) {
            return groupPredicates.get(0);
        }

        Predicate result = groupPredicates.get(0);
        for (int i = 1; i < groupPredicates.size(); i++) {
            ConditionGroup group = groups.get(i);
            FilterLogicalOperator operator = group.getLogicalOperatorBefore();

            if (operator == FilterLogicalOperator.AND) {
                result = cb.and(result, groupPredicates.get(i));
            } else {
                result = cb.or(result, groupPredicates.get(i));
            }
        }

        return result;
    }

    private Predicate buildConditionsPredicate(List<Condition> conditions, Root<?> root, CriteriaBuilder cb) {
        if (conditions == null || conditions.isEmpty()) {
            return cb.conjunction();
        }

        List<PredicateWithOperator> predicateElements = new ArrayList<>();

        for (Condition condition : conditions) {
            // Obsługa otwierających nawiasów
            for (int i = 0; i < condition.getOpenParenthesis(); i++) {
                predicateElements.add(new PredicateWithOperator(null, null, true, false));
            }

            // Dodaj predykat dla warunku
            Predicate conditionPredicate = buildSingleConditionPredicate(condition, root, cb);
            predicateElements.add(new PredicateWithOperator(
                    conditionPredicate,
                    condition.getLogicalOperatorBefore()
            ));

            // Obsługa zamykających nawiasów
            for (int i = 0; i < condition.getCloseParenthesis(); i++) {
                predicateElements.add(new PredicateWithOperator(null, null, false, true));
            }
        }

        return combinePredicatesWithParentheses(predicateElements, cb);
    }

    private Predicate buildSingleConditionPredicate(Condition condition, Root<?> root, CriteriaBuilder cb) {
        final AdvancedFilterField advancedFilterField = filter.parseField(condition.getFieldEnumName());
        String fieldName = advancedFilterField.getFieldName();
        FilterOperator operator = condition.getOperator();
        String value1 = condition.getFirstValueAsString();
        String value2 = condition.getSecondValueAsString();

        if ("amount".equals(fieldName)) {
            return buildAmountPredicate(condition, root, cb);
        }

        Class<?> fieldType = root.get(fieldName).getJavaType();
        boolean isDateTimeField = advancedFilterField.getFilterDataType() == FilterDataType.DATE;

        return switch (operator) {
            case EQUALS -> {
                if (isDateTimeField) {
                    Object dateValue = parseDateTime(value1);
                    yield cb.equal(root.get(fieldName), dateValue);
                }
                final Object object;
                if (fieldType == Boolean.class) {
                    object = Boolean.parseBoolean(value1);
                } else {
                    object = root.get(fieldName);

                }

                yield cb.equal(root.get(fieldName), object);
            }
            case NOT_EQUALS -> {
                if (isDateTimeField) {
                    Object dateValue = parseDateTime(value1);
                    yield cb.notEqual(root.get(fieldName), dateValue);
                }
                yield cb.notEqual(root.get(fieldName), value1);
            }
            case GREATER_THAN, AFTER -> {
                if (isDateTimeField) {
                    Comparable dateValue = (Comparable) parseDateTime(value1);
                    yield cb.greaterThan(root.get(fieldName), dateValue);
                }
                yield cb.greaterThan(root.get(fieldName), value1);
            }
            case GREATER_THAN_OR_EQUAL -> {
                if (isDateTimeField) {
                    Comparable dateValue = (Comparable) parseDateTime(value1);
                    yield cb.greaterThanOrEqualTo(root.get(fieldName), dateValue);
                }
                yield cb.greaterThanOrEqualTo(root.get(fieldName), value1);
            }
            case LESS_THAN, BEFORE -> {
                if (isDateTimeField) {
                    Comparable dateValue = (Comparable) parseDateTime(value1);
                    yield cb.lessThan(root.get(fieldName), dateValue);
                }
                yield cb.lessThan(root.get(fieldName), value1);
            }
            case LESS_THAN_OR_EQUAL -> {
                if (isDateTimeField) {
                    Comparable dateValue = (Comparable) parseDateTime(value1);
                    yield cb.lessThanOrEqualTo(root.get(fieldName), dateValue);
                }
                yield cb.lessThanOrEqualTo(root.get(fieldName), value1);
            }
            case BETWEEN -> {
                if (isDateTimeField) {
                    Comparable dateValue1 = (Comparable) parseDateTime(value1);
                    Comparable dateValue2 = (Comparable) parseDateTime(value2);
                    yield cb.between(root.get(fieldName), dateValue1, dateValue2);
                }
                yield cb.between(root.get(fieldName), value1, value2);
            }
            case CONTAINS -> cb.like(root.get(fieldName), "%" + value1 + "%");
            case NOT_CONTAINS -> cb.notLike(root.get(fieldName), "%" + value1 + "%");
            case STARTS_WITH -> cb.like(root.get(fieldName), value1 + "%");
            case ENDS_WITH -> cb.like(root.get(fieldName), "%" + value1);
        };
    }

    private Predicate buildAmountPredicate(Condition condition, Root<?> root, CriteriaBuilder cb) {
        Subquery<BigDecimal> subquery = cb.createQuery().subquery(BigDecimal.class);
        Root<Receipt> subRoot = subquery.from(Receipt.class);
        Join<Receipt, ReceiptItem> itemsJoin = subRoot.join("items");

        subquery.select(cb.sum(cb.prod(itemsJoin.get("price"), itemsJoin.get("quantity"))));
        subquery.where(cb.equal(subRoot.get("id"), root.get("id")));

        BigDecimal numValue1 = new BigDecimal(condition.getFirstValueAsString());

        return switch (condition.getOperator()) {
            case EQUALS -> cb.equal(subquery, numValue1);
            case NOT_EQUALS -> cb.notEqual(subquery, numValue1);
            case GREATER_THAN -> cb.gt(subquery, numValue1);
            case GREATER_THAN_OR_EQUAL -> cb.ge(subquery, numValue1);
            case LESS_THAN -> cb.lt(subquery, numValue1);
            case LESS_THAN_OR_EQUAL -> cb.le(subquery, numValue1);
            case BETWEEN -> {
                BigDecimal numValue2 = new BigDecimal(condition.getSecondValueAsString());
                yield cb.between(subquery, numValue1, numValue2);
            }
            default -> throw new UnsupportedOperationException("Operator not supported for amount field");
        };
    }

    private Object parseDateTime(String value) {
        return DateParserUtil.parseDate(value).get();
    }

    private Predicate combinePredicates(List<PredicateWithOperator> elements, CriteriaBuilder cb) {
        if (elements.isEmpty()) {
            return cb.conjunction();
        }

        Predicate result = elements.get(0).predicate();

        for (int i = 1; i < elements.size(); i++) {
            PredicateWithOperator current = elements.get(i);
            FilterLogicalOperator operator = current.operator();

            if (operator == FilterLogicalOperator.AND) {
                result = cb.and(result, current.predicate());
            } else if (operator == FilterLogicalOperator.OR) {
                result = cb.or(result, current.predicate());
            }
        }

        return result;
    }

    private Predicate combinePredicatesWithParentheses(List<PredicateWithOperator> elements, CriteriaBuilder cb) {
        Stack<List<PredicateWithOperator>> stack = new Stack<>();
        stack.push(new ArrayList<>());

        for (PredicateWithOperator element : elements) {
            if (element.isOpenParenthesis()) {
                // Rozpocznij nową grupę
                stack.push(new ArrayList<>());
            } else if (element.isCloseParenthesis()) {
                // Zakończ grupę i połącz predykaty
                List<PredicateWithOperator> group = stack.pop();
                Predicate groupPredicate = combinePredicates(group, cb);

                // Dodaj wynik do poprzedniej grupy
                FilterLogicalOperator operator = !group.isEmpty() ?
                        group.getFirst().operator() : null;
                stack.peek().add(new PredicateWithOperator(groupPredicate, operator));
            } else {
                // Dodaj do aktualnej grupy
                stack.peek().add(element);
            }
        }

        return combinePredicates(stack.pop(), cb);
    }

    private record PredicateWithOperator(
            Predicate predicate,
            FilterLogicalOperator operator,
            boolean isOpenParenthesis,
            boolean isCloseParenthesis
    ) {
        PredicateWithOperator(Predicate predicate, FilterLogicalOperator operator) {
            this(predicate, operator, false, false);
        }
    }
}