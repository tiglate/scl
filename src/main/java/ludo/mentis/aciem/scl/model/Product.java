package ludo.mentis.aciem.scl.model;


public enum Product {

    FX_SPOT("FX Spot"),
    FX_FORWARD("FX Forward"),
    NDF("NDF");
	
    private final String displayName;

    Product(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
