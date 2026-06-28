package pl.crystalek.budgetweb.receipt;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import pl.crystalek.budgetweb.household.Household;
import pl.crystalek.budgetweb.household.member.HouseholdMember;
import pl.crystalek.budgetweb.receipt.items.ReceiptItem;

import java.math.BigDecimal;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class ReceiptGetterMainQueryBuilder {
    Long userId;
    Specification<Receipt> filterSpecification;
    Pageable pageable;
    EntityManager entityManager;

    CriteriaBuilder criteriaBuilder;
    Root<Receipt> receipt;
    CriteriaQuery<Tuple> queryBuilder;
    Join<Receipt, ReceiptItem> items;
    Join<Household, HouseholdMember> members;


    ReceiptGetterMainQueryBuilder(
            final Long userId,
            final Specification<Receipt> filterSpecification,
            final Pageable pageable,
            final EntityManager entityManager
    ) {
        this.userId = userId;
        this.filterSpecification = filterSpecification;
        this.pageable = pageable;
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
        this.queryBuilder = criteriaBuilder.createTupleQuery();
        this.receipt = queryBuilder.from(Receipt.class);
        this.items = receipt.join("items", JoinType.LEFT);
        final Join<Receipt, Household> household = receipt.join("household");
        this.members = household.join("members");
    }

    TypedQuery<Tuple> buildMainQuery() {
        buildUserAndFilterConditions();
        buildGroupBy();
        buildSelect();
        return applyPageableOptions();
    }

    private void buildUserAndFilterConditions() {
        Predicate specPredicate = filterSpecification.toPredicate(receipt, queryBuilder, criteriaBuilder);

        Predicate userPredicate = criteriaBuilder.equal(members.get("user").get("id"), userId);
        queryBuilder.where(criteriaBuilder.and(specPredicate, userPredicate));
    }

    private void buildGroupBy() {
        queryBuilder.groupBy(
                receipt.get("id"),
                receipt.get("shop"),
                receipt.get("shoppingTime"),
                receipt.get("settled"),
                receipt.get("whoPaid").get("id"),
                receipt.get("whoPaid").get("nickname")
        );
    }

    private void buildSelect() {
        queryBuilder.multiselect(
                receipt.get("id").alias("id"),
                receipt.get("shop").alias("shop"),
                receipt.get("shoppingTime").alias("shoppingTime"),
                criteriaBuilder.coalesce(
                        criteriaBuilder.sum(
                                criteriaBuilder.prod(
                                        items.get("price"),
                                        items.get("quantity")
                                )
                        ),
                        BigDecimal.ZERO
                ).alias("total"),
                receipt.get("whoPaid").get("id").alias("whoPaidId"),
                receipt.get("whoPaid").get("nickname").alias("whoPaidNickname"),
                receipt.get("settled").alias("settled")
        );
    }

    private TypedQuery<Tuple> applyPageableOptions() {
        for (Sort.Order order : pageable.getSort()) {
            Path<?> path = receipt.get(order.getProperty());
            queryBuilder.orderBy(order.isAscending() ? criteriaBuilder.asc(path) : criteriaBuilder.desc(path));
        }

        TypedQuery<Tuple> query = entityManager.createQuery(queryBuilder);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        return query;
    }
}
