package net.spudacious5705.testinggrounds.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.spudacious5705.testinggrounds.TestingGrounds;

public class ShopScreen extends HandledScreen<ShopScreenHandler> {
    private static final Identifier TEXTURE_SELLER = new Identifier(TestingGrounds.MOD_ID, "textures/gui/shop_seller.png");
    private static final Identifier TEXTURE_CUSTOMER = new Identifier(TestingGrounds.MOD_ID, "textures/gui/shop_customer.png");
    private Identifier TEXTURE;
    private int bWidth;
    private int bHeight;
    private int x;
    private int y;
    private boolean screenTypeUnselected = true;

    public ShopScreen(ShopScreenHandler handler1, PlayerInventory inventory, Text title) {
        super(handler1, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        playerInventoryTitleX = 1000;
        titleX = 1000;

    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (screenTypeUnselected) {
            if (/*handler.isScreenTypeSeller()*/true) {drawSellerBackground();} else {drawCustomerBackground();}
            screenTypeUnselected = false;
        }
        RenderSystem.setShaderTexture(0, TEXTURE);
        context.drawTexture(TEXTURE, x , y, 0, 0, bWidth, bHeight);
    }

    private void drawSellerBackground() {
        TEXTURE = TEXTURE_SELLER;
        bWidth = 228;
        bHeight = 253;
        x = (width - bWidth)/2;
        y = (height - bHeight)/2;

        handler.addShopInventory();
        handler.addPlayerInventory(true);
    }

    private void drawCustomerBackground() {
        TEXTURE = TEXTURE_CUSTOMER;

        x = (width - backgroundWidth)/2;
        y = (height - backgroundHeight)/2;

        bWidth = backgroundWidth;
        bHeight = backgroundHeight;

        handler.addCustomerInventory();
        handler.addPlayerInventory(false);
    }



    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
