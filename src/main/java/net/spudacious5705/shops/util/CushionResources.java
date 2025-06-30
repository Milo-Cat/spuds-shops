package net.spudacious5705.shops.util;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.spudacious5705.shops.properties.Colour;

import java.util.HashMap;
import java.util.Map;

public class CushionResources {
    public static final Map<Colour, cushionColourGroup> COLOUR_MAP  = new HashMap<Colour,cushionColourGroup>();
    public static final Map<Item, cushionColourGroup> DYE_MAP = new HashMap<Item, cushionColourGroup>();
    public static final Map<Item, cushionColourGroup> WOOL_MAP = new HashMap<Item, cushionColourGroup>();


    public static final cushionColourGroup RED_GROUP = registerColourGroup(Colour.RED, Items.RED_DYE, Items.RED_WOOL);
    public static final cushionColourGroup WHITE_GROUP = registerColourGroup(Colour.WHITE, Items.WHITE_DYE, Items.WHITE_WOOL);
    public static final cushionColourGroup BLUE_GROUP = registerColourGroup(Colour.BLUE, Items.BLUE_DYE, Items.BLUE_WOOL);
    public static final cushionColourGroup PURPLE_GROUP = registerColourGroup(Colour.PURPLE, Items.PURPLE_DYE, Items.PURPLE_WOOL);
    public static final cushionColourGroup GREEN_GROUP = registerColourGroup(Colour.GREEN, Items.GREEN_DYE, Items.GREEN_WOOL);
    public static final cushionColourGroup LIME_GROUP = registerColourGroup(Colour.LIME, Items.LIME_DYE, Items.LIME_WOOL);
    public static final cushionColourGroup ORANGE_GROUP = registerColourGroup(Colour.ORANGE, Items.ORANGE_DYE, Items.ORANGE_WOOL);
    public static final cushionColourGroup GRAY_GROUP = registerColourGroup(Colour.GRAY, Items.GRAY_DYE, Items.GRAY_WOOL);
    public static final cushionColourGroup BLACK_GROUP = registerColourGroup(Colour.BLACK, Items.BLACK_DYE, Items.BLACK_WOOL);
    public static final cushionColourGroup LIGHT_GREY_GROUP = registerColourGroup(Colour.LIGHT_GRAY, Items.LIGHT_GRAY_DYE, Items.LIGHT_GRAY_WOOL); // Note: Same gray/grey spelling check
    public static final cushionColourGroup BROWN_GROUP = registerColourGroup(Colour.BROWN, Items.BROWN_DYE, Items.BROWN_WOOL);
    public static final cushionColourGroup YELLOW_GROUP = registerColourGroup(Colour.YELLOW, Items.YELLOW_DYE, Items.YELLOW_WOOL);
    public static final cushionColourGroup LIGHT_BLUE_GROUP = registerColourGroup(Colour.LIGHT_BLUE, Items.LIGHT_BLUE_DYE, Items.LIGHT_BLUE_WOOL);
    public static final cushionColourGroup CYAN_GROUP = registerColourGroup(Colour.CYAN, Items.CYAN_DYE, Items.CYAN_WOOL);
    public static final cushionColourGroup MAGENTA_GROUP = registerColourGroup(Colour.MAGENTA, Items.MAGENTA_DYE, Items.MAGENTA_WOOL);
    public static final cushionColourGroup PINK_GROUP = registerColourGroup(Colour.PINK, Items.PINK_DYE, Items.PINK_WOOL);

    public static cushionColourGroup registerColourGroup(Colour colour, Item dye, Item wool){
        cushionColourGroup group = new cushionColourGroup(colour,dye,wool);
        COLOUR_MAP.put(colour,group);
        DYE_MAP.put(dye,group);
        WOOL_MAP.put(wool,group);
        return group;
    }

    public static void initialise(){

    }

    public record cushionColourGroup(Colour colour , Item dye, Item wool){

    }
}
