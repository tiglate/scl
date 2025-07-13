package ludo.mentis.aciem.scl.model;


public enum FxTradePurpose {

    EQ("Equity"),
    FI("Fixed Income");
	
    private final String displayName;

    FxTradePurpose(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
