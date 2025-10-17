package net.spudacious5705.shops.screen;

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
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.spudacious5705.shops.screen.networking.NetworkHelper;
import net.spudacious5705.shops.screen.networking.ShopSelfDemotePkt;
import org.intellij.lang.annotations.MagicConstant;

import java.util.EnumMap;
import java.util.List;

import static net.spudacious5705.shops.SpudaciousShops.getResource;
import static net.spudacious5705.shops.screen.ModScreenHandlers.CURRENCY_IMG_MAP;
import static net.spudacious5705.shops.screen.ShopScreenHandlerOwner.*;

public class ShopScreenOwner extends AbstractContainerScreen<ShopScreenHandlerOwner> {
    
    private final ScreenSettingsGroup SETTINGS;

    private ResourceLocation TEXTURE;

    private static final ResourceLocation WARNING_TEXTURE = getResource("textures/gui/warning_screen.png");

    private static final ResourceLocation RED_BUTTON = getResource("textures/gui/red_button.png");
    private static final ResourceLocation RED_BUTTON_SELECTED = getResource("textures/gui/red_button_selected.png");
    private static final ResourceLocation GREEN_BUTTON = getResource("textures/gui/green_button.png");
    private static final ResourceLocation GREEN_BUTTON_SELECTED = getResource("textures/gui/green_button_selected.png");


    public ShopScreenOwner(ShopScreenHandlerOwner menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 228;
        this.imageHeight = 256;
        this.SETTINGS = menu.getSettings();
        this.TEXTURE = SETTINGS.SELLER().textureID();
        this.leftPos = (width - imageWidth)/2;
        this.topPos = (height - imageHeight)/2;

        menu.initiateWarn(this::openWarnPopup);
        menu.settingsUpdater(this::updateToggleButtonFromPacket);
        menu.setWidgetFunction(this::setWidgetsVisible);
        menu.updateTabSelection();
    }

    private void closeWarnPopup(){
        WarningCancel.visible=false;
        WarningProceed.visible=false;
        SettingsTabButton.visible=true;
        SellerTabButton.visible=true;
        ShopFrontTabButton.visible=true;
        switchToSettingsTab();
    }

    private void WarnPopupContinue(){
        NetworkHelper.CHANNEL.sendToServer(new ShopSelfDemotePkt());
        menu.close();
    }



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

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Do nothing — this prevents the title and inventory label from rendering
        //TODO perhaps implement this in fabric
    }

    @Override
    protected void init() {
        super.init();

        int posX = SETTINGS.tab1ButtonX()+leftPos;
        int posY = SETTINGS.tab1ButtonY()+topPos;
        SellerTabButton = addRenderableWidget(new TabWidget(posX, posY, Component.literal(""), this::switchToSellerTab, true, STORAGE_ICON, true));


        posX = SETTINGS.tab2ButtonX()+leftPos;
        posY = SETTINGS.tab2ButtonY()+topPos;
        SettingsTabButton = addRenderableWidget(new TabWidget(posX, posY, Component.literal(""), this::switchToSettingsTab, true, COG_ICON));


        posX = SETTINGS.tab3ButtonX()+leftPos;
        posY = SETTINGS.tab3ButtonY()+topPos;
        ShopFrontTabButton = addRenderableWidget(new TabWidget(posX, posY, Component.literal(""), this::switchToCustomerTab, true, SHOPFRONT_ICON));


        posX = 22+leftPos;
        posY = 128+topPos;
        WarningCancel = addRenderableWidget(new ButtonWidget(posX, posY, Component.literal("CANCEL"), this::closeWarnPopup, GREEN_BUTTON, GREEN_BUTTON_SELECTED, CANCEL, 3840));

        posX += 113;
        WarningProceed = addRenderableWidget(new ButtonWidget(posX, posY, Component.literal("CONTINUE"), this::WarnPopupContinue, RED_BUTTON, RED_BUTTON_SELECTED, DELETE, 984329));

        posX = SETTINGS.creativeButtonX()+leftPos;
        posY = SETTINGS.creativeButtonY()+topPos;
        ToggleCreative = addRenderableWidget(new ToggleWidget(posX, posY, ToggleButtonID.CreativeToggle, CREATIVE_ON, CREATIVE_OFF, CREATIVE_TOGGLE_TOOLTIP));
        posX = SETTINGS.toggleEffectsButtonX()+leftPos;
        posY = SETTINGS.toggleEffectsButtonY()+topPos;
        ToggleIconsEffects = addRenderableWidget(new ToggleWidget(posX, posY, ToggleButtonID.EffectsToggle, EFFECTS_ON, EFFECTS_OFF, EFFECTS_TOGGLE_TOOLTIP));
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
        switch (menu.getActiveTab()) {
            case SETTINGS_TAB -> {
                settingsGUI();
            }
            case CUSTOMER_TAB -> {
                customerGUI();
            }
            case WARNING_TAB -> {
                warnGUI();
            }
            default -> { //SELLER_TAB
                sellerGUI();
            }
        }

        WARN_TEXTS = addWarnPopupTexts();
        STORAGE_TEXTS = addStorageTexts();
        TEXTS = addToolTipTexts();
    }

    private void switchToCustomerTab() {
        this.menu.updateTabSelectionClientside(ShopScreenHandlerOwner.CUSTOMER_TAB);
        customerGUI();
    }
    protected void customerGUI(){
        TEXTURE = SETTINGS.CUSTOMER().textureID();
        ShopFrontTabButton.toggle();
        SellerTabButton.unToggle();
        SettingsTabButton.unToggle();this.setWidgetsVisible(false);
    }

    private void switchToSellerTab(){
        this.menu.updateTabSelectionClientside(ShopScreenHandlerOwner.SELLER_TAB);
        sellerGUI();
    }

    protected void sellerGUI(){
        TEXTURE = SETTINGS.SELLER().textureID();
        SellerTabButton.toggle();
        ShopFrontTabButton.unToggle();
        SettingsTabButton.unToggle();this.setWidgetsVisible(false);
    }

    private void switchToSettingsTab() {
        this.menu.updateTabSelectionClientside(SETTINGS_TAB);
        settingsGUI();
    }
    protected void settingsGUI(){
        TEXTURE = SETTINGS.SETTINGS().textureID();
        SettingsTabButton.toggle();this.setWidgetsVisible(true);
        ShopFrontTabButton.unToggle();
        SellerTabButton.unToggle();
    }

    void openWarnPopup(){
        this.menu.updateTabSelectionClientside(WARNING_TAB);
        warnGUI();
        LocalPlayer player = Minecraft.getInstance().player;
        if(player != null) {
            Minecraft.getInstance().player.playSound(
                    SoundEvents.NOTE_BLOCK_GUITAR.value(),
                    3.0F,
                    0.3F
            );
        }
    }

    protected void warnGUI(){
        TEXTURE = WARNING_TEXTURE;
        SettingsTabButton.visible=false;
        SellerTabButton.visible=false;
        ShopFrontTabButton.visible=false;
        WarningCancel.visible=true;
        WarningProceed.visible=true;

        this.setWidgetsVisible(false);
    }

    @OnlyIn(Dist.CLIENT)
    public void updateTabSelectionResponse(int tab) {
        if(this.menu.updateTabSelectionResponse(tab)) {
            switch (tab) {
                case WARNING_TAB -> warnGUI();
                case ShopScreenHandlerOwner.CUSTOMER_TAB -> customerGUI();
                case SETTINGS_TAB -> settingsGUI();
                default -> sellerGUI();
            }
        }
    }


    TabWidget SellerTabButton;
    TabWidget SettingsTabButton;
    TabWidget ShopFrontTabButton;
    ButtonWidget WarningCancel;
    ButtonWidget WarningProceed;
    ToggleWidget ToggleCreative;
    ToggleWidget ToggleIconsEffects;
    ToggleWidget ToggleShopStyle;
    ToggleWidget ToggleIgnoreNBT;
    protected final EnumMap<ToggleButtonID, ToggleWidget> toggleButtons = new EnumMap<>(ToggleButtonID.class);


    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    private static final Component OWNER = Component.translatable("gui.spudaciousshops.owner");
    private static final Component MANAGER = Component.translatable("gui.spudaciousshops.manager");
    private static final Component SUPERVISOR = Component.translatable("gui.spudaciousshops.supervisor");
    private static final Component CLERK = Component.translatable("gui.spudaciousshops.clerk");
    private static final Component WARN_TITLE = Component.translatable("gui.spudaciousshops.delete_warn_title");
    private static final Component WARN_LINE_1 = Component.translatable("gui.spudaciousshops.delete_warn_message_line1");
    private static final Component WARN_LINE_2 = Component.translatable("gui.spudaciousshops.delete_warn_message_line2");
    private static final Component CANCEL = Component.translatable("gui.spudaciousshops.cancel");
    private static final Component DELETE = Component.translatable("gui.spudaciousshops.delete");

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float partialTick) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, partialTick);
        Font font = Minecraft.getInstance().font;


        int activeTab = this.menu.getActiveTab();
        
        if(activeTab == SETTINGS_TAB) {

                for(ToolTipText ttt : TEXTS){
                    ttt.render(context,font,mouseX,mouseY);
                }
            
        }else if(activeTab == WARNING_TAB){

                for(Warn_popup_texts t : WARN_TEXTS){
                    t.render(context,font);
                }
            
        }else if(activeTab == SELLER_TAB){

                for(Warn_popup_texts t : STORAGE_TEXTS){
                    t.render(context,font);
                }
            
        }
        this.renderTooltip(context, mouseX, mouseY);
    }

    private final String PERMISSIONS = Component.translatable("gui.spudaciousshops.text_permissions").getString();
    private final String IMPORT_ITEMS = Component.translatable("gui.spudaciousshops.text_import_items").getString();
    private final String TAKE_ITEMS = Component.translatable("gui.spudaciousshops.text_take_items").getString();
    private final String EDIT_PERMS = Component.translatable("gui.spudaciousshops.text_edit_perms").getString();
    private final String CHANGE_TRADE = Component.translatable("gui.spudaciousshops.text_change_trade").getString();
    private final String BREAK_SHOP = Component.translatable("gui.spudaciousshops.text_break_shop").getString();
    private final String YES = Component.translatable("gui.spudaciousshops.text_yes").getString();
    private final String NO = Component.translatable("gui.spudaciousshops.text_no").getString();
    private final String ALL = Component.translatable("gui.spudaciousshops.text_all").getString();
    private final String SUPERVISOR_AND_LOWER = Component.translatable("gui.spudaciousshops.text_supervisor_and_lower").getString();
    private final String NONE = Component.translatable("gui.spudaciousshops.text_none").getString();
    private final String CREATIVE_TOGGLE_TOOLTIP = Component.translatable("gui.spudaciousshops.toggle_creative").getString();
    private final String EFFECTS_TOGGLE_TOOLTIP = Component.translatable("gui.spudaciousshops.toggle_effects").getString();



    private ToolTipText[] addToolTipTexts(){
        int textX = 14+leftPos;
        int textY = 72+topPos;
        @MagicConstant
        int increment = 23;
        int colour = this.menu.SCREEN_SETTINGS.SETTINGS_TEXT_COLOUR();//11141290;

        ToolTipText[] texts = new ToolTipText[4];
        MutableComponent permissions_title = Component.literal("§l" + PERMISSIONS + ":");
        texts[0] = new ToolTipText(colour,OWNER, textX, textY,
                List.of(
                        permissions_title,
                        Component.literal("§a + "+IMPORT_ITEMS+": "+YES),
                        Component.literal("§a + "+TAKE_ITEMS+": "+YES),
                        Component.literal("§a + "+EDIT_PERMS+": "+ALL),
                        Component.literal("§a + "+CHANGE_TRADE+": "+YES),
                        Component.literal("§a + "+BREAK_SHOP+": "+YES)
        ));
        textY += increment;
        texts[1] = new ToolTipText(colour,MANAGER, textX, textY,
                List.of(
                        permissions_title,
                        Component.literal("§a + "+IMPORT_ITEMS+": "+YES),
                        Component.literal("§a + "+TAKE_ITEMS+": "+YES),
                        Component.literal("§9 + "+EDIT_PERMS+": "+SUPERVISOR_AND_LOWER),
                        Component.literal("§c - "+CHANGE_TRADE+": "+NO),
                        Component.literal("§c - "+BREAK_SHOP+": "+NO)
                ));
        textY += increment;
        texts[2] = new ToolTipText(colour,SUPERVISOR, textX, textY,
                List.of(
                        permissions_title,
                        Component.literal("§a + "+IMPORT_ITEMS+": "+YES),
                        Component.literal("§a + "+TAKE_ITEMS+": "+YES),
                        Component.literal("§c - "+EDIT_PERMS+": "+NONE),
                        Component.literal("§c - "+CHANGE_TRADE+": "+NO),
                        Component.literal("§c - "+BREAK_SHOP+": "+NO)
                ));
        textY += increment;
        texts[3] = new ToolTipText(colour,CLERK, textX, textY,
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

    private Warn_popup_texts[] addWarnPopupTexts(){
        int textX = leftPos+110;
        int textY = topPos+84;
        Warn_popup_texts[] warn_texts = new Warn_popup_texts[3];
        warn_texts[0] = new Warn_popup_texts(textX,textY,WARN_TITLE,14745600, true);
        textY += 20;
        warn_texts[1] = new Warn_popup_texts(textX,textY,WARN_LINE_1,986895, false);
        textY += 10;
        warn_texts[2] = new Warn_popup_texts(textX,textY,WARN_LINE_2,986895, false);
        return warn_texts;
    }

    private Warn_popup_texts[] addStorageTexts(){
        Warn_popup_texts[] storage_texts = new Warn_popup_texts[4];
        storage_texts[0] = new Warn_popup_texts(leftPos+90,topPos+5, Component.literal("Stock"),2434341, false);

        storage_texts[1] = new Warn_popup_texts(leftPos+35,topPos+113, Component.literal("Register"),2434341, false);//8282679

        storage_texts[2] = new Warn_popup_texts(leftPos+33,topPos+18, Component.literal("Payment"),2434341, false);

        storage_texts[3] = new Warn_popup_texts(leftPos+33,topPos+61,  Component.literal("Product"),2434341, false);
        return storage_texts;
    }

    private ToolTipText[] TEXTS = addToolTipTexts();

    private class ToolTipText{
        private final Component TEXT;
        private final int X;
        private final int Y;
        private final int Xmax;
        private final int Ymax;
        private final List<Component> TOOLTIP;
        private final int COLOUR;


        private ToolTipText(int colour, Component text, int x, int y, List<Component> tooltip) {
            TEXT = text;
            X = x;
            Y = y;
            int tXmax = 0;
            int tYmax = 0;
            try {
                tXmax = X + font.width(TEXT);
                tYmax = Y + font.lineHeight;
            } catch (Exception e) {
            }
            if(tXmax!=0&&tYmax!=0){

            } else {
                tXmax = X+25;
                tYmax = Y+8;
            }
            Xmax = tXmax;
            Ymax = tYmax;
            TOOLTIP = tooltip;
            COLOUR = colour;
        }

        public void render(GuiGraphics context, Font textRenderer, int mouseX, int mouseY){
            context.drawString(textRenderer, TEXT, X, Y, COLOUR, false);
            if(mouseX >= X && mouseX<=Xmax){
                if(mouseY >= Y && mouseY<=Ymax){
                    context.renderTooltip(textRenderer,TOOLTIP, java.util.Optional.empty(),mouseX,mouseY);
                }
            }
        }
    }


    interface ClickEventHandler {
        void execute();
    }

    private static class TabWidget extends AbstractWidget {

        private final ClickEventHandler thisTab;

        private final ResourceLocation ICON_TEXTURE;

        private boolean toggle;

        public TabWidget(int x, int y, Component message, ClickEventHandler tab, boolean visible, ResourceLocation texture) {
            this(x,y, message,tab,visible,texture,false);
        }

        public TabWidget(int x, int y, Component message, ClickEventHandler tab, boolean visible, ResourceLocation texture, boolean toggle) {
            super(x, y, 22, 22, message);
            this.thisTab = tab;
            this.visible = visible;
            this.toggle = toggle;
            this.ICON_TEXTURE = texture;
        }

        @Override
        protected void renderWidget(GuiGraphics context, int pMouseX, int pMouseY, float pPartialTick) {
            int x = this.getX()-3;
            int y = this.getY()-6;
            if(toggle){
                context.blit(TAB_SELECTED,x,y,32,32,0f,0f,32,32,32,32);
            }else if(isHovered){
                context.blit(TAB_HOVER,x,y,32,32,0f,0f,32,32,32,32);
            }else{
                context.blit(TAB_DESELECTED,x,y,32,32,0f,0f,32,32,32,32);
            }
            context.blit(ICON_TEXTURE,x+6,y+9,16,16,0f,0f,16,16,16,16);
        }

        public void onClick(double mouseX, double mouseY) {
            thisTab.execute();
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {}

        void unToggle(){
            this.toggle = false;
        }

        void toggle(){
            this.toggle = true;
        }

    }

    private class ToggleWidget extends AbstractWidget{

        private final ResourceLocation TEXTURE_ON;
        private final ResourceLocation TEXTURE_OFF;

        private final ToggleButtonID BUTTON_ID;
        private final Component tooltip;

        private boolean toggle;

        public ToggleWidget(int x, int y, ToggleButtonID buttonID, ResourceLocation textureON, ResourceLocation textureOFF, String tooltipText) {
            super(x, y, 32, 16, Component.literal(""));
            this.BUTTON_ID = buttonID;
            this.visible = false;
            this.toggle = menu.getStateOfSetting(BUTTON_ID);
            this.TEXTURE_ON = textureON;
            this.TEXTURE_OFF = textureOFF;
            this.tooltip = Component.literal(tooltipText);
        }

        @Override
        protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float pPartialTick) {
            int x = this.getX();
            int y = this.getY();

            context.blit(SETTINGS.BUTTON_BACKGROUND(),x-3,y-3,64,64,0f,0f,64,64,64,64);
            context.blit(toggle ? TEXTURE_ON : TEXTURE_OFF ,x,y,32,32,0f,0f,32,32,32,32);

            if(isHovered){
                context.renderTooltip(Minecraft.getInstance().font, tooltip,mouseX,mouseY);
            }
        }


        public void onClick(double mouseX, double mouseY) {
            toggle = menu.handleToggleButtonInput(BUTTON_ID, !toggle);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

        }

        void toggleOff(){
            this.toggle = false;
        }

        void toggleOn(){
            this.toggle = true;
        }
    }
    protected void updateToggleButtonFromPacket(ToggleButtonID button, boolean state) {
        toggleButtons.get(button).toggle = state;
    }
    protected void setWidgetsVisible(boolean state){
        //TODO implement the other features
        //toggleButtons.values().forEach((w)-> {if(w != null){w.visible=state;}});

        ToggleWidget w = toggleButtons.get(ToggleButtonID.EffectsToggle);
        if(w != null){
            w.visible=state;
        }

        w = toggleButtons.get(ToggleButtonID.CreativeToggle);
        if(w != null){
            w.visible=state&&this.menu.isPlayerCreative();
        }

    }


    private class ButtonWidget extends AbstractWidget{

        private final ClickEventHandler FUNCTION;
        private final ResourceLocation TEXTURE;
        private final ResourceLocation TEXTURE_HOVERED;
        private final Warn_popup_texts TEXT;

        public ButtonWidget(int x, int y, Component message, ClickEventHandler function, ResourceLocation texture, ResourceLocation textureHovered, Component text, int colour) {
            super(x, y, 64, 28, message);
            this.FUNCTION = function;
            this.visible = false;
            this.TEXTURE = texture;
            this.TEXTURE_HOVERED = textureHovered;
            this.TEXT = new Warn_popup_texts(x+32,y+13,text,colour, false);
        }

        @Override
        protected void renderWidget(GuiGraphics context, int pMouseX, int pMouseY, float pPartialTick) {
            int x = this.getX();
            int y = this.getY()-16;
            if(isHovered){
                context.blit(TEXTURE_HOVERED,x,y,64,64,0f,0f,64,64,64,64);
                renderText(context,true);
            }else{
                context.blit(TEXTURE,x,y,64,64,0f,0f,64,64,64,64);
                renderText(context,false);
            }
        }

        private void renderText(GuiGraphics context, boolean offset){
            Font f = Minecraft.getInstance().font;
                if(offset){
                    TEXT.renderOffset(context,f);
                }else{
                    TEXT.render(context,f);
                }

        }


        @Override
        public void onClick(double mouseX, double mouseY) {
            FUNCTION.execute();
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

        }

    }

    private Warn_popup_texts[] WARN_TEXTS = addWarnPopupTexts();

    private Warn_popup_texts[] STORAGE_TEXTS = addStorageTexts();

    private record Warn_popup_texts(int x, int y, Component text, int colour, boolean shadow){

        void render(GuiGraphics context, Font f){
            context.drawString(f, text, x - f.width(text) / 2, y, colour, shadow);
        }
        void renderOffset(GuiGraphics context, Font textRenderer) {
            context.drawString(textRenderer, text, 1+x - textRenderer.width(text) / 2, y, colour, shadow);
        }
    }
}
