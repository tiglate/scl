package ludo.mentis.aciem.scl.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class SecurityViolationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 4177843589831918135L;

    public SecurityViolationException(String message) {
        super(message);
    }
}
