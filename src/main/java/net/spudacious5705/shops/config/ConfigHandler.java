package net.spudacious5705.shops.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.spudacious5705.shops.screen.ToggleButtonID;

import java.util.EnumMap;



public class ConfigHandler {

    private static final ForgeConfigSpec.Builder CONF_BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue EFFECTS_ENABLED_DEFAULT = CONF_BUILDER
            .comment("Default shop settings value to show or hide shop effects")
            .define("effectsDefault", true);

    private static final ForgeConfigSpec.IntValue SHOP_TRADE_STACK_MULTIPLIER = CONF_BUILDER
            .comment("Multiplier for the stack size in a single shop trade. Min:1 Max:54")
            .defineInRange("shopTradeStackMultiplier", 4, 1, 54);


    private static final EnumMap<ToggleButtonID, Boolean> toggleSettingsStates = initialiseToggleSettingStates();



    private static EnumMap<ToggleButtonID, Boolean> initialiseToggleSettingStates() {
        EnumMap<ToggleButtonID, Boolean> tss = new EnumMap<>(ToggleButtonID.class);

        for (ToggleButtonID value : ToggleButtonID.values()) {
            tss.put(value, false);
        }

        return tss;
    }

    public static Boolean getDefaultToggleSetting(ToggleButtonID ID) {
        return toggleSettingsStates.getOrDefault(ID,false);
    }

    public static int stackSizeMultiplier = 1;
    public static void initialise(final ModConfigEvent event){
        stackSizeMultiplier = SHOP_TRADE_STACK_MULTIPLIER.get();

        boolean effectsDefault = EFFECTS_ENABLED_DEFAULT.get();
        toggleSettingsStates.put(ToggleButtonID.EffectsToggle, effectsDefault);
    }
}
