package net.spudacious5705.shops.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.properties.Colour;

import java.util.HashMap;
import java.util.Map;

import static net.spudacious5705.shops.properties.Colour.*;

@Environment(EnvType.CLIENT)
public class CushionTextures {
    public static final Map<Colour,Identifier> TEXTURE_MAP = new HashMap<Colour,Identifier>();

    public static final Identifier TEXTURE_RED = registerTexture(RED,"textures/block/cushion/red.png");
    public static final Identifier TEXTURE_WHITE = registerTexture(WHITE,"textures/block/cushion/white.png");
    public static final Identifier TEXTURE_BLUE = registerTexture(BLUE,"textures/block/cushion/blue.png");
    public static final Identifier TEXTURE_PURPLE = registerTexture(PURPLE,"textures/block/cushion/purple.png");
    public static final Identifier TEXTURE_GREEN = registerTexture(GREEN,"textures/block/cushion/green.png");
    public static final Identifier TEXTURE_LIME = registerTexture(LIME,"textures/block/cushion/lime.png");
    public static final Identifier TEXTURE_ORANGE = registerTexture(ORANGE,"textures/block/cushion/orange.png");
    public static final Identifier TEXTURE_GRAY = registerTexture(GRAY,"textures/block/cushion/gray.png");
    public static final Identifier TEXTURE_BLACK = registerTexture(BLACK,"textures/block/cushion/black.png");
    public static final Identifier TEXTURE_LIGHT_GRAY = registerTexture(LIGHT_GRAY,"textures/block/cushion/light_gray.png");
    public static final Identifier TEXTURE_BROWN = registerTexture(BROWN,"textures/block/cushion/brown.png");
    public static final Identifier TEXTURE_YELLOW = registerTexture(YELLOW,"textures/block/cushion/yellow.png");
    public static final Identifier TEXTURE_LIGHT_BLUE = registerTexture(LIGHT_BLUE,"textures/block/cushion/light_blue.png");
    public static final Identifier TEXTURE_CYAN = registerTexture(CYAN,"textures/block/cushion/cyan.png");
    public static final Identifier TEXTURE_MAGENTA = registerTexture(MAGENTA,"textures/block/cushion/magenta.png");
    public static final Identifier TEXTURE_PINK = registerTexture(PINK,"textures/block/cushion/pink.png");

    public static Identifier registerTexture(Colour colour, String path) {
        Identifier texture = SpudaciousShops.id(path);
        TEXTURE_MAP.put(colour, texture);
        return texture;
    }

    public static void initialiseCushionTextures(){
    }
}
