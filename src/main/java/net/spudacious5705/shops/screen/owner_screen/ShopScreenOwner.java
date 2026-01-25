package net.spudacious5705.shops.screen.owner_screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.spudacious5705.shops.block.entity.AbstractShopEntity;
import net.spudacious5705.shops.screen.ScreenResources;
import net.spudacious5705.shops.screen.ScreenSettingsGroup;
import net.spudacious5705.shops.screen.ToggleButtonID;
import net.spudacious5705.shops.screen.networking.NetworkHelper;
import net.spudacious5705.shops.screen.networking.ShopSelfDemotePkt;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;

import static net.spudacious5705.shops.SpudaciousShops.getResource;
import static net.spudacious5705.shops.screen.ModScreenHandlers.CURRENCY_IMG_MAP;
import static net.spudacious5705.shops.screen.ScreenResources.*;
import static net.spudacious5705.shops.screen.owner_screen.ShopScreenHandlerOwner.*;

public class ShopScreenOwner extends AbstractContainerScreen<ShopScreenHandlerOwner> {

    private static final ResourceLocation WARNING_TEXTURE = getResource("textures/gui/warning_screen.png");

    private static final ResourceLocation RED_BUTTON = getResource("textures/gui/red_button.png");
    private static final ResourceLocation RED_BUTTON_SELECTED = getResource("textures/gui/red_button_selected.png");
    private static final ResourceLocation GREEN_BUTTON = getResource("textures/gui/green_button.png");
    private static final ResourceLocation GREEN_BUTTON_SELECTED = getResource("textures/gui/green_button_selected.png");
    private static final ResourceLocation COG_ICON = getResource("textures/gui/settings.png");
    private static final ResourceLocation STORAGE_ICON = getResource("textures/gui/storage.png");
    private static final ResourceLocation SHOPFRONT_ICON = CURRENCY_IMG_MAP.getOrDefault(
            Component.translatable("gui.spudaciousshops.currency_type").getString().charAt(0),
            getResource("textures/gui/currency_textures/gbp.png")
    );
    private static final ResourceLocation TAB_SELECTED = getResource("textures/gui/tab_selected.png");
    private static final ResourceLocation TAB_DESELECTED = getResource("textures/gui/tab_deselected.png");
    private static final ResourceLocation TAB_HOVER = getResource("textures/gui/tab_hover.png");
    private static final ResourceLocation CREATIVE_ON = getResource("textures/gui/creative_on.png");
    private static final ResourceLocation CREATIVE_OFF = getResource("textures/gui/creative_off.png");
    private static final ResourceLocation EFFECTS_ON = getResource("textures/gui/effects_on.png");
    private static final ResourceLocation EFFECTS_OFF = getResource("textures/gui/effects_off.png");


    private final ScreenSettingsGroup SETTINGS;

    private boolean isCreative;

    public ShopScreenOwner(ShopScreenHandlerOwner menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 228;
        this.imageHeight = 256;
        this.SETTINGS = menu.getSettings();
        this.leftPos = (width - imageWidth)/2;
        this.topPos = (height - imageHeight)/2;

        isCreative = playerInventory.player.isCreative();

        menu.updateTabSelection();
    }

    private void WarnPopupContinue(){
        NetworkHelper.CHANNEL.sendToServer(new ShopSelfDemotePkt());
        menu.close();
    }


    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Do nothing — this prevents the title and inventory label from rendering
    }

    @Override
    protected void init() {
        super.init();

        int posX = SETTINGS.tab1ButtonX()+leftPos;
        int posY = SETTINGS.tab1ButtonY()+topPos;
        SellerTabButton = new TabWidget(posX, posY, Component.literal(""), STORAGE_ICON, SELLER_TAB);


        posX = SETTINGS.tab2ButtonX()+leftPos;
        posY = SETTINGS.tab2ButtonY()+topPos;
        SettingsTabButton = new TabWidget(posX, posY, Component.literal(""), COG_ICON, SETTINGS_TAB);


        posX = SETTINGS.tab3ButtonX()+leftPos;
        posY = SETTINGS.tab3ButtonY()+topPos;
        ShopFrontTabButton = new TabWidget(posX, posY, Component.literal(""), SHOPFRONT_ICON, CUSTOMER_TAB);


        posX = 22+leftPos;
        posY = 128+topPos;
        WarningCancel = new ButtonWidget(posX, posY, Component.literal("CANCEL"), () -> this.menu.updateTabSelectionClientside(SETTINGS_TAB), GREEN_BUTTON, GREEN_BUTTON_SELECTED, CANCEL, 3840);

        posX += 113;
        WarningProceed = new ButtonWidget(posX, posY, Component.literal("CONTINUE"), this::WarnPopupContinue, RED_BUTTON, RED_BUTTON_SELECTED, DELETE, 984329);

        posX = SETTINGS.creativeButtonX()+leftPos;
        posY = SETTINGS.creativeButtonY()+topPos;
        ToggleCreative = new ToggleWidget(posX, posY, ToggleButtonID.CreativeToggle, CREATIVE_ON, CREATIVE_OFF, CREATIVE_TOGGLE_TOOLTIP);
        posX = SETTINGS.toggleEffectsButtonX()+leftPos;
        posY = SETTINGS.toggleEffectsButtonY()+topPos;
        ToggleIconsEffects = new ToggleWidget(posX, posY, ToggleButtonID.EffectsToggle, EFFECTS_ON, EFFECTS_OFF, EFFECTS_TOGGLE_TOOLTIP);

        PaymentItemWidget = new TradeItemWidget(menu.shopInventory, menu.PaymentSlot, PAYMENT);
        ProductItemWidget = new TradeItemWidget(menu.shopInventory, menu.VendingSlot, PRODUCT);

        /*
        posX = SETTINGS.shopStyleButtonX()+leftPos;
        posY = SETTINGS.shopStyleButtonY()+topPos;
        ToggleShopStyle = addRenderableWidget(new ToggleWidget(posX, posY, ToggleButtonID.ShopStyleToggle, SHOPFRONT_ICON, EFFECTS_OFF, "foo"));
        posX = SETTINGS.ignoreNBTButtonX()+leftPos;
        posY = SETTINGS.ignoreNBTButtonY()+topPos;
        ToggleIgnoreNBT = addRenderableWidget(new ToggleWidget(posX, posY, ToggleButtonID.IgnoreNBTToggle, SHOPFRONT_ICON, EFFECTS_OFF, "foo"));
        */
        for (ToggleButtonID value : ToggleButtonID.values()) {
            toggleButtons.put(value,
                    switch (value){
                        case CreativeToggle -> ToggleCreative;
                        case ShopStyleToggle -> ToggleShopStyle;
                        case IgnoreNBTToggle -> ToggleIgnoreNBT;
                        case EffectsToggle -> ToggleIconsEffects;
                    }
            );
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(menu.getBackgroundTexture(), leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(@NotNull GuiGraphics context, int mouseX, int mouseY, float partialTick) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, partialTick);
        Font font = Minecraft.getInstance().font;


        switch (menu.getActiveTab()){
            case SELLER_TAB -> {
                renderStorageHeaders(context,font,leftPos,topPos);

                renderScreenGenerics(context,mouseX,mouseY,partialTick);
            }
            case SETTINGS_TAB -> {

                //toggleButtons.forEach();

                if(isCreative) {
                    ToggleCreative.renderWidget(context, mouseX, mouseY, partialTick);
                }

                ToggleIconsEffects.renderWidget(context,mouseX,mouseY,partialTick);

                PaymentItemWidget.renderWidget(context,mouseX,mouseY,partialTick,font);
                ProductItemWidget.renderWidget(context,mouseX,mouseY,partialTick,font);



                for(ScreenResources.ToolTipText ttt : SETTINGS_HOVER_INFO_TEXTS){
                    ttt.render(context,font,mouseX,mouseY,leftPos,topPos);
                }

                renderScreenGenerics(context,mouseX,mouseY,partialTick);
            }
            case CUSTOMER_TAB -> {


                renderScreenGenerics(context,mouseX,mouseY,partialTick);
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

    TradeItemWidget PaymentItemWidget;
    TradeItemWidget ProductItemWidget;

    TabWidget SellerTabButton;
    TabWidget SettingsTabButton;
    TabWidget ShopFrontTabButton;
    ButtonWidget WarningCancel;
    ButtonWidget WarningProceed;
    ToggleWidget ToggleCreative;
    ToggleWidget ToggleIconsEffects;
    ToggleWidget ToggleShopStyle;
    ToggleWidget ToggleIgnoreNBT;
    private final EnumMap<ToggleButtonID, ToggleWidget> toggleButtons = new EnumMap<>(ToggleButtonID.class);


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {


            if(switch (menu.getActiveTab()) {
                case SETTINGS_TAB -> tryClickWidgets(mouseX,mouseY,button,
                        ToggleCreative,
                        ToggleIconsEffects,
                        SettingsTabButton, SellerTabButton, ShopFrontTabButton,
                        ProductItemWidget, PaymentItemWidget
                        );

                case WARNING_TAB -> tryClickWidgets(mouseX,mouseY,button,
                        WarningCancel,
                        WarningProceed
                );

                default -> tryClickWidgets(mouseX,mouseY,button,
                        SettingsTabButton, SellerTabButton, ShopFrontTabButton
                );
            }){
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

    private void renderScreenGenerics(@NotNull GuiGraphics context, int mouseX, int mouseY, float partialTick) {
        SellerTabButton.renderWidget(context,mouseX,mouseY,partialTick);
        SettingsTabButton.renderWidget(context,mouseX,mouseY,partialTick);
        ShopFrontTabButton.renderWidget(context,mouseX,mouseY,partialTick);
    }

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

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput pNarrationElementOutput) {}

    }

    private class ToggleWidget extends CustomClickableWidget{

        private final ResourceLocation TEXTURE_ON;
        private final ResourceLocation TEXTURE_OFF;

        private final ToggleButtonID BUTTON_ID;
        private final Component tooltip;


        public ToggleWidget(int x, int y, ToggleButtonID buttonID, ResourceLocation textureON, ResourceLocation textureOFF, MutableComponent tooltipText) {
            super(x, y, 32, 16, Component.literal(""));
            this.BUTTON_ID = buttonID;
            this.visible = false;
            this.TEXTURE_ON = textureON;
            this.TEXTURE_OFF = textureOFF;
            this.tooltip = tooltipText;
        }

        @Override
        protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float pPartialTick) {
            int x = this.getX();
            int y = this.getY();

            boolean toggle = menu.getStateOfSetting(BUTTON_ID);

            context.blit(SETTINGS.BUTTON_BACKGROUND(),x-3,y-3,64,64,0f,0f,64,64,64,64);
            context.blit(toggle ? TEXTURE_ON : TEXTURE_OFF ,x,y,32,32,0f,0f,32,32,32,32);

            if(isHovered){
                context.renderTooltip(Minecraft.getInstance().font, tooltip,mouseX,mouseY);
            }
        }

        @Override
        public void onClick(double pMouseX, double pMouseY) {
            menu.handleToggleButtonInput(BUTTON_ID);
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput pNarrationElementOutput) {

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

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput pNarrationElementOutput) {

        }

    }


    private class TradeItemWidget extends CustomClickableWidget{

        private final Container container;
        private final Slot slot;

        private TradeItemWidget(AbstractShopEntity.InventoryDelegate inventory, Slot slot, Component message) {
            super(slot.x, slot.y, 16, 16, message);
            this.container = inventory;
            this.slot = slot;
        }


        protected void renderWidget(@NotNull GuiGraphics context, int pMouseX, int pMouseY, float pPartialTick, Font font) {

            int x = this.getX()+leftPos;
            int y = this.getY()+topPos;

            ItemStack stack = slot.getItem();



            context.pose().pushPose();
            context.pose().translate(0.0F, 0.0F, 232.0F);
            if (isHovered(pMouseX,pMouseY)) {//hovered?
                context.fill(x, y, x + 16, y + 16, -2130706433);
                context.renderTooltip(font,stack,pMouseX,pMouseY);
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
        protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

        }

        @Override
        public void onClick(double mouseX, double mouseY, int button) {



            ItemStack itemstack = draggingItem.isEmpty() ? menu.getCarried() :  draggingItem;
            if (itemstack.isEmpty()) {
                /*slot.onTake();

                int l1 = 8;
                int i2 = this.draggingItem.isEmpty() ? 8 : 16;
                String s = null;
                if (!this.draggingItem.isEmpty() && this.isSplittingStack) {
                    itemstack = itemstack.copyWithCount(Mth.ceil((float)itemstack.getCount() / 2.0F));
                } else if (this.isQuickCrafting && this.quickCraftSlots.size() > 1) {
                    itemstack = itemstack.copyWithCount(this.quickCraftingRemainder);
                    if (itemstack.isEmpty()) {
                        s = ChatFormatting.YELLOW + "0";
                    }
                }*/


            } else {

            }
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput pNarrationElementOutput) {

        }

    }
}
