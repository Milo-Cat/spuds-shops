package net.spudacious5705.shops.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.spudacious5705.shops.SpudaciousShops;

public class ShopScreenCustomer extends HandledScreen<ShopScreenHandlerCustomer> {

    private final Identifier TEXTURE;

    public ShopScreenCustomer(ShopScreenHandlerCustomer handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        TEXTURE = handler.texture();
    }

    private int textureX = 0;
    private int textureY = 0;

    @Override
    protected void init() {
        playerInventoryTitleX = 1000;
        titleX = 1000;
        this.backgroundWidth = 256;
        this.backgroundHeight = 256;
        x = (width - backgroundWidth)/2+45;
        y = (height - backgroundHeight)/2+50;
        textureX = x-25;
        textureY = y-90;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        RenderSystem.setShaderTexture(0, TEXTURE);
        context.drawTexture(TEXTURE, textureX , textureY, 0, 0, backgroundWidth, backgroundHeight);
    }



    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context,mouseX,mouseY,delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
