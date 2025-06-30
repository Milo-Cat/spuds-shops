package net.spudacious5705.shops.properties;

import net.minecraft.state.property.BooleanProperty;

public class ModProperties {
    /**
            old way of storing the cushion colour
     public static final EnumProperty<Colour> CUSHION_COLOUR = EnumProperty.of("colour", Colour.class);
     */


    public static final BooleanProperty BREAKABLE = BooleanProperty.of("breakable");

    public static void registerModProperties(){}
}
