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

    static final int DEFAULT_TEXT_COLOUR = 11141290;

    static final MutableComponent OWNER = Component.translatable("gui.spudaciousshops.owner");
    static final MutableComponent MANAGER = Component.translatable("gui.spudaciousshops.manager");
    static final MutableComponent SUPERVISOR = Component.translatable("gui.spudaciousshops.supervisor");
    static final MutableComponent CLERK = Component.translatable("gui.spudaciousshops.clerk");
    static final MutableComponent WARN_TITLE = Component.translatable("gui.spudaciousshops.delete_warn_title");
    static final MutableComponent WARN_LINE_1 = Component.translatable("gui.spudaciousshops.delete_warn_message_line1");
    static final MutableComponent WARN_LINE_2 = Component.translatable("gui.spudaciousshops.delete_warn_message_line2");
    static final MutableComponent CANCEL = Component.translatable("gui.spudaciousshops.cancel");
    static final MutableComponent DELETE = Component.translatable("gui.spudaciousshops.delete");

    static final MutableComponent PERMISSIONS = Component.translatable("gui.spudaciousshops.text_permissions");
    static final MutableComponent IMPORT_ITEMS = Component.translatable("gui.spudaciousshops.text_import_items");
    static final MutableComponent TAKE_ITEMS = Component.translatable("gui.spudaciousshops.text_take_items");
    static final MutableComponent EDIT_PERMS = Component.translatable("gui.spudaciousshops.text_edit_perms");
    static final MutableComponent CHANGE_TRADE = Component.translatable("gui.spudaciousshops.text_change_trade");
    static final MutableComponent BREAK_SHOP = Component.translatable("gui.spudaciousshops.text_break_shop");
    static final MutableComponent YES = Component.translatable("gui.spudaciousshops.text_yes");
    static final MutableComponent NO = Component.translatable("gui.spudaciousshops.text_no");
    static final MutableComponent ALL = Component.translatable("gui.spudaciousshops.text_all");
    static final MutableComponent SUPERVISOR_AND_LOWER = Component.translatable("gui.spudaciousshops.text_supervisor_and_lower");
    static final MutableComponent NONE = Component.translatable("gui.spudaciousshops.text_none");
    static final MutableComponent CREATIVE_TOGGLE_TOOLTIP = Component.translatable("gui.spudaciousshops.toggle_creative");
    static final MutableComponent EFFECTS_TOGGLE_TOOLTIP = Component.translatable("gui.spudaciousshops.toggle_effects");



    static ShopScreenOwner.ToolTipText[] addToolTipTexts(){
        int textX = 14;
        int textY = 72;
        @MagicConstant
        int increment = 23;
        int colour = this.menu.SCREEN_SETTINGS.SETTINGS_TEXT_COLOUR();//11141290;

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

    static ShopScreenOwner.Warn_popup_texts[] addWarnPopupTexts(){
        int textX = leftPos+110;
        int textY = topPos+84;
        ShopScreenOwner.Warn_popup_texts[] warn_texts = new ShopScreenOwner.Warn_popup_texts[3];
        warn_texts[0] = new ShopScreenOwner.Warn_popup_texts(textX,textY,WARN_TITLE,14745600, true);
        textY += 20;
        warn_texts[1] = new ShopScreenOwner.Warn_popup_texts(textX,textY,WARN_LINE_1,986895, false);
        textY += 10;
        warn_texts[2] = new ShopScreenOwner.Warn_popup_texts(textX,textY,WARN_LINE_2,986895, false);
        return warn_texts;
    }

    static ShopScreenOwner.Warn_popup_texts[] addStorageTexts(){
        ShopScreenOwner.Warn_popup_texts[] storage_texts = new ShopScreenOwner.Warn_popup_texts[4];
        storage_texts[0] = new ShopScreenOwner.Warn_popup_texts(leftPos+90,topPos+5, Component.literal("Stock"),2434341, false);

        storage_texts[1] = new ShopScreenOwner.Warn_popup_texts(leftPos+35,topPos+113, Component.literal("Register"),2434341, false);//8282679

        storage_texts[2] = new ShopScreenOwner.Warn_popup_texts(leftPos+33,topPos+18, Component.literal("Payment"),2434341, false);

        storage_texts[3] = new ShopScreenOwner.Warn_popup_texts(leftPos+33,topPos+61,  Component.literal("Product"),2434341, false);
        return storage_texts;
    }

    static ShopScreenOwner.ToolTipText[] TEXTS = addToolTipTexts();

    static class ToolTipText{
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
