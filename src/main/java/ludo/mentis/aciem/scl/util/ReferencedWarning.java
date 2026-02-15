package ludo.mentis.aciem.scl.util;

import java.util.ArrayList;
import java.util.stream.Collectors;


public class ReferencedWarning {

    private String message = null;

    public String toMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public void setMessage(final String message, final Object... args) {
        this.message = message.formatted(args);
    }


}
