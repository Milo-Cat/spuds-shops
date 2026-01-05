package net.spudacious5705.shops.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.spudacious5705.shops.SpudaciousShops;
import org.intellij.lang.annotations.MagicConstant;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

import static net.spudacious5705.shops.screen.ModScreenHandlers.CURRENCY_IMG_MAP;
import static net.spudacious5705.shops.screen.ShopScreenHandlerOwner.*;
import static net.spudacious5705.shops.screen.networking.NetworkHelper.SHOP_SELF_DEMOTE;

public class ShopScreenOwner extends HandledScreen<ShopScreenHandlerOwner> {

    private static void playClickSound(){
        assert MinecraftClient.getInstance().player != null;
        MinecraftClient.getInstance().player.playSound(
                SoundEvents.UI_BUTTON_CLICK.value(),
                1.0F,
                1.0F
        );
    }
    
    private final ScreenSettingsGroup SETTINGS;

    private static final Identifier RED_BUTTON = SpudaciousShops.id("textures/gui/red_button.png");
    private static final Identifier RED_BUTTON_SELECTED = SpudaciousShops.id("textures/gui/red_button_selected.png");
    private static final Identifier GREEN_BUTTON = SpudaciousShops.id("textures/gui/green_button.png");
    private static final Identifier GREEN_BUTTON_SELECTED = SpudaciousShops.id("textures/gui/green_button_selected.png");


    public ShopScreenOwner(ShopScreenHandlerOwner handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 228;
        this.backgroundHeight = 256;
        this.SETTINGS = handler.getSettings();
        this.x = (width - backgroundWidth)/2;
        this.y = (height - backgroundHeight)/2;

        handler.initiateWarn(this::openWarnPopup);
        handler.settingsUpdater(this::updateToggleButtonFromPacket);
        handler.setWidgetFunction(this::setWidgetsVisible);
    }

    private void closeWarnPopup(){
        setStateAllButtons(true);
        switchToSettingsTab();
    }

    private void WarnPopupContinue(){
        PacketByteBuf buf = PacketByteBufs.create();
        ClientPlayNetworking.send(SHOP_SELF_DEMOTE,buf);

    }

    private void setStateAllButtons(boolean state){
        SettingsTabButton.visible=state;
        SellerTabButton.visible=state;
        ShopFrontTabButton.visible=state;
        WarningCancel.visible=!state;
        WarningProceed.visible=!state;
        for (ToggleButtonID value : ToggleButtonID.values()) {
            toggleButtons.get(value).visible=state;
        }
    }

    private static final Identifier COG_ICON = SpudaciousShops.id("textures/gui/settings.png");
    private static final Identifier STORAGE_ICON = SpudaciousShops.id("textures/gui/storage.png");
    private static final Identifier SHOPFRONT_ICON = CURRENCY_IMG_MAP.getOrDefault(
            Text.translatable("gui.spudaciousshops.currency_type").getString().charAt(0),
            SpudaciousShops.id("textures/gui/currency_textures/gbp.png")
    );
    private static final Identifier TAB_SELECTED = SpudaciousShops.id("textures/gui/tab_selected.png");
    private static final Identifier TAB_DESELECTED = SpudaciousShops.id("textures/gui/tab_deselected.png");
    private static final Identifier TAB_HOVER = SpudaciousShops.id("textures/gui/tab_hover.png");
    private static final Identifier CREATIVE_ON = SpudaciousShops.id("textures/gui/creative_on.png");
    private static final Identifier CREATIVE_OFF = SpudaciousShops.id("textures/gui/creative_off.png");
    private static final Identifier EFFECTS_ON = SpudaciousShops.id("textures/gui/effects_on.png");
    private static final Identifier EFFECTS_OFF = SpudaciousShops.id("textures/gui/effects_off.png");

    @Override
    protected void init() {
        super.init();
        playerInventoryTitleX = 1000;
        titleX = 1000;

        int posX = SETTINGS.tab1ButtonX()+x;
        int posY = SETTINGS.tab1ButtonY()+y;
        SellerTabButton = addDrawableChild(new TabWidget(posX, posY, Text.of(""), this::switchToSellerTab, true, STORAGE_ICON, true));


        posX = SETTINGS.tab2ButtonX()+x;
        posY = SETTINGS.tab2ButtonY()+y;
        SettingsTabButton = addDrawableChild(new TabWidget(posX, posY, Text.of(""), this::switchToSettingsTab, true, COG_ICON));


        posX = SETTINGS.tab3ButtonX()+x;
        posY = SETTINGS.tab3ButtonY()+y;
        ShopFrontTabButton = addDrawableChild(new TabWidget(posX, posY, Text.of(""), this::switchToCustomerTab, true, SHOPFRONT_ICON));


        posX = 22+x;
        posY = 128+y;
        WarningCancel = addDrawableChild(new ButtonWidget(posX, posY, Text.of("CANCEL"), this::closeWarnPopup, GREEN_BUTTON, GREEN_BUTTON_SELECTED, CANCEL, 3840));

        posX += 113;
        WarningProceed = addDrawableChild(new ButtonWidget(posX, posY, Text.of("CONTINUE"), this::WarnPopupContinue, RED_BUTTON, RED_BUTTON_SELECTED, DELETE, 984329));

        posX = SETTINGS.creativeButtonX()+x;
        posY = SETTINGS.creativeButtonY()+y;
        ToggleCreative = new ToggleWidget(posX, posY, ToggleButtonID.CreativeToggle, CREATIVE_ON, CREATIVE_OFF, CREATIVE_TOGGLE_TOOLTIP);
        posX = SETTINGS.toggleEffectsButtonX()+x;
        posY = SETTINGS.toggleEffectsButtonY()+y;
        ToggleIconsEffects = new ToggleWidget(posX, posY, ToggleButtonID.EffectsToggle, EFFECTS_ON, EFFECTS_OFF, EFFECTS_TOGGLE_TOOLTIP);
        posX = SETTINGS.shopStyleButtonX()+x;
        posY = SETTINGS.shopStyleButtonY()+y;
        ToggleShopStyle = addDrawableChild(new ToggleWidget(posX, posY, ToggleButtonID.ShopStyleToggle, SHOPFRONT_ICON, EFFECTS_OFF, "foo"));
        posX = SETTINGS.ignoreNBTButtonX()+x;
        posY = SETTINGS.ignoreNBTButtonY()+y;
        ToggleIgnoreNBT = addDrawableChild(new ToggleWidget(posX, posY, ToggleButtonID.IgnoreNBTToggle, SHOPFRONT_ICON, EFFECTS_OFF, "foo"));

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

        addToolTipTexts();
        addWarnPopupTexts();
        addStorageTexts();
    }

    private void switchToCustomerTab() {
        handler.updateTabSelectionClientside(CUSTOMER_TAB);
        customerGUI();
    }
    private void customerGUI(){
        SellerTabButton.unToggle();
        SettingsTabButton.unToggle();
    }

    private void switchToSellerTab(){
        handler.updateTabSelectionClientside(SELLER_TAB);
        sellerGUI();
    }

    private void sellerGUI(){
        ShopFrontTabButton.unToggle();
        SettingsTabButton.unToggle();
    }

    private void switchToSettingsTab() {
        handler.updateTabSelectionClientside(SETTINGS_TAB);
        settingsGUI();
    }
    private void settingsGUI(){
        ShopFrontTabButton.unToggle();
        SellerTabButton.unToggle();
    }

    void openWarnPopup(){
        handler.updateTabSelectionClientside(WARNING_TAB);
        warnGUI();
    }
    private void warnGUI(){
        setStateAllButtons(false);
    }

    @Environment(EnvType.CLIENT)
    public void updateTabSelectionResponse(int tab) {
        if(handler.updateTabSelectionResponse(tab)) {
            switch (tab) {
                case WARNING_TAB -> warnGUI();
                case CUSTOMER_TAB -> customerGUI();
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
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        Identifier TEXTURE = handler.getBackgroundTexture();
        RenderSystem.setShaderTexture(0, TEXTURE);
        context.drawTexture(TEXTURE, x , y, 0, 0, backgroundWidth, backgroundHeight);
    }

    private static final MutableText OWNER = Text.translatable("gui.spudaciousshops.owner");
    private static final MutableText MANAGER = Text.translatable("gui.spudaciousshops.manager");
    private static final MutableText SUPERVISOR = Text.translatable("gui.spudaciousshops.supervisor");
    private static final MutableText CLERK = Text.translatable("gui.spudaciousshops.clerk");
    private static final MutableText WARN_TITLE = Text.translatable("gui.spudaciousshops.delete_warn_title");
    private static final MutableText WARN_LINE_1 = Text.translatable("gui.spudaciousshops.delete_warn_message_line1");
    private static final MutableText WARN_LINE_2 = Text.translatable("gui.spudaciousshops.delete_warn_message_line2");
    private static final MutableText CANCEL = Text.translatable("gui.spudaciousshops.cancel");
    private static final MutableText DELETE = Text.translatable("gui.spudaciousshops.delete");

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);

        if(client == null) return;

        TextRenderer textRenderer = client.textRenderer;

        switch (handler.getActiveTab()){
            case SELLER_TAB -> {
                for(Warn_popup_texts t : STORAGE_TEXTS){
                    t.render(context,textRenderer);
                }
            }
            case SETTINGS_TAB -> {
                ToggleCreative.render(context,mouseX,mouseY,delta);

                for(ToolTipText ttt : TEXTS){
                    ttt.render(context,textRenderer,mouseX,mouseY);
                }

            }
            case CUSTOMER_TAB -> {

            }
            case WARNING_TAB -> {

                for(Warn_popup_texts t : WARN_TEXTS){
                    t.render(context,textRenderer);
                }

            }
            default -> throw new IllegalStateException("Unexpected value: " + handler.getActiveTab());
        }

        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    private final String PERMISSIONS = Text.translatable("gui.spudaciousshops.text_permissions").getString();
    private final String IMPORT_ITEMS = Text.translatable("gui.spudaciousshops.text_import_items").getString();
    private final String TAKE_ITEMS = Text.translatable("gui.spudaciousshops.text_take_items").getString();
    private final String EDIT_PERMS = Text.translatable("gui.spudaciousshops.text_edit_perms").getString();
    private final String CHANGE_TRADE = Text.translatable("gui.spudaciousshops.text_change_trade").getString();
    private final String BREAK_SHOP = Text.translatable("gui.spudaciousshops.text_break_shop").getString();
    private final String YES = Text.translatable("gui.spudaciousshops.text_yes").getString();
    private final String NO = Text.translatable("gui.spudaciousshops.text_no").getString();
    private final String ALL = Text.translatable("gui.spudaciousshops.text_all").getString();
    private final String SUPERVISOR_AND_LOWER = Text.translatable("gui.spudaciousshops.text_supervisor_and_lower").getString();
    private final String NONE = Text.translatable("gui.spudaciousshops.text_none").getString();
    private final String CREATIVE_TOGGLE_TOOLTIP = Text.translatable("gui.spudaciousshops.toggle_creative").getString();
    private final String EFFECTS_TOGGLE_TOOLTIP = Text.translatable("gui.spudaciousshops.toggle_effects").getString();



    private void addToolTipTexts(){
        int textX = x+14;
        int textY = y+72;
        @MagicConstant
        int increment = 23;
        int colour = handler.SCREEN_SETTINGS.SETTINGS_TEXT_COLOUR();//11141290;

        Text permissions_title = Text.of("§l" + PERMISSIONS + ":");
        TEXTS[0] = new ToolTipText(colour,OWNER, textX, textY,
                List.of(
                        permissions_title,
                        Text.of("§a + "+IMPORT_ITEMS+": "+YES),
                        Text.of("§a + "+TAKE_ITEMS+": "+YES),
                        Text.of("§a + "+EDIT_PERMS+": "+ALL),
                        Text.of("§a + "+CHANGE_TRADE+": "+YES),
                        Text.of("§a + "+BREAK_SHOP+": "+YES)
        ));
        textY += increment;
        TEXTS[1] = new ToolTipText(colour,MANAGER, textX, textY,
                List.of(
                        permissions_title,
                        Text.of("§a + "+IMPORT_ITEMS+": "+YES),
                        Text.of("§a + "+TAKE_ITEMS+": "+YES),
                        Text.of("§9 + "+EDIT_PERMS+": "+SUPERVISOR_AND_LOWER),
                        Text.of("§c - "+CHANGE_TRADE+": "+NO),
                        Text.of("§c - "+BREAK_SHOP+": "+NO)
                ));
        textY += increment;
        TEXTS[2] = new ToolTipText(colour,SUPERVISOR, textX, textY,
                List.of(
                        permissions_title,
                        Text.of("§a + "+IMPORT_ITEMS+": "+YES),
                        Text.of("§a + "+TAKE_ITEMS+": "+YES),
                        Text.of("§c - "+EDIT_PERMS+": "+NONE),
                        Text.of("§c - "+CHANGE_TRADE+": "+NO),
                        Text.of("§c - "+BREAK_SHOP+": "+NO)
                ));
        textY += increment;
        TEXTS[3] = new ToolTipText(colour,CLERK, textX, textY,
                List.of(
                        permissions_title,
                        Text.of("§a + "+IMPORT_ITEMS+": "+YES),
                        Text.of("§c - "+TAKE_ITEMS+": "+NO),
                        Text.of("§c - "+EDIT_PERMS+": "+NONE),
                        Text.of("§c - "+CHANGE_TRADE+": "+NO),
                        Text.of("§c - "+BREAK_SHOP+": "+NO)
                ));
    }

    private void addWarnPopupTexts(){
        int textX = x+110;
        int textY = y+84;
        WARN_TEXTS[0] = new Warn_popup_texts(textX,textY,WARN_TITLE,14745600, true);
        textY += 20;
        WARN_TEXTS[1] = new Warn_popup_texts(textX,textY,WARN_LINE_1,986895, false);
        textY += 10;
        WARN_TEXTS[2] = new Warn_popup_texts(textX,textY,WARN_LINE_2,986895, false);
    }

    private void addStorageTexts(){
        STORAGE_TEXTS[0] = new Warn_popup_texts(x+90,y+5,(MutableText) Text.of("Stock"),2434341, false);

        STORAGE_TEXTS[1] = new Warn_popup_texts(x+35,y+113,(MutableText) Text.of("Register"),2434341, false);//8282679

        STORAGE_TEXTS[2] = new Warn_popup_texts(x+33,y+18,(MutableText) Text.of("Payment"),2434341, false);

        STORAGE_TEXTS[3] = new Warn_popup_texts(x+33,y+61, (MutableText) Text.of("Product"),2434341, false);
    }

    private final ToolTipText[] TEXTS = new ToolTipText[4];

    private static class ToolTipText{
        private final MutableText TEXT;
        private final int X;
        private final int Y;
        private final int Xmax;
        private final int Ymax;
        private final List<Text> TOOLTIP;
        private final int COLOUR;


        private ToolTipText(int colour, MutableText text, int x, int y, List<Text> tooltip) {
            TEXT = text;
            X = x;
            Y = y;
            Xmax = X+56;
            Ymax = Y+8;
            TOOLTIP = tooltip;
            COLOUR = colour;
        }

        public void render(DrawContext context, TextRenderer textRenderer, int mouseX, int mouseY){
            context.drawText(textRenderer, TEXT, X, Y, COLOUR, false);
            if(mouseX >= X && mouseX<=Xmax){
                if(mouseY >= Y && mouseY<=Ymax){
                    context.drawTooltip(textRenderer, TOOLTIP,mouseX,mouseY);
                }
            }
        }
    }


    interface ClickEventHandler {
        void execute();
    }

    private static class TabWidget extends ClickableWidget{

        private final ClickEventHandler thisTab;

        private final Identifier ICON_TEXTURE;

        private boolean toggle;

        public TabWidget(int x, int y, Text message, ClickEventHandler tab, boolean visible, Identifier texture) {
            this(x,y, message,tab,visible,texture,false);
        }

        public TabWidget(int x, int y, Text message, ClickEventHandler tab, boolean visible, Identifier texture, boolean toggle) {
            super(x, y, 22, 22, message);
            this.thisTab = tab;
            this.visible = visible;
            this.toggle = toggle;
            this.ICON_TEXTURE = texture;
        }

        @Override
        protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            int x = this.getX()-3;
            int y = this.getY()-6;
            if(toggle){
                context.drawTexture(TAB_SELECTED,x,y,32,32,0f,0f,32,32,32,32);
            }else if(hovered){
                context.drawTexture(TAB_HOVER,x,y,32,32,0f,0f,32,32,32,32);
            }else{
                context.drawTexture(TAB_DESELECTED,x,y,32,32,0f,0f,32,32,32,32);
            }
            context.drawTexture(ICON_TEXTURE,x+6,y+9,16,16,0f,0f,16,16,16,16);
        }

        public void onClick(double mouseX, double mouseY) {
            playClickSound();
            thisTab.execute();
            toggle();
        }

        void unToggle(){
            this.toggle = false;
        }

        void toggle(){
            this.toggle = true;
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {}
    }

    private class ToggleWidget extends ClickableWidget{

        private final Identifier TEXTURE_ON;
        private final Identifier TEXTURE_OFF;

        private final ToggleButtonID BUTTON_ID;
        private final Text tooltip;

        private boolean toggle;

        public ToggleWidget(int x, int y, ToggleButtonID buttonID, Identifier textureON, Identifier textureOFF, String tooltipText) {
            super(x, y, 32, 16, Text.of(""));//TODO change width and height
            this.BUTTON_ID = buttonID;
            this.visible = false;
            this.toggle = handler.getStateOfSetting(BUTTON_ID);
            this.TEXTURE_ON = textureON;
            this.TEXTURE_OFF = textureOFF;
            this.tooltip = Text.of(tooltipText);
        }

        @Override
        protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            int x = this.getX();
            int y = this.getY();

            context.drawTexture(SETTINGS.BUTTON_BACKGROUND(),x-3,y-3,64,64,0f,0f,64,64,64,64);
            context.drawTexture(toggle ? TEXTURE_ON : TEXTURE_OFF ,x,y,32,32,0f,0f,32,32,32,32);

            if(hovered){
                context.drawTooltip(textRenderer, tooltip,mouseX,mouseY);
            }
        }

        public void onClick(double mouseX, double mouseY) {
            playClickSound();
            toggle = handler.handleToggleButtonInput(BUTTON_ID, !toggle);
        }

        void toggleOff(){
            this.toggle = false;
        }

        void toggleOn(){
            this.toggle = true;
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {}
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
            w.visible=state&&handler.isPlayerCreative();
        }

    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private class ButtonWidget extends ClickableWidget{

        private final ClickEventHandler FUNCTION;
        private final Identifier TEXTURE;
        private final Identifier TEXTURE_HOVERED;
        private final Warn_popup_texts TEXT;

        public ButtonWidget(int x, int y, Text message, ClickEventHandler function, Identifier texture, Identifier textureHovered, MutableText text, int colour) {
            super(x, y, 64, 28, message);
            this.FUNCTION = function;
            this.visible = false;
            this.TEXTURE = texture;
            this.TEXTURE_HOVERED = textureHovered;
            this.TEXT = new Warn_popup_texts(x+32,y+13,text,colour, false);
        }

        @Override
        protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            int x = this.getX();
            int y = this.getY()-16;
            if(hovered){
                context.drawTexture(TEXTURE_HOVERED,x,y,64,64,0f,0f,64,64,64,64);
                renderText(context,true);
            }else{
                context.drawTexture(TEXTURE,x,y,64,64,0f,0f,64,64,64,64);
                renderText(context,false);
            }
        }

        private void renderText(DrawContext context, boolean offset){
            if(client != null) {
                if(offset){
                    TEXT.renderOffset(context,client.textRenderer);
                }else{
                    TEXT.render(context,client.textRenderer);
                }
            }
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            FUNCTION.execute();
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {}
    }

    private final Warn_popup_texts[] WARN_TEXTS = new Warn_popup_texts[3];

    private final Warn_popup_texts[] STORAGE_TEXTS = new Warn_popup_texts[4];

    private record Warn_popup_texts(int x, int y, MutableText text, int colour, boolean shadow){

        void render(DrawContext context, TextRenderer textRenderer){
            context.drawText(textRenderer, text, x - textRenderer.getWidth(text) / 2, y, colour, shadow);
        }
        void renderOffset(DrawContext context, TextRenderer textRenderer) {
            context.drawText(textRenderer, text, 1+x - textRenderer.getWidth(text) / 2, y, colour, shadow);
        }
    }
}
