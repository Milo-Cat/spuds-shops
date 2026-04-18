package net.spudacious5705.shops.block.resources;


import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.spudacious5705.shops.properties.Colour;

import java.util.HashMap;
import java.util.Map;

import static net.spudacious5705.shops.SpudaciousShops.id;
import static net.spudacious5705.shops.properties.Colour.*;

@OnlyIn(Dist.CLIENT)
public class CushionTextures {
    public static final Map<Colour, Identifier> TEXTURE_MAP = new HashMap<>();

    public static final Identifier TEXTURE_RED = registerTexture(RED, "block/cushion/red");
    public static final Identifier TEXTURE_WHITE = registerTexture(WHITE, "block/cushion/white");
    public static final Identifier TEXTURE_BLUE = registerTexture(BLUE, "block/cushion/blue");
    public static final Identifier TEXTURE_PURPLE = registerTexture(PURPLE, "block/cushion/purple");
    public static final Identifier TEXTURE_GREEN = registerTexture(GREEN, "block/cushion/green");
    public static final Identifier TEXTURE_LIME = registerTexture(LIME, "block/cushion/lime");
    public static final Identifier TEXTURE_ORANGE = registerTexture(ORANGE, "block/cushion/orange");
    public static final Identifier TEXTURE_GRAY = registerTexture(GRAY, "block/cushion/gray");
    public static final Identifier TEXTURE_BLACK = registerTexture(BLACK, "block/cushion/black");
    public static final Identifier TEXTURE_LIGHT_GRAY = registerTexture(LIGHT_GRAY, "block/cushion/light_gray");
    public static final Identifier TEXTURE_BROWN = registerTexture(BROWN, "block/cushion/brown");
    public static final Identifier TEXTURE_YELLOW = registerTexture(YELLOW, "block/cushion/yellow");
    public static final Identifier TEXTURE_LIGHT_BLUE = registerTexture(LIGHT_BLUE, "block/cushion/light_blue");
    public static final Identifier TEXTURE_CYAN = registerTexture(CYAN, "block/cushion/cyan");
    public static final Identifier TEXTURE_MAGENTA = registerTexture(MAGENTA, "block/cushion/magenta");
    public static final Identifier TEXTURE_PINK = registerTexture(PINK, "block/cushion/pink");

    public static Identifier registerTexture(Colour colour, String path) {
        Identifier texture = id(path);
        TEXTURE_MAP.put(colour, texture);
        return texture;
    }

    public static void initialiseCushionTextures() {
    }
}
