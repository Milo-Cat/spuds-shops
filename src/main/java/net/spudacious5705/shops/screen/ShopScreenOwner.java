package net.spudacious5705.shops.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.spudacious5705.shops.SpudaciousShops;

public class ShopScreenOwner extends HandledScreen<ShopScreenHandlerOwner> {
    private static final Identifier TEXTURE = new Identifier(SpudaciousShops.MOD_ID, "textures/gui/shop_seller.png");


    public ShopScreenOwner(ShopScreenHandlerOwner handler1, PlayerInventory inventory, Text title) {
        super(handler1, inventory, title);
        this.backgroundWidth = 228;
        this.backgroundHeight = 254;
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
        x = (width - backgroundWidth)/2;
        y = (height - backgroundHeight)/2;
        RenderSystem.setShaderTexture(0, TEXTURE);
        context.drawTexture(TEXTURE, x , y, 0, 0, backgroundWidth, backgroundHeight);
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
