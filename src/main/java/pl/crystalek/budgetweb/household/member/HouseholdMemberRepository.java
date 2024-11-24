package pl.crystalek.budgetweb.household.member;

import org.springframework.data.repository.CrudRepository;

interface HouseholdMemberRepository extends CrudRepository<HouseholdMember, Long> {

    boolean existsByUser_Id(final long userId);
}
