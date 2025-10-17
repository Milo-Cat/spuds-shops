package net.spudacious5705.shops.util;


import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.spudacious5705.shops.properties.Colour;

import java.util.HashMap;
import java.util.Map;

public class CushionResources {
    public static final Map<Colour, cushionColourGroup> COLOUR_MAP  = new HashMap<>();
    public static final Map<Item, cushionColourGroup> DYE_MAP = new HashMap<>();
    public static final Map<Item, cushionColourGroup> WOOL_MAP = new HashMap<>();


    static void registerColourGroups()
    {
        registerColourGroup(Colour.RED, Items.RED_DYE, Items.RED_WOOL);
        registerColourGroup(Colour.WHITE, Items.WHITE_DYE, Items.WHITE_WOOL);
        registerColourGroup(Colour.BLUE, Items.BLUE_DYE, Items.BLUE_WOOL);
        registerColourGroup(Colour.PURPLE, Items.PURPLE_DYE, Items.PURPLE_WOOL);
        registerColourGroup(Colour.GREEN, Items.GREEN_DYE, Items.GREEN_WOOL);
        registerColourGroup(Colour.LIME, Items.LIME_DYE, Items.LIME_WOOL);
        registerColourGroup(Colour.ORANGE, Items.ORANGE_DYE, Items.ORANGE_WOOL);
        registerColourGroup(Colour.GRAY, Items.GRAY_DYE, Items.GRAY_WOOL);
        registerColourGroup(Colour.BLACK, Items.BLACK_DYE, Items.BLACK_WOOL);
        registerColourGroup(Colour.LIGHT_GRAY, Items.LIGHT_GRAY_DYE, Items.LIGHT_GRAY_WOOL);
        registerColourGroup(Colour.BROWN, Items.BROWN_DYE, Items.BROWN_WOOL);
        registerColourGroup(Colour.YELLOW, Items.YELLOW_DYE, Items.YELLOW_WOOL);
        registerColourGroup(Colour.LIGHT_BLUE, Items.LIGHT_BLUE_DYE, Items.LIGHT_BLUE_WOOL);
        registerColourGroup(Colour.CYAN, Items.CYAN_DYE, Items.CYAN_WOOL);
        registerColourGroup(Colour.MAGENTA, Items.MAGENTA_DYE, Items.MAGENTA_WOOL);
        registerColourGroup(Colour.PINK, Items.PINK_DYE, Items.PINK_WOOL);
    }

    public static cushionColourGroup registerColourGroup(Colour colour, Item dye, Item wool){
        cushionColourGroup group = new cushionColourGroup(colour,dye,wool);
        COLOUR_MAP.put(colour,group);
        DYE_MAP.put(dye,group);
        WOOL_MAP.put(wool,group);
        return group;
    }

    public static void initialise(){
        registerColourGroups();
    }

    public record cushionColourGroup(Colour colour , Item dye, Item wool){

    }
}
