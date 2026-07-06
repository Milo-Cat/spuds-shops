package net.spudacious5705.shops.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import static net.spudacious5705.shops.SpudaciousShops.id;

/** Server-safe GUI strings and resource locations. Client rendering lives in {@link ScreenResourcesClient}. */
public class ScreenResources {

    public static final int DEFAULT_TEXT_COLOUR = 11141290;

    public static final ResourceLocation WARNING_TEXTURE = id("textures/gui/warning_screen.png");

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

    public static final ResourceLocation NOTIFICATION_ICON = id("textures/gui/notification.png");

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
}
