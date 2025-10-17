package net.spudacious5705.shops.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.block.custom.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


public class VariantResources {
    public static <T extends AbstractShopBlock> void putItem(
            Map<Item, T> map,
            PostRegAssigner<Item> assigner,
            Supplier<T> shopGetter
    ){
        assigner.copy().assignTo(key ->{
            map.put(key,shopGetter.get());
        });
    }

    public static <T extends AbstractShopBlock> void putBlock(
            Map<Item, T> map,
            PostRegAssigner<Block> assigner,
            Supplier<T> shopGetter
    ){
        assigner.copy().assignTo(key ->{
            map.put(key.asItem(),shopGetter.get());
        });
    }

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
        CHERRY("cherry",0),
        CRIMSON("crimson",0),
        DARK_OAK("dark_oak",11141290),
        MANGROVE("mangrove",0),
        OAK("oak",11141290),
        SPRUCE("spruce",11141290),
        WARPED("warped",0),
        JUNGLE("jungle",11141290);
        
        public final String name;
        public final ResourceLocation customer;
        public final ResourceLocation owner_trade;
        public final ResourceLocation settings;
        public final ResourceLocation storage;
        public final ResourceLocation settings_button;
        public final int settings_text_colour;

        wood_variant(String texture, int text_colour) {
            ResourceLocation[] ids = GUIid(texture);
            name = texture;
            customer = ids[0];
            owner_trade = ids[1];
            settings = ids[2];
            storage = ids[3];
            settings_button = ids[4];
            settings_text_colour = text_colour;
        }

        private static ResourceLocation[] GUIid(String texture){

            return new ResourceLocation[]{
                    SpudaciousShops.id("textures/gui/wood_gui/customer_"+texture+".png"),
                    SpudaciousShops.id("textures/gui/wood_gui/owner_trade_"+texture+".png"),
                    SpudaciousShops.id("textures/gui/wood_gui/settings_"+texture+".png"),
                    SpudaciousShops.id("textures/gui/wood_gui/storage_"+texture+".png"),
                    SpudaciousShops.id("textures/gui/wood_gui/button_back_"+texture+".png")
            };

        }
    }


}
