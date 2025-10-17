package net.spudacious5705.shops.config;

import com.electronwill.nightconfig.core.file.FileConfig;
import net.spudacious5705.shops.screen.ToggleButtonID;

import java.io.File;
import java.util.EnumMap;

import static net.spudacious5705.shops.SpudaciousShops.MOD_ID;

public class ConfigHandler {
    private static final File CONFIG_PATH = new File("config/"+MOD_ID+".toml");
    public static FileConfig config;


    private static final EnumMap<ToggleButtonID, Boolean> toggleSettingsStates = getDefaultToggleSettingStates();


    private static EnumMap<ToggleButtonID, Boolean> getDefaultToggleSettingStates() {
        EnumMap<ToggleButtonID, Boolean> tss = new EnumMap<>(ToggleButtonID.class);
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
        for (ToggleButtonID value : ToggleButtonID.values()) {
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
    public static Boolean getDefaultToggleSetting(ToggleButtonID ID) {
        return toggleSettingsStates.getOrDefault(ID,false);
    }
    public static void initialise(){}
}
