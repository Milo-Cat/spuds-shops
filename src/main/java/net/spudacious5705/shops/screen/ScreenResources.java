package net.spudacious5705.shops.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.intellij.lang.annotations.MagicConstant;

import java.util.List;
import java.util.stream.Stream;

import static net.spudacious5705.shops.SpudaciousShops.id;

@OnlyIn(Dist.CLIENT)
public class ScreenResources {

    public static final int DEFAULT_TEXT_COLOUR = 0xFF000000 | 11141290;
    public static final Identifier WARNING_TEXTURE = id("textures/gui/warning_screen.png");
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
    public static final MutableComponent SHOP_STYLE_TOGGLE_TOOLTIP = Component.translatable("gui.spudaciousshops.toggle_shop_style");
    public static final MutableComponent IGNORE_NBT_TOGGLE_TOOLTIP = Component.translatable("gui.spudaciousshops.toggle_ignore_nbt");
    public static final MutableComponent PAYMENT_EMPTY_TOOLTIP = Component.translatable("gui.spudaciousshops.payment_empty_tooltip");
    public static final MutableComponent PRODUCT_EMPTY_TOOLTIP = Component.translatable("gui.spudaciousshops.product_empty_tooltip");
    public static final MutableComponent STOCK = Component.translatable("gui.spudaciousshops.stock");
    public static final MutableComponent REGISTER = Component.translatable("gui.spudaciousshops.register");
    public static final MutableComponent PAYMENT = Component.translatable("gui.spudaciousshops.payment");
    public static final MutableComponent PRODUCT = Component.translatable("gui.spudaciousshops.product");
    public static final Identifier NOTIFICATION_ICON = id("textures/gui/notification.png");
    public static final List<Component> PRODUCT_NBT_UNCHECKED_WARN = List.of(
            Component.translatable("gui.spudaciousshops.nbt_unchecked_warn_header"),
            Component.translatable("gui.spudaciousshops.nbt_unchecked_warn_line1"),
            Component.translatable("gui.spudaciousshops.nbt_unchecked_warn_line2")
    );
    public static final List<Component> SELECT_STYLE_INFO = List.of(
            Component.translatable("gui.spudaciousshops.select_style_info_header"),
            Component.translatable("gui.spudaciousshops.select_style_info_line1"),
            Component.translatable("gui.spudaciousshops.select_style_info_line2")
    );
    public static ToolTipText[] SETTINGS_HOVER_INFO_TEXTS = initSettingsHoverInfoTexts();

    public static void init() {
    }

    public static ClientTooltipComponent toFunnyClientTooltip(Component sensible) {
        return ClientTooltipComponent.create(sensible.getVisualOrderText());
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
                Stream.of(
                        permissions_title,
                        CAN.copy().append(IMPORT_ITEMS).append(COLON).append(YES),
                        CAN.copy().append(TAKE_ITEMS).append(COLON).append(YES),
                        CAN.copy().append(EDIT_PERMS).append(COLON).append(ALL),
                        CAN.copy().append(CHANGE_TRADE).append(COLON).append(YES),
                        CAN.copy().append(BREAK_SHOP).append(COLON).append(YES)
                ));
        textY += increment;
        texts[1] = new ToolTipText(MANAGER, textX, textY,
                Stream.of(
                        permissions_title,
                        CAN.copy().append(IMPORT_ITEMS).append(COLON).append(YES),
                        CAN.copy().append(TAKE_ITEMS).append(COLON).append(YES),
                        CONDITIONAL.copy().append(EDIT_PERMS).append(COLON).append(SUPERVISOR_AND_LOWER),
                        CANT.copy().append(CHANGE_TRADE).append(COLON).append(NO),
                        CANT.copy().append(BREAK_SHOP).append(COLON).append(NO)
                ));
        textY += increment;
        texts[2] = new ToolTipText(SUPERVISOR, textX, textY,
                Stream.of(
                        permissions_title,
                        CAN.copy().append(IMPORT_ITEMS).append(COLON).append(YES),
                        CAN.copy().append(TAKE_ITEMS).append(COLON).append(YES),
                        CANT.copy().append(EDIT_PERMS).append(COLON).append(NONE),
                        CANT.copy().append(CHANGE_TRADE).append(COLON).append(NO),
                        CANT.copy().append(BREAK_SHOP).append(COLON).append(NO)
                ));
        textY += increment;
        texts[3] = new ToolTipText(CLERK, textX, textY,
                Stream.of(
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
        renderCentredText(context, font, WARN_TITLE, textX, textY, 0xFF000000 | 14745600, true);
        textY += 20;
        renderCentredText(context, font, WARN_LINE_1, textX, textY, 0xFF000000 | 986895, false);
        textY += 10;
        renderCentredText(context, font, WARN_LINE_2, textX, textY, 0xFF000000 | 986895, false);
    }

    public static void renderStorageHeaders(GuiGraphics context, Font font, int screenX, int screenY) {

        renderText(context, font, STOCK, screenX + 77, screenY + 5, 0xFF000000 | 2434341, false);

        renderText(context, font, REGISTER, screenX + 14, screenY + 113, 0xFF000000 | 2434341, false);

        renderText(context, font, PAYMENT, screenX + 13, screenY + 18, 0xFF000000 | 2434341, false);

        renderText(context, font, PRODUCT, screenX + 13, screenY + 61, 0xFF000000 | 2434341, false);

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
        private final List<ClientTooltipComponent> TOOLTIP;


        private ToolTipText(MutableComponent text, int x, int y, Stream<MutableComponent> tooltip) {
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
            if (tXmax == 0 || tYmax == 0) {
                tXmax = X + 25;
                tYmax = Y + 8;
            }

            Xmax = tXmax;
            Ymax = tYmax;
            TOOLTIP = tooltip.map(ScreenResources::toFunnyClientTooltip).toList();
        }

        public void render(GuiGraphics context, Font textRenderer, int mouseX, int mouseY, int screenX, int screenY) {
            render(context, textRenderer, mouseX, mouseY, screenX, screenY, DEFAULT_TEXT_COLOUR);
        }

        public void render(GuiGraphics context, Font textRenderer, int mouseX, int mouseY, int screenX, int screenY, int colour) {
            drawText(context, textRenderer, screenX, screenY, colour);
            drawTooltipIfHovered(context, textRenderer, mouseX, mouseY, screenX, screenY);
        }

        public void drawText(GuiGraphics context, Font textRenderer, int screenX, int screenY, int colour) {
            context.drawString(textRenderer, TEXT, X + screenX, Y + screenY, colour, false);
        }

        public boolean isHovered(Font textRenderer, int mouseX, int mouseY, int screenX, int screenY) {
            int mX = mouseX - screenX;
            int mY = mouseY - screenY;
            int max = textRenderer.width(TEXT) + X;
            return mX >= X && mX <= max && mY >= Y && mY <= Ymax;
        }

        public void drawTooltipIfHovered(GuiGraphics context, Font textRenderer, int mouseX, int mouseY, int screenX, int screenY) {
            if (isHovered(textRenderer, mouseX, mouseY, screenX, screenY)) {
                context.renderTooltip(
                        textRenderer,
                        TOOLTIP,
                        mouseX,
                        mouseY,
                        DefaultTooltipPositioner.INSTANCE,
                        null);
            }
        }
    }
}