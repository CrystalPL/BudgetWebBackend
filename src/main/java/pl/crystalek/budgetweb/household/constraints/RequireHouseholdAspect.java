package pl.crystalek.budgetweb.household.constraints;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.household.HouseholdService;
import pl.crystalek.budgetweb.household.response.RequireHouseholdResponse;
import pl.crystalek.budgetweb.share.ResponseAPI;

@Aspect
@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class RequireHouseholdAspect {
    HouseholdService householdService;

    @Around("@annotation(RequireHousehold)")
    public Object checkHouseholdExists(final ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return new ResponseAPI<>(false, RequireHouseholdResponse.USER_NOT_IN_HOUSEHOLD);
        }

        if (authentication.getPrincipal() == null) {
            return new ResponseAPI<>(false, RequireHouseholdResponse.USER_NOT_IN_HOUSEHOLD);
        }

        long userId = (long) authentication.getPrincipal();
        if (!householdService.userHasHousehold(userId)) {
            return new ResponseAPI<>(false, RequireHouseholdResponse.USER_NOT_IN_HOUSEHOLD);
        }

        return joinPoint.proceed();
    }
}
