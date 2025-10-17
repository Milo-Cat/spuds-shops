package net.spudacious5705.shops.screen;


import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;


public class ShopScreenCustomer extends AbstractContainerScreen<ShopScreenHandlerCustomer> {

    protected int textureX = 0;
    protected int textureY = 0;

    private final ResourceLocation TEXTURE;

    public ShopScreenCustomer(ShopScreenHandlerCustomer handler, Inventory playerInventory, Component title) {
        super(handler, playerInventory, title);
        TEXTURE = handler.getSettings().CUSTOMER().textureID();
        imageWidth = 256;
        imageHeight = 256;
    }
    @Override
    protected void init() {
        super.init();

        leftPos = (width - imageWidth)/2+42;
        topPos = (height - imageHeight)/2+50;

        textureX = leftPos-25;
        textureY = topPos-90;
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Do nothing — this prevents the title and inventory label from rendering
        //TODO perhaps implement this in fabric
    }



    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, textureX, textureY, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
