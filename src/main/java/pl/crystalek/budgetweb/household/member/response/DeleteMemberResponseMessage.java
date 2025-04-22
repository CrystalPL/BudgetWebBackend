package pl.crystalek.budgetweb.household.member.response;

public enum DeleteMemberResponseMessage {
    SUCCESS, YOURSELF_DELETE, DIFFERENT_HOUSEHOLD, MISSING_MEMBER_ID, ERROR_NUMBER_FORMAT, MEMBER_NOT_FOUND, USER_IS_OWNER
}
