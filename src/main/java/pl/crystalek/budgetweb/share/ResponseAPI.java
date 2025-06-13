package pl.crystalek.budgetweb.share;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ResponseAPI<T extends Enum<T>> {
    boolean success;
    T message;

    @JsonIgnore
    public HttpStatusCode getStatusCode() {
        return success ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
    }
}
