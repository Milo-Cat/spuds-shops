package net.spudacious5705.shops.screen;

public enum ToggleButtonID {
    CreativeToggle("Crea"),
    SelectableTradeToggle("Styl"),
    IgnoreNBTToggle("iNBT"),
    EffectsToggle("Efex");

    public final String serialised;

    ToggleButtonID(String s) {
        this.serialised = s;
    }

    public static ToggleButtonID fromString(String s) 
    {
        for (ToggleButtonID id : ToggleButtonID.values()) {
            if (id.getSerialised().equalsIgnoreCase(s)) {
                return id;
            }
        }
        throw new IllegalArgumentException("Unknown ToggleButtonID: " + s);
    }

    public String getSerialised() 
    {
        return serialised;
    }
}
