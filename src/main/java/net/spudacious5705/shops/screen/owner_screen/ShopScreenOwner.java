package net.spudacious5705.shops.screen.owner_screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.spudacious5705.shops.screen.ScreenResources;
import net.spudacious5705.shops.screen.ScreenSettingsGroup;
import net.spudacious5705.shops.screen.ToggleButtonID;
import net.spudacious5705.shops.screen.networking.ShopSelfDemotePkt;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

import static net.spudacious5705.shops.SpudaciousShops.id;
import static net.spudacious5705.shops.screen.ModScreenHandlers.CURRENCY_IMG_MAP;
import static net.spudacious5705.shops.screen.ScreenResources.*;
import static net.spudacious5705.shops.screen.owner_screen.ShopScreenHandlerOwner.*;

public class ShopScreenOwner extends AbstractContainerScreen<ShopScreenHandlerOwner> {

    //region resources
    private static final ResourceLocation RED_BUTTON = id("textures/gui/red_button.png");
    private static final ResourceLocation RED_BUTTON_SELECTED = id("textures/gui/red_button_selected.png");
    private static final ResourceLocation GREEN_BUTTON = id("textures/gui/green_button.png");
    private static final ResourceLocation GREEN_BUTTON_SELECTED = id("textures/gui/green_button_selected.png");
    private static final ResourceLocation COG_ICON = id("textures/gui/settings.png");
    private static final ResourceLocation STORAGE_ICON = id("textures/gui/storage.png");
    private static final ResourceLocation SHOPFRONT_ICON = CURRENCY_IMG_MAP.getOrDefault(
            Component.translatable("gui.spudaciousshops.currency_type").getString().charAt(0),
            id("textures/gui/currency_textures/gbp.png")
    );
    private static final ResourceLocation TAB_SELECTED = id("textures/gui/tab_selected.png");
    private static final ResourceLocation TAB_DESELECTED = id("textures/gui/tab_deselected.png");
    private static final ResourceLocation TAB_HOVER = id("textures/gui/tab_hover.png");
    private static final ResourceLocation CREATIVE_ON = id("textures/gui/creative_on.png");
    private static final ResourceLocation CREATIVE_OFF = id("textures/gui/creative_off.png");
    private static final ResourceLocation EFFECTS_ON = id("textures/gui/effects_on.png");
    private static final ResourceLocation EFFECTS_OFF = id("textures/gui/effects_off.png");
    private static final ResourceLocation NBT_IGNORE = id("textures/gui/nbt_ignore.png");
    private static final ResourceLocation NBT_CHECK = id("textures/gui/nbt_check.png");
    private static final ResourceLocation TRADE_MONO = id("textures/gui/trade_mono.png");
    private static final ResourceLocation TRADE_SELECT = id("textures/gui/trade_select.png");
    //endregion resources

    private final ScreenSettingsGroup SETTINGS;

    TradeItemWidget PaymentItemWidget;
    TradeItemWidget ProductItemWidget;

    NotificationWidget NotifShopStyleSelectOn;
    NotificationWidget NotifNBTMatchOFF;

    TabWidget SellerTabButton;
    TabWidget SettingsTabButton;
    TabWidget ShopFrontTabButton;
    ButtonWidget WarningCancel;
    ButtonWidget WarningProceed;
    ToggleWidget ToggleCreative;
    ToggleWidget ToggleIconsEffects;
    ToggleWidget ToggleShopStyle;
    ToggleWidget ToggleIgnoreNBT;

    public ShopScreenOwner(ShopScreenHandlerOwner menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 228;
        this.imageHeight = 256;
        this.SETTINGS = menu.getSettings();
        this.leftPos = (width - imageWidth)/2;
        this.topPos = (height - imageHeight)/2;

        menu.updateTabSelection();
    }

    @Override
    protected void init() {
        super.init();

        int posX = SETTINGS.tab1ButtonX() + leftPos;
        int posY = SETTINGS.tab1ButtonY() + topPos;
        SellerTabButton = new TabWidget(posX, posY, Component.literal(""), STORAGE_ICON, SELLER_TAB);


        posX = SETTINGS.tab2ButtonX() + leftPos;
        posY = SETTINGS.tab2ButtonY() + topPos;
        SettingsTabButton = new TabWidget(posX, posY, Component.literal(""), COG_ICON, SETTINGS_TAB);


        posX = SETTINGS.tab3ButtonX() + leftPos;
        posY = SETTINGS.tab3ButtonY() + topPos;
        ShopFrontTabButton = new TabWidget(posX, posY, Component.literal(""), SHOPFRONT_ICON, CUSTOMER_TAB);


        posX = 22 + leftPos;
        posY = 128 + topPos;
        WarningCancel = new ButtonWidget(posX, posY, Component.literal("CANCEL"), () -> this.menu.updateTabSelectionClientside(SETTINGS_TAB), GREEN_BUTTON, GREEN_BUTTON_SELECTED, CANCEL, 3840);

        posX += 113;
        WarningProceed = new ButtonWidget(posX, posY, Component.literal("CONTINUE"), this::WarnPopupContinue, RED_BUTTON, RED_BUTTON_SELECTED, DELETE, 984329);

        posX = SETTINGS.creativeButtonX() + leftPos;
        posY = SETTINGS.creativeButtonY() + topPos;
        ToggleCreative = new ToggleWidget(posX, posY, ToggleButtonID.CreativeToggle, CREATIVE_ON, CREATIVE_OFF, CREATIVE_TOGGLE_TOOLTIP);
        posX = SETTINGS.toggleEffectsButtonX() + leftPos;
        posY = SETTINGS.toggleEffectsButtonY() + topPos;
        ToggleIconsEffects = new ToggleWidget(posX, posY, ToggleButtonID.EffectsToggle, EFFECTS_ON, EFFECTS_OFF, EFFECTS_TOGGLE_TOOLTIP);

        PaymentItemWidget = new TradeItemWidget(menu.PaymentSlot, PAYMENT, 0, PAYMENT_EMPTY_TOOLTIP);
        ProductItemWidget = new TradeItemWidget(menu.VendingSlot, PRODUCT, 1, PRODUCT_EMPTY_TOOLTIP);


        posX = SETTINGS.shopStyleButtonX() + leftPos;
        posY = SETTINGS.shopStyleButtonY() + topPos;
        ToggleShopStyle = new ToggleWidget(posX, posY, ToggleButtonID.SelectableTradeToggle, TRADE_SELECT, TRADE_MONO, SHOP_STYLE_TOGGLE_TOOLTIP);

        posX = SETTINGS.ignoreNBTButtonX() + leftPos;
        posY = SETTINGS.ignoreNBTButtonY() + topPos;
        ToggleIgnoreNBT = new ToggleWidget(posX, posY, ToggleButtonID.IgnoreNBTToggle, NBT_IGNORE, NBT_CHECK, IGNORE_NBT_TOGGLE_TOOLTIP);

        posX = 2 + leftPos;
        posY = 78 + topPos;
        NotifShopStyleSelectOn = new NotificationWidget(posX, posY, SELECT_STYLE_INFO,
                () -> menu.getStateOfSetting(ToggleButtonID.SelectableTradeToggle));

        posX = 168 + leftPos;
        posY = 127 + topPos;
        NotifNBTMatchOFF = new NotificationWidget(posX, posY, PRODUCT_NBT_UNCHECKED_WARN,
                () -> menu.getStateOfSetting(ToggleButtonID.IgnoreNBTToggle) && !menu.getStateOfSetting(ToggleButtonID.SelectableTradeToggle));

    }

    //region rendering
    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Do nothing — this prevents the title and inventory label from rendering
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(menu.getBackgroundTexture(), leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(@NotNull GuiGraphics context, int mouseX, int mouseY, float partialTick) {
        renderBackground(context, mouseX, mouseY, partialTick);
        super.render(context, mouseX, mouseY, partialTick);
        Font font = Minecraft.getInstance().font;


        switch (menu.getActiveTab()){
            case SELLER_TAB -> {
                renderStorageHeaders(context,font,leftPos,topPos);

                renderScreenGenerics(context,mouseX,mouseY,partialTick);

                PaymentItemWidget.renderWidget(context,mouseX,mouseY,partialTick,font);
                ProductItemWidget.renderWidget(context,mouseX,mouseY,partialTick,font);

                NotifShopStyleSelectOn.renderWidget(context,mouseX,mouseY,partialTick, font);
            }
            case SETTINGS_TAB -> {

                //toggleButtons.forEach();

                if(menu.isPlayerCreative()) {
                    ToggleCreative.renderWidget(context, mouseX, mouseY, partialTick);
                }

                ToggleIconsEffects.renderWidget(context,mouseX,mouseY,partialTick);

                ToggleIgnoreNBT.renderWidget(context,mouseX,mouseY,partialTick);

                ToggleShopStyle.renderWidget(context,mouseX,mouseY,partialTick);




                for(ScreenResources.ToolTipText ttt : SETTINGS_HOVER_INFO_TEXTS){
                    ttt.render(context,font,mouseX,mouseY,leftPos,topPos);
                }

                renderScreenGenerics(context,mouseX,mouseY,partialTick);
            }
            case CUSTOMER_TAB -> {

                renderScreenGenerics(context,mouseX,mouseY,partialTick);

                NotifNBTMatchOFF.renderWidget(context,mouseX,mouseY,partialTick, font);
            }
            case WARNING_TAB -> {
                WarningCancel.renderWidget(context,mouseX,mouseY,partialTick);
                WarningProceed.renderWidget(context,mouseX,mouseY,partialTick);

                renderWarnPopupTextBody(context,font,leftPos,topPos);

            }
            default -> throw new IllegalStateException("Unexpected value: " + menu.getActiveTab());
        }

        this.renderTooltip(context, mouseX, mouseY);
    }

    private void renderScreenGenerics(@NotNull GuiGraphics context, int mouseX, int mouseY, float partialTick) {
        SellerTabButton.renderWidget(context,mouseX,mouseY,partialTick);
        SettingsTabButton.renderWidget(context,mouseX,mouseY,partialTick);
        ShopFrontTabButton.renderWidget(context,mouseX,mouseY,partialTick);
    }
    //endregion rendering

    //region interaction
    private void WarnPopupContinue(){
        PacketDistributor.sendToServer(new ShopSelfDemotePkt());
        menu.close();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {


            if(switch (menu.getActiveTab()) {
                case SETTINGS_TAB -> tryClickWidgets(mouseX,mouseY,button,
                        ToggleCreative, ToggleIconsEffects, ToggleIgnoreNBT, ToggleShopStyle,
                        SettingsTabButton, SellerTabButton, ShopFrontTabButton
                        );

                case WARNING_TAB -> tryClickWidgets(mouseX,mouseY,button,
                        WarningCancel,
                        WarningProceed
                );

                case SELLER_TAB -> tryClickWidgets(mouseX,mouseY,button,
                        SettingsTabButton, SellerTabButton, ShopFrontTabButton,
                        ProductItemWidget, PaymentItemWidget
                );

                default -> tryClickWidgets(mouseX,mouseY,button,
                        SettingsTabButton, SellerTabButton, ShopFrontTabButton
                );
            }){
                //click success
                var player = Minecraft.getInstance().player;
                if(player != null){
                    player.playSound(
                            SoundEvents.UI_BUTTON_CLICK.value()
                    );
                }
                return true;
            }



        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean tryClickWidgets(double mouseX, double mouseY, int button, CustomClickableWidget... widgets) {
        for(CustomClickableWidget widget : widgets){
            if(widget.attemptClick(mouseX,mouseY,button))return true;
        }
        return false;
    }
    //endregion interaction


    private abstract static class CustomClickableWidget extends AbstractWidget {

        public CustomClickableWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
            super(pX, pY, pWidth, pHeight, pMessage);
        }

        public boolean attemptClick(double X, double Y, int button){
            if( isHovered(X,Y) ){
                onClick(X,Y, button);
                return true;
            }
            return false;
        }

        public void onClick(double X, double Y, int button){
            if(button==0) {
                onClick(X, Y);
            }
        }

        public boolean isHovered(double X, double Y){
            X -= this.getX();
            Y -= this.getY();
            return X >= 0 && X < this.width
                    &&
                    Y >= 0 && Y < this.height;
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {}

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput pNarrationElementOutput) {}
    }

    private class TabWidget extends CustomClickableWidget {

        private final ResourceLocation ICON_TEXTURE;

        private final int relatedState;

        public TabWidget(int x, int y, Component message, ResourceLocation texture, int relatedState) {
            super(x, y, 22, 22, message);
            this.relatedState = relatedState;
            this.ICON_TEXTURE = texture;
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics context, int pMouseX, int pMouseY, float pPartialTick) {
            int x = this.getX()-3;
            int y = this.getY()-6;
            if(menu.getActiveTab() == relatedState){
                context.blit(TAB_SELECTED,x,y,32,32,0f,0f,32,32,32,32);
            }else if(isHovered(pMouseX,pMouseY)){
                context.blit(TAB_HOVER,x,y,32,32,0f,0f,32,32,32,32);
            }else{
                context.blit(TAB_DESELECTED,x,y,32,32,0f,0f,32,32,32,32);
            }
            context.blit(ICON_TEXTURE,x+6,y+9,16,16,0f,0f,16,16,16,16);
        }

        public void onClick(double mouseX, double mouseY) {
            menu.updateTabSelectionClientside(relatedState);
        }

    }

    private class ToggleWidget extends CustomClickableWidget{

        private final ResourceLocation TEXTURE_ON;
        private final ResourceLocation TEXTURE_OFF;

        private final ToggleButtonID BUTTON_ID;
        private final MutableComponent tooltip;


        public ToggleWidget(int x, int y, ToggleButtonID buttonID, ResourceLocation textureON, ResourceLocation textureOFF, MutableComponent tooltipText) {
            super(x, y, 32, 16, Component.literal(""));
            this.BUTTON_ID = buttonID;
            this.visible = false;
            this.TEXTURE_ON = textureON;
            this.TEXTURE_OFF = textureOFF;
            this.tooltip = tooltipText;
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics context, int mouseX, int mouseY, float pPartialTick) {
            int x = this.getX();
            int y = this.getY();

            boolean toggle = menu.getStateOfSetting(BUTTON_ID);

            context.blit(SETTINGS.BUTTON_BACKGROUND(),x-3,y-3,64,64,0f,0f,64,64,64,64);
            context.blit(toggle ? TEXTURE_ON : TEXTURE_OFF ,x,y,32,32,0f,0f,32,32,32,32);

            if(isHovered(mouseX,mouseY)){
                context.renderTooltip(Minecraft.getInstance().font, tooltip,mouseX,mouseY);
            }
        }

        @Override
        public void onClick(double pMouseX, double pMouseY) {
            menu.handleToggleButtonInput(BUTTON_ID);
        }
    }

    private class ButtonWidget extends CustomClickableWidget{

        private final Runnable FUNCTION;
        private final ResourceLocation TEXTURE;
        private final ResourceLocation TEXTURE_HOVERED;
        private final MutableComponent TEXT;
        private final int textX;
        private final int textY;
        private final int textColour;

        public ButtonWidget(int x, int y, Component message, Runnable function, ResourceLocation texture, ResourceLocation textureHovered, MutableComponent text, int colour) {
            super(x, y, 64, 28, message);
            this.FUNCTION = function;
            this.TEXTURE = texture;
            this.TEXTURE_HOVERED = textureHovered;
            this.textX = 32+x;
            this.textY = 13+y;
            this.TEXT = text;
            this.textColour = colour;
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics context, int pMouseX, int pMouseY, float pPartialTick) {
            int x = this.getX();
            int y = this.getY()-16;
            int tx = textX;

            if(isHovered(pMouseX,pMouseY)){

                context.blit(TEXTURE_HOVERED,x,y,64,64,0f,0f,64,64,64,64);

                tx++;

            }else{

                context.blit(TEXTURE,x,y,64,64,0f,0f,64,64,64,64);

            }

            renderCentredText(context, font, TEXT, tx,textY,textColour, false);
        }


        @Override
        public void onClick(double mouseX, double mouseY) {
            FUNCTION.run();
        }

    }

    private class TradeItemWidget extends CustomClickableWidget{

        private final ShopScreenHandlerOwner.shop_trade_slot slot;
        private final MutableComponent emptyStackTooltip;

        private TradeItemWidget(ShopScreenHandlerOwner.shop_trade_slot slot, Component message, int slotId, MutableComponent emptyStackTooltip) {
            super(slot.x, slot.y, 16, 16, message);
            this.slot = slot;
            this.emptyStackTooltip = emptyStackTooltip;
        }


        protected void renderWidget(@NotNull GuiGraphics context, int pMouseX, int pMouseY, float pPartialTick, Font font) {

            int x = this.getX()+leftPos;
            int y = this.getY()+topPos;

            ItemStack stack = slot.getItem();



            context.pose().pushPose();
            context.pose().translate(0.0F, 0.0F, 150.0F);
            if (isHovered(pMouseX,pMouseY)) {//hovered?
                context.fill(x, y, x + 16, y + 16, -2130706433);
                if(stack.isEmpty()){
                    context.renderTooltip(font, emptyStackTooltip, pMouseX, pMouseY);
                } else {
                    context.renderTooltip(font, stack, pMouseX, pMouseY);
                }
            }

            context.renderItem(stack, x, y);
            context.renderItemDecorations(font, stack, x, y, null);


            context.pose().popPose();
        }

        @Override
        public boolean isHovered(double X, double Y) {
            return super.isHovered(X-leftPos, Y-topPos);
        }

        @Override
        public void onClick(double mouseX, double mouseY, int button) {


            if(!menu.perms.canEditTrades()){
                playWarnSound(menu.playerInventory.player);
                return;
            }

            assert minecraft != null;
            assert minecraft.gameMode != null;
            assert minecraft.player != null;
            minecraft.gameMode.handleInventoryMouseClick(menu.containerId, slot.index, button, ClickType.PICKUP, minecraft.player);
        }

    }

    public static class NotificationWidget extends CustomClickableWidget {
        private final List<Component> tooltip;

        private final Supplier<Boolean> renderToggle;

        public NotificationWidget(int pX, int pY, List<Component> tooltip, Supplier<Boolean> renderToggle) {
            super(pX, pY, 16, 16, Component.literal(""));
            this.tooltip = tooltip;
            this.renderToggle = renderToggle;
        }
        public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float d, Font font) {
            if(!renderToggle.get())return;
            int x = this.getX();
            int y = this.getY();

            graphics.blit(NOTIFICATION_ICON,x,y,32,32,0f,0f,32,32,32,32);

            if(isHovered(mouseX,mouseY)){
                graphics.renderTooltip(font, tooltip, java.util.Optional.empty(), mouseX, mouseY);
            }
        }
    }
}
