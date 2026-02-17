package ludo.mentis.aciem.scl.model;


public enum FxSettlementStepType {

    INSTRUCTION_RECEIVED,
    RECEIVED_OR_PAID_FOREIGN_CURRENCY,
    RECEIVED_OR_PAID_LOCAL_CURRENCY,
    UPSTREAM_RELEASE_OR_CONFIRMATION;

    public static final FxSettlementStepType translateStringToEnum(String workflowStep) {
        if (workflowStep == null) {
            throw new IllegalArgumentException("workflowStep cannot be null");
        }
        switch (workflowStep.toUpperCase()) {
            case "INS": return INSTRUCTION_RECEIVED;
            case "G10": return RECEIVED_OR_PAID_FOREIGN_CURRENCY;
            case "BRL": return RECEIVED_OR_PAID_LOCAL_CURRENCY;
            case "ION": return UPSTREAM_RELEASE_OR_CONFIRMATION;
            default: {
                throw new IllegalArgumentException("Invalid workflowStep: " + workflowStep);
            }
        }
    }
}
