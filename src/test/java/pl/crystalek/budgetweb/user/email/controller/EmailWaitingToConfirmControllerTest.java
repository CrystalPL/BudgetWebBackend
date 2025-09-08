package pl.crystalek.budgetweb.user.email.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import pl.crystalek.budgetweb.user.request.ChangeEmailRequest;
import pl.crystalek.budgetweb.user.response.GetEmailChangingInfoResponse;
import pl.crystalek.budgetweb.utils.BaseAccessControllerTest;
import pl.crystalek.budgetweb.utils.UserAccountUtil;
import pl.crystalek.budgetweb.utils.request.RequestHelper;

class EmailWaitingToConfirmControllerTest extends BaseAccessControllerTest {

    @Override
    protected String[][] shouldDeniedAccessWithoutAccount() {
        return new String[][]{{"/account/email-changing-wait-to-confirm", "GET"}};
    }

    @Override
    protected String[][] shouldAllowAccessWithAccount() {
        return new String[][]{{"/account/email-changing-wait-to-confirm", "GET"},};
    }

    @Test
    void returnTrueWhenEmailWaitingToConfirm() {
        final ChangeEmailRequest changeEmailRequest = new ChangeEmailRequest("test2@example.com", "test2@example.com", UserAccountUtil.TESTING_USER.password());
        RequestHelper.builder()
                .withUser(userAccountUtil)
                .path("/account/change-email")
                .httpMethod(HttpMethod.POST)
                .content(changeEmailRequest)
                .build().sendRequest(mockMvc);

        RequestHelper.builder()
                .loginUser(userAccountUtil)
                .path("/account/email-changing-wait-to-confirm")
                .httpMethod(HttpMethod.GET)
                .expectedResponseCode(HttpStatus.OK)
                .expectedResponseObject(new GetEmailChangingInfoResponse(true))
                .build().sendRequest(mockMvc);
    }

    @Test
    void returnFalseWhenEmailNotWaitingToConfirm() {
        RequestHelper.builder()
                .withUser(userAccountUtil)
                .path("/account/email-changing-wait-to-confirm")
                .httpMethod(HttpMethod.GET)
                .expectedResponseCode(HttpStatus.OK)
                .expectedResponseObject(new GetEmailChangingInfoResponse(false))
                .build().sendRequest(mockMvc);
    }
}
