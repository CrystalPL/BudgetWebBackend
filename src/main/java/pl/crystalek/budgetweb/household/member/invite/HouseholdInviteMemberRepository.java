package pl.crystalek.budgetweb.household.member.invite;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

interface HouseholdInviteMemberRepository extends CrudRepository<HouseholdInviteMember, UUID> {

}
