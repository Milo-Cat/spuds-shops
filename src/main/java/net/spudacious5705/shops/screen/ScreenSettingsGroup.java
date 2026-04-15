package net.spudacious5705.shops.screen;


import net.minecraft.resources.Identifier;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.block.resources.VariantResources;


public record ScreenSettingsGroup(
        ScreenSettings CUSTOMER,
        ScreenSettings CUSTOMER_MULTI,
        ScreenSettings SELLER,
        ScreenSettings SETTINGS,
        Identifier BUTTON_BACKGROUND,

        int tab1ButtonX, int tab1ButtonY,
        int tab2ButtonX, int tab2ButtonY,
        int tab3ButtonX, int tab3ButtonY,

        //settings button positions
        int creativeButtonX, int creativeButtonY,
        int shopStyleButtonX, int shopStyleButtonY,
        int ignoreNBTButtonX, int ignoreNBTButtonY,
        int toggleEffectsButtonX, int toggleEffectsButtonY,

        int SETTINGS_TEXT_COLOUR
) {
    /*BASIC(0,
            new ScreenSettings(
                    id("owner_customer_screen"),
                    176, 165,
                    8,84,
                    0,0,
                    80,11),
            new ScreenSettings(
                    id("shop_seller"),
                    228, 254,
                    33,172,
                    60,10,
                    23,11),
            new ScreenSettings(
                    id("shop_settings"),
                    228, 254,
                    33,172,
                    60,10,
                    23,11),
            203,174,
            201,195,
            201,221
    );*/

    private static Identifier id(String texture) {
        return SpudaciousShops.id("textures/gui/" + texture + ".png");
    }

    public static ScreenSettingsGroup createBasicWood(VariantResources.wood_variant VARIANT) {
        return createBasic(VARIANT.trade, VARIANT.trade_multi, VARIANT.storage, VARIANT.settings, VARIANT.settings_button, VARIANT.settings_text_colour);
    }

    public static ScreenSettingsGroup createBasic(
            Identifier CUSTOMER,
            Identifier CUSTOMER_MULTI,
            Identifier SELLER,
            Identifier SETTINGS,
            Identifier BUTTON_BACKGROUND,
            int colour) {
        return new ScreenSettingsGroup(
                ScreenSettings.createBasicCUSTOMER(CUSTOMER),
                ScreenSettings.createBasicCUSTOMER_MULTI(CUSTOMER_MULTI),
                ScreenSettings.createBasicSELLER(SELLER),
                ScreenSettings.createBasicSETTINGS(SETTINGS),
                BUTTON_BACKGROUND,

                203, 174,
                203, 199,
                203, 225,


                31, 42,
                79, 42,
                127, 42,
                175, 42,

                colour);
    }

    public record ScreenSettings(
            Identifier textureID,
            int backgroundWidth, int backgroundHeight,
            int playerInvX, int playerInvY,
            int shopInvX, int shopInvY,
            int tradeInvX, int tradeInvY) {

        public static ScreenSettings createBasicCUSTOMER(Identifier textureID) {
            return new ScreenSettings(textureID,
                    176, 165,
                    8, 84,
                    0, 0,
                    80, 11);
        }

        public static ScreenSettings createBasicCUSTOMER_MULTI(Identifier textureID) {
            return new ScreenSettings(textureID,
                    176, 165,
                    8, 84,
                    0, 0,
                    80, 11);
        }

        public static ScreenSettings createBasicSELLER(Identifier textureID) {
            return new ScreenSettings(textureID,
                    228, 254,
                    33, 172,
                    60, 10,
                    23, 11);
        }

        public static ScreenSettings createBasicSETTINGS(Identifier textureID) {
            return new ScreenSettings(textureID,
                    228, 254,
                    33, 172,
                    60, 10,
                    23, 11);
        }
    }
}
