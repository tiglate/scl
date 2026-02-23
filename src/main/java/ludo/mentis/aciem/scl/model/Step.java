package ludo.mentis.aciem.scl.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Step {
    INSTRUCTION_RECEIVED("ins"),
    RECEIVED_OR_PAID_FOREIGN_CURRENCY("g10"),
    RECEIVED_OR_PAID_LOCAL_CURRENCY("brl"),
    UPSTREAM_RELEASE_OR_CONFIRMATION("ion");

    private final String value;

    Step(String value) {
        this.value = value;
    }

    /**
     * Controls how this enum is serialized to JSON.
     * JS will still see: "ins", "g10", "brl", "ion".
     */
    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Controls how this enum is deserialized from JSON strings.
     * Accepts payloads like: { "currentStep": "ins" }.
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static Step fromValue(String value) {
        if (value == null) return null;
        for (Step step : values()) {
            if (step.value.equalsIgnoreCase(value)) {
                return step;
            }
        }
        throw new IllegalArgumentException("Unknown step value: " + value);
    }
}