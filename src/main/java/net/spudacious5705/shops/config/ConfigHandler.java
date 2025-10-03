package net.spudacious5705.shops.config;

import net.spudacious5705.shops.screenNetworking.ToggleSyncPayload;

import java.util.EnumMap;

import com.electronwill.nightconfig.core.file.FileConfig;

import java.io.File;

import static net.spudacious5705.shops.SpudaciousShops.MOD_ID;

public class ConfigHandler {
    private static final File CONFIG_PATH = new File("config/"+MOD_ID+".toml");
    public static FileConfig config;


    private static final EnumMap<ToggleSyncPayload.ToggleButtonID, Boolean> toggleSettingsStates = getDefaultToggleSettingStates();


    private static EnumMap<ToggleSyncPayload.ToggleButtonID, Boolean> getDefaultToggleSettingStates() {
        EnumMap<ToggleSyncPayload.ToggleButtonID, Boolean> tss = new EnumMap<>(ToggleSyncPayload.ToggleButtonID.class);
        config = FileConfig.of(CONFIG_PATH);
        config.load();

        String TogEffectID = "ToggleEffectsDefault";
        boolean effectsEnabled = true;
        if (!config.contains(TogEffectID)) {
            config.set(TogEffectID, true);
            config.save();
        } else {
            effectsEnabled = config.getOrElse(TogEffectID, true);
        }

        //ugly but ensures all values are covered
        for (ToggleSyncPayload.ToggleButtonID value : ToggleSyncPayload.ToggleButtonID.values()) {
            tss.put(value,
                    switch (value){
                        case CreativeToggle -> false;
                        case ShopStyleToggle -> false;
                        case IgnoreNBTToggle -> false;
                        case EffectsToggle -> effectsEnabled;
                    }
                    );
        }
        return tss;
    }
    public static Boolean getDefaultToggleSetting(ToggleSyncPayload.ToggleButtonID ID) {
        return toggleSettingsStates.get(ID);
    }
    public static void initialise(){}
}
