package pl.crystalek.budgetweb.utils.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RequestHelper {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    HttpMethod httpMethod;
    String path;
    String contentType;
    Object content;
    Map<String, Object> headers;
    Set<Cookie> cookies;
    Set<ResultMatcher> expects;
    Object responseObject;

    public static RequestHelperBuilder builder() {
        return new RequestHelperBuilder();
    }

    public ResponseData sendRequest(final MockMvc mockMvc) throws Exception {
        final MockHttpServletRequestBuilder request = request(httpMethod, path)
                .contentType(contentType)
                .content(OBJECT_MAPPER.writeValueAsString(content));

        if (!cookies.isEmpty()) {
            request.cookie(cookies.toArray(new Cookie[0]));

        }
        headers.forEach(request::header);

        final ResultActions resultActions = mockMvc.perform(request);
        resultActions.andExpectAll(expects.toArray(new ResultMatcher[0]));

        return new ResponseData(responseObject, resultActions);
    }
}
