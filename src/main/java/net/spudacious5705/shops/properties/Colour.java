package net.spudacious5705.shops.properties;

import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public enum Colour implements StringIdentifiable {
    RED("red", 0),
    WHITE("white", 1),
    BLUE("blue", 2),
    PURPLE("purple", 3),
    GREEN("green", 4),
    LIME("lime", 5),
    ORANGE("orange", 6),
    GRAY("gray", 7),
    BLACK("black", 8),
    LIGHT_GRAY("light_gray", 9),
    BROWN("brown", 10),
    YELLOW("yellow", 11),
    LIGHT_BLUE("light_blue", 12),
    CYAN("cyan", 13),
    MAGENTA("magenta", 14),
    PINK("pink", 15);

    private final String name;
    private final int id;

    Colour(String colour, int i) {
        name = colour;
        id = i;
    }

    public int getId() {
        return id;
    }

    @Override
    public String asString() {
        return name;
    }

    @Nullable
    public static Colour fromId(int checkID){
        return Arrays.stream(values()).filter(colour -> colour.id == checkID).findFirst().orElse(null);
    }

    public boolean matchesString(String test) {
        return Objects.equals(this.name, test);
    }
}
