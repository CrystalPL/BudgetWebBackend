package pl.crystalek.budgetweb.user.profile.avatar;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.user.UserService;
import pl.crystalek.budgetweb.user.profile.avatar.response.AvatarWithHouseholdCheckResponse;
import pl.crystalek.budgetweb.user.profile.avatar.response.GetAvatarResponse;
import pl.crystalek.budgetweb.user.profile.avatar.response.GetAvatarResponseMessage;
import pl.crystalek.budgetweb.utils.NumberUtil;

import java.util.Optional;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class GetUserAvatar {
    AvatarRepository avatarRepository;
    UserService userService;

    GetAvatarResponse validateRequest(final String targetUserIdString, final long requesterUserId) {
        final Optional<Long> targetUserIdOptional = NumberUtil.getLong(targetUserIdString);
        if (targetUserIdOptional.isEmpty()) {
            return new GetAvatarResponse(false, GetAvatarResponseMessage.ERROR_NUMBER_FORMAT);
        }

        final long targetUserId = targetUserIdOptional.get();
        final boolean userExists = userService.isUserExists(targetUserId);
        if (!userExists) {
            return new GetAvatarResponse(false, GetAvatarResponseMessage.USER_NOT_FOUND);
        }

        final AvatarWithHouseholdCheckResponse avatarWithHouseholdCheck = avatarRepository.findAvatarWithHouseholdCheck(targetUserId, requesterUserId);
        return null;
    }
}
