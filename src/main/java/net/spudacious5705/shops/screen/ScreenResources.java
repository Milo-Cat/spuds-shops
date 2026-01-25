package net.spudacious5705.shops.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.intellij.lang.annotations.MagicConstant;

import java.util.List;

public class ScreenResources {

    public static void init(){};

    public static final int DEFAULT_TEXT_COLOUR = 11141290;

    public static final MutableComponent OWNER = Component.translatable("gui.spudaciousshops.owner");
    public static final MutableComponent MANAGER = Component.translatable("gui.spudaciousshops.manager");
    public static final MutableComponent SUPERVISOR = Component.translatable("gui.spudaciousshops.supervisor");
    public static final MutableComponent CLERK = Component.translatable("gui.spudaciousshops.clerk");
    public static final MutableComponent WARN_TITLE = Component.translatable("gui.spudaciousshops.delete_warn_title");
    public static final MutableComponent WARN_LINE_1 = Component.translatable("gui.spudaciousshops.delete_warn_message_line1");
    public static final MutableComponent WARN_LINE_2 = Component.translatable("gui.spudaciousshops.delete_warn_message_line2");
    public static final MutableComponent CANCEL = Component.translatable("gui.spudaciousshops.cancel");
    public static final MutableComponent DELETE = Component.translatable("gui.spudaciousshops.delete");

    public static final MutableComponent PERMISSIONS = Component.translatable("gui.spudaciousshops.text_permissions");
    public static final MutableComponent IMPORT_ITEMS = Component.translatable("gui.spudaciousshops.text_import_items");
    public static final MutableComponent TAKE_ITEMS = Component.translatable("gui.spudaciousshops.text_take_items");
    public static final MutableComponent EDIT_PERMS = Component.translatable("gui.spudaciousshops.text_edit_perms");
    public static final MutableComponent CHANGE_TRADE = Component.translatable("gui.spudaciousshops.text_change_trade");
    public static final MutableComponent BREAK_SHOP = Component.translatable("gui.spudaciousshops.text_break_shop");
    public static final MutableComponent YES = Component.translatable("gui.spudaciousshops.text_yes");
    public static final MutableComponent NO = Component.translatable("gui.spudaciousshops.text_no");
    public static final MutableComponent ALL = Component.translatable("gui.spudaciousshops.text_all");
    public static final MutableComponent SUPERVISOR_AND_LOWER = Component.translatable("gui.spudaciousshops.text_supervisor_and_lower");
    public static final MutableComponent NONE = Component.translatable("gui.spudaciousshops.text_none");
    public static final MutableComponent CREATIVE_TOGGLE_TOOLTIP = Component.translatable("gui.spudaciousshops.toggle_creative");
    public static final MutableComponent EFFECTS_TOGGLE_TOOLTIP = Component.translatable("gui.spudaciousshops.toggle_effects");

    public static final MutableComponent STOCK = Component.translatable("gui.spudaciousshops.stock");
    public static final MutableComponent REGISTER = Component.translatable("gui.spudaciousshops.register");
    public static final MutableComponent PAYMENT = Component.translatable("gui.spudaciousshops.payment");
    public static final MutableComponent PRODUCT = Component.translatable("gui.spudaciousshops.product");

    public static ToolTipText[] SETTINGS_HOVER_INFO_TEXTS = initSettingsHoverInfoTexts();

    static ToolTipText[] initSettingsHoverInfoTexts(){
        int textX = 14;
        int textY = 72;
        @MagicConstant
        int increment = 23;

        ToolTipText[] texts = new ToolTipText[4];
        MutableComponent permissions_title = Component.literal("§l" + PERMISSIONS + ":");
        texts[0] = new ToolTipText(OWNER, textX, textY,
                List.of(
                        permissions_title,
                        Component.literal("§a + "+IMPORT_ITEMS+": "+YES),
                        Component.literal("§a + "+TAKE_ITEMS+": "+YES),
                        Component.literal("§a + "+EDIT_PERMS+": "+ALL),
                        Component.literal("§a + "+CHANGE_TRADE+": "+YES),
                        Component.literal("§a + "+BREAK_SHOP+": "+YES)
                ));
        textY += increment;
        texts[1] = new ToolTipText(MANAGER, textX, textY,
                List.of(
                        permissions_title,
                        Component.literal("§a + "+IMPORT_ITEMS+": "+YES),
                        Component.literal("§a + "+TAKE_ITEMS+": "+YES),
                        Component.literal("§9 + "+EDIT_PERMS+": "+SUPERVISOR_AND_LOWER),
                        Component.literal("§c - "+CHANGE_TRADE+": "+NO),
                        Component.literal("§c - "+BREAK_SHOP+": "+NO)
                ));
        textY += increment;
        texts[2] = new ToolTipText(SUPERVISOR, textX, textY,
                List.of(
                        permissions_title,
                        Component.literal("§a + "+IMPORT_ITEMS+": "+YES),
                        Component.literal("§a + "+TAKE_ITEMS+": "+YES),
                        Component.literal("§c - "+EDIT_PERMS+": "+NONE),
                        Component.literal("§c - "+CHANGE_TRADE+": "+NO),
                        Component.literal("§c - "+BREAK_SHOP+": "+NO)
                ));
        textY += increment;
        texts[3] = new ToolTipText(CLERK, textX, textY,
                List.of(
                        permissions_title,
                        Component.literal("§a + "+IMPORT_ITEMS+": "+YES),
                        Component.literal("§c - "+TAKE_ITEMS+": "+NO),
                        Component.literal("§c - "+EDIT_PERMS+": "+NONE),
                        Component.literal("§c - "+CHANGE_TRADE+": "+NO),
                        Component.literal("§c - "+BREAK_SHOP+": "+NO)
                ));
        return texts;
    }

    public static void renderWarnPopupTextBody(GuiGraphics context, Font font, int screenX, int screenY){

        int textX = screenX+110;
        int textY = screenY+84;
        renderCentredText(context, font, WARN_TITLE, textX,textY,14745600, true);
        textY += 20;
        renderCentredText(context, font, WARN_LINE_1, textX,textY,986895, false);
        textY += 10;
        renderCentredText(context, font, WARN_LINE_2, textX,textY,986895, false);
    }

    public static void renderStorageHeaders(GuiGraphics context, Font font, int screenX, int screenY){

        renderText(context, font, STOCK,screenX+77,screenY+5,2434341, false);

        renderText(context, font, REGISTER,screenX+14,screenY+113,2434341, false);

        renderText(context, font, PAYMENT,screenX+13,screenY+18,2434341, false);

        renderText(context, font, PRODUCT,screenX+13,screenY+61,2434341, false);

    }

    public static void renderCentredText(GuiGraphics context, Font font, MutableComponent text, int x, int y, int colour, boolean shadow){
        context.drawString(font, text, x - font.width(text) / 2, y, colour, shadow);
    }

    static void renderText(GuiGraphics context, Font font, MutableComponent text, int x, int y, int colour, boolean shadow){
        context.drawString(font, text, x, y, colour, shadow);
    }

    public static class ToolTipText{
        private final Component TEXT;
        private final int X;
        private final int Y;
        private final int Xmax;
        private final int Ymax;
        private final List<Component> TOOLTIP;


        private ToolTipText(Component text, int x, int y, List<Component> tooltip) {
            TEXT = text;
            X = x;
            Y = y;
            int tXmax = 0;
            int tYmax = 0;
            try {
                Font font = Minecraft.getInstance().font;
                tXmax = X + font.width(TEXT);
                tYmax = Y + font.lineHeight;
            } catch (Exception ignored) {
            }
            if(tXmax == 0 || tYmax == 0){
                tXmax = X+25;
                tYmax = Y+8;
            }

            Xmax = tXmax;
            Ymax = tYmax;
            TOOLTIP = tooltip;
        }

        public void render(GuiGraphics context, Font textRenderer, int mouseX, int mouseY, int screenX, int screenY){
            render(context, textRenderer, mouseX, mouseY, screenX, screenY, DEFAULT_TEXT_COLOUR);
        }

        public void render(GuiGraphics context, Font textRenderer, int mouseX, int mouseY, int screenX, int screenY, int colour){
            context.drawString(textRenderer, TEXT, X, Y, colour, false);
            mouseX -= screenX;
            mouseY -= screenY;
            if(mouseX >= X && mouseX<=Xmax){
                if(mouseY >= Y && mouseY<=Ymax){
                    context.renderTooltip(textRenderer,TOOLTIP, java.util.Optional.empty(),mouseX,mouseY);
                }
            }
        }
    }

}
