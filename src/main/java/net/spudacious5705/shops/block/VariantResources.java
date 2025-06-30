package net.spudacious5705.shops.block;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.block.custom.AngledShopBlock;
import net.spudacious5705.shops.block.custom.RugShopBlock;
import net.spudacious5705.shops.block.custom.ShelfShopBlock;
import net.spudacious5705.shops.block.custom.WindowSillShopBlock;

import java.util.HashMap;
import java.util.Map;


public class VariantResources {
    public static final Map<Item, RugShopBlock> RUGS_DYE = new HashMap<>();

    public static final Map<Item, RugShopBlock> RUGS_CARPET = new HashMap<>();

    public static final Map<Item, WindowSillShopBlock> WINDOW_SILL = new HashMap<>();

    public static final Map<Item, ShelfShopBlock> SHELF = new HashMap<>();

    public static final Map<Item, AngledShopBlock> ANGLED = new HashMap<>();

    public static void register(){}
    
    public enum wood_variant {
        
        ACACIA("acacia",11141290),
        BAMBOO("bamboo",11141290),
        BIRCH("birch",11141290),
        CHERRY("cherry",11141290),
        CRIMSON("crimson",11141290),
        DARK_OAK("dark_oak",11141290),
        MANGROVE("mangrove",11141290),
        OAK("oak",11141290),
        SPRUCE("spruce",11141290),
        WARPED("warped",11141290),
        JUNGLE("jungle",11141290);
        
        public final String name;
        public final Identifier customer;
        public final Identifier owner_trade;
        public final Identifier settings;
        public final Identifier storage;
        public final int settings_text_colour;

        wood_variant(String texture, int text_colour) {
            Identifier[] ids = GUIid(texture);
            name = texture;
            customer = ids[0];
            owner_trade = ids[1];
            settings = ids[2];
            storage = ids[3];
            settings_text_colour = text_colour;
        }

        private static Identifier[] GUIid(String texture){

            return new Identifier[]{
                    SpudaciousShops.id("textures/gui/wood_gui/customer_"+texture+".png"),
                    SpudaciousShops.id("textures/gui/wood_gui/owner_trade_"+texture+".png"),
                    SpudaciousShops.id("textures/gui/wood_gui/settings_"+texture+".png"),
                    SpudaciousShops.id("textures/gui/wood_gui/storage_"+texture+".png")
            };

        }
    }
}
