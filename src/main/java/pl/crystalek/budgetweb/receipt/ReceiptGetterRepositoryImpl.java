package pl.crystalek.budgetweb.receipt;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import pl.crystalek.budgetweb.household.Household;
import pl.crystalek.budgetweb.household.member.HouseholdMember;
import pl.crystalek.budgetweb.receipt.response.GetReceiptResponse;
import pl.crystalek.budgetweb.receipt.response.UserWhoPaid;

import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class ReceiptGetterRepositoryImpl implements ReceiptGetterRepository {

    EntityManager entityManager;

    @Override
    public Page<GetReceiptResponse> findReceipts(final Long userId,
                                                 final Specification<Receipt> filterSpecification,
                                                 final Pageable pageable) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        final TypedQuery<Tuple> query = buildQuery(userId, filterSpecification, pageable);
        final List<GetReceiptResponse> content = getMappedResponse(query);
        final Long total = countEntities(userId, filterSpecification, criteriaBuilder);

        return new PageImpl<>(content, pageable, total);
    }

    private TypedQuery<Tuple> buildQuery(
            final Long userId,
            final Specification<Receipt> filterSpecification,
            final Pageable pageable
    ) {
        final ReceiptGetterMainQueryBuilder queryBuilder = new ReceiptGetterMainQueryBuilder(userId, filterSpecification, pageable, entityManager);
        return queryBuilder.buildMainQuery();
    }

    private List<GetReceiptResponse> getMappedResponse(final TypedQuery<Tuple> query) {
        return query.getResultList().stream()
                .map(this::mapToResponse)
                .toList();
    }

    private GetReceiptResponse mapToResponse(final Tuple tuple) {
        return new GetReceiptResponse(
                tuple.get("id", Long.class),
                tuple.get("shop", String.class),
                tuple.get("shoppingTime", Instant.class),
                tuple.get("total", Double.class),
                new UserWhoPaid(
                        tuple.get("whoPaidId", Long.class),
                        tuple.get("whoPaidNickname", String.class)
                ),
                tuple.get("settled", Boolean.class)
        );
    }

    private Long countEntities(final Long userId, final Specification<Receipt> specification, final CriteriaBuilder criteriaBuilder) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Receipt> countRoot = countQuery.from(Receipt.class);
        Join<Receipt, Household> countHousehold = countRoot.join("household");
        Join<Household, HouseholdMember> countMembers = countHousehold.join("members");

        Predicate countSpec = specification.toPredicate(countRoot, countQuery, criteriaBuilder);
        Predicate countUser = criteriaBuilder.equal(countMembers.get("user").get("id"), userId);

        countQuery.select(criteriaBuilder.countDistinct(countRoot)).where(criteriaBuilder.and(countSpec, countUser));

        return entityManager.createQuery(countQuery).getSingleResult();
    }
}