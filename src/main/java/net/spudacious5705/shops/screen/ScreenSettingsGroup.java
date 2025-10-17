package net.spudacious5705.shops.screen;


import net.minecraft.resources.ResourceLocation;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.block.VariantResources;


public record ScreenSettingsGroup(
        ScreenSettings CUSTOMER,
        ScreenSettings SELLER,
        ScreenSettings SETTINGS,
        ResourceLocation BUTTON_BACKGROUND,

        int tab1ButtonX,  int tab1ButtonY,
        int tab2ButtonX,  int tab2ButtonY,
        int tab3ButtonX,  int tab3ButtonY,

        //settings button positions
        int creativeButtonX,  int creativeButtonY,
        int shopStyleButtonX,  int shopStyleButtonY,
        int ignoreNBTButtonX,  int ignoreNBTButtonY,
        int toggleEffectsButtonX,  int toggleEffectsButtonY,

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

    private static ResourceLocation id(String texture){
        return SpudaciousShops.id("textures/gui/"+texture+".png");
    }


    public record ScreenSettings(
            ResourceLocation textureID,
            int backgroundWidth, int backgroundHeight,
            int playerInvX, int playerInvY,
            int shopInvX, int shopInvY,
            int tradeInvX, int tradeInvY) {

        public static ScreenSettings createBasicCUSTOMER(ResourceLocation textureID){
            return new ScreenSettings(textureID,
                    176, 165,
                    8,84,
                    0,0,
                    80,11);
        }
        public static ScreenSettings createBasicSELLER(ResourceLocation textureID){
            return new ScreenSettings(textureID,
                    228, 254,
                    33,172,
                    60,10,
                    23,11);
        }
        public static ScreenSettings createBasicSETTINGS(ResourceLocation textureID){
            return new ScreenSettings(textureID,
                    228, 254,
                    33,172,
                    60,10,
                    23,11);
        }
    }

    public static ScreenSettingsGroup createBasicWood(VariantResources.wood_variant VARIANT){
        return createBasic(VARIANT.owner_trade,VARIANT.storage,VARIANT.settings,VARIANT.settings_button, VARIANT.settings_text_colour);
    }

    public static ScreenSettingsGroup createBasic(
            ResourceLocation CUSTOMER,
            ResourceLocation SELLER,
            ResourceLocation SETTINGS,
            ResourceLocation BUTTON_BACKGROUND,
            int colour){
        return new ScreenSettingsGroup(
                ScreenSettings.createBasicCUSTOMER(CUSTOMER),
                ScreenSettings.createBasicSELLER(SELLER),
                ScreenSettings.createBasicSETTINGS(SETTINGS),
                BUTTON_BACKGROUND,

                203,174,
                203,199,
                203,225,


                127,42,
                75,42,
                25,42,
                175,42,

                colour);
    }
}
