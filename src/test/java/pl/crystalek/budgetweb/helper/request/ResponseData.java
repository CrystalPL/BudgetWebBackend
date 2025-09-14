package pl.crystalek.budgetweb.helper.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.test.web.servlet.ResultActions;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class ResponseData {
    Object responseData;
    @Getter
    ResultActions resultActions;

    @SuppressWarnings("unchecked")
    public <T> T getResponseData(final Class<T> clazz) {
        return (T) responseData;
    }

}