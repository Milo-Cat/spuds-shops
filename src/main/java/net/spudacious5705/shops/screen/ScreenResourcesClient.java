package net.spudacious5705.shops.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.intellij.lang.annotations.MagicConstant;

import java.util.List;

import static net.spudacious5705.shops.screen.ScreenResources.*;

public final class ScreenResourcesClient {
    private ScreenResourcesClient() {}

    public static ToolTipText[] SETTINGS_HOVER_INFO_TEXTS = new ToolTipText[0];

    public static void init() {
        SETTINGS_HOVER_INFO_TEXTS = initSettingsHoverInfoTexts();
    }

    static ToolTipText[] initSettingsHoverInfoTexts() {
        int textX = 14;
        int textY = 72;
        @MagicConstant
        int increment = 23;

        ToolTipText[] texts = new ToolTipText[4];
        MutableComponent permissions_title = Component.literal("§l").append(PERMISSIONS).append(":");

        MutableComponent CAN = Component.literal("§a + ");
        MutableComponent CANT = Component.literal("§c - ");
        MutableComponent CONDITIONAL = Component.literal("§9 + ");
        MutableComponent COLON = Component.literal(": ");

        texts[0] = new ToolTipText(OWNER, textX, textY,
                List.of(
                        permissions_title,
                        CAN.copy().append(IMPORT_ITEMS).append(COLON).append(YES),
                        CAN.copy().append(TAKE_ITEMS).append(COLON).append(YES),
                        CAN.copy().append(EDIT_PERMS).append(COLON).append(ALL),
                        CAN.copy().append(CHANGE_TRADE).append(COLON).append(YES),
                        CAN.copy().append(BREAK_SHOP).append(COLON).append(YES)
                ));
        textY += increment;
        texts[1] = new ToolTipText(MANAGER, textX, textY,
                List.of(
                        permissions_title,
                        CAN.copy().append(IMPORT_ITEMS).append(COLON).append(YES),
                        CAN.copy().append(TAKE_ITEMS).append(COLON).append(YES),
                        CONDITIONAL.copy().append(EDIT_PERMS).append(COLON).append(SUPERVISOR_AND_LOWER),
                        CANT.copy().append(CHANGE_TRADE).append(COLON).append(NO),
                        CANT.copy().append(BREAK_SHOP).append(COLON).append(NO)
                ));
        textY += increment;
        texts[2] = new ToolTipText(SUPERVISOR, textX, textY,
                List.of(
                        permissions_title,
                        CAN.copy().append(IMPORT_ITEMS).append(COLON).append(YES),
                        CAN.copy().append(TAKE_ITEMS).append(COLON).append(YES),
                        CANT.copy().append(EDIT_PERMS).append(COLON).append(NONE),
                        CANT.copy().append(CHANGE_TRADE).append(COLON).append(NO),
                        CANT.copy().append(BREAK_SHOP).append(COLON).append(NO)
                ));
        textY += increment;
        texts[3] = new ToolTipText(CLERK, textX, textY,
                List.of(
                        permissions_title,
                        CAN.copy().append(IMPORT_ITEMS).append(COLON).append(YES),
                        CANT.copy().append(TAKE_ITEMS).append(COLON).append(NO),
                        CANT.copy().append(EDIT_PERMS).append(COLON).append(NONE),
                        CANT.copy().append(CHANGE_TRADE).append(COLON).append(NO),
                        CANT.copy().append(BREAK_SHOP).append(COLON).append(NO)
                ));
        return texts;
    }

    public static void renderWarnPopupTextBody(GuiGraphics context, Font font, int screenX, int screenY) {
        int textX = screenX + 110;
        int textY = screenY + 84;
        renderCentredText(context, font, WARN_TITLE, textX, textY, 14745600, true);
        textY += 20;
        renderCentredText(context, font, WARN_LINE_1, textX, textY, 986895, false);
        textY += 10;
        renderCentredText(context, font, WARN_LINE_2, textX, textY, 986895, false);
    }

    public static void renderStorageHeaders(GuiGraphics context, Font font, int screenX, int screenY) {
        renderText(context, font, STOCK, screenX + 77, screenY + 5, 2434341, false);
        renderText(context, font, REGISTER, screenX + 14, screenY + 113, 2434341, false);
        renderText(context, font, PAYMENT, screenX + 13, screenY + 18, 2434341, false);
        renderText(context, font, PRODUCT, screenX + 13, screenY + 61, 2434341, false);
    }

    public static void renderCentredText(GuiGraphics context, Font font, MutableComponent text, int x, int y, int colour, boolean shadow) {
        context.drawString(font, text, x - font.width(text) / 2, y, colour, shadow);
    }

    static void renderText(GuiGraphics context, Font font, MutableComponent text, int x, int y, int colour, boolean shadow) {
        context.drawString(font, text, x, y, colour, shadow);
    }

    public static class ToolTipText {
        private final Component TEXT;
        private final int X;
        private final int Y;
        private final int Xmax;
        private final int Ymax;
        private final List<Component> TOOLTIP;

        private ToolTipText(MutableComponent text, int x, int y, List<Component> tooltip) {
            TEXT = text;
            X = x;
            Y = y;
            int tXmax = X + 25;
            int tYmax = Y + 8;
            try {
                Font font = Minecraft.getInstance().font;
                tXmax = X + font.width(TEXT);
                tYmax = Y + font.lineHeight;
            } catch (Exception ignored) {
            }
            Xmax = tXmax;
            Ymax = tYmax;
            TOOLTIP = tooltip;
        }

        public void render(GuiGraphics context, Font textRenderer, int mouseX, int mouseY, int screenX, int screenY) {
            render(context, textRenderer, mouseX, mouseY, screenX, screenY, DEFAULT_TEXT_COLOUR);
        }

        public void render(GuiGraphics context, Font textRenderer, int mouseX, int mouseY, int screenX, int screenY, int colour) {
            context.drawString(textRenderer, TEXT, X + screenX, Y + screenY, colour, false);
            int mX = mouseX - screenX;
            int mY = mouseY - screenY;
            int max = textRenderer.width(TEXT) + X;
            if (mX >= X && mX <= max) {
                if (mY >= Y && mY <= Ymax) {
                    context.renderTooltip(textRenderer, TOOLTIP, java.util.Optional.empty(), mouseX, mouseY);
                }
            }
        }
    }
}
