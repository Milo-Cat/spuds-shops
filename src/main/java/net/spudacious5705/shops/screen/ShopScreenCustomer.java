package net.spudacious5705.shops.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.spudacious5705.shops.screen.owner_screen.ShopScreenOwner;
import org.jetbrains.annotations.NotNull;

import static net.spudacious5705.shops.screen.ScreenResources.PRODUCT_NBT_UNCHECKED_WARN;

public class ShopScreenCustomer extends AbstractContainerScreen<ShopScreenHandlerCustomer> {


    ShopScreenOwner.NotificationWidget NotifNBTMatchOFF;

    public ShopScreenCustomer(ShopScreenHandlerCustomer handler, Inventory playerInventory, Component title) {
        super(handler, playerInventory, title);
        imageWidth = 228;
        imageHeight = 256;
    }

    @Override
    protected void init() {
        super.init();

        leftPos = (width - imageWidth) / 2;
        topPos = (height - imageHeight) / 2;

        NotifNBTMatchOFF = new ShopScreenOwner.NotificationWidget(168 + leftPos, 127 + topPos, PRODUCT_NBT_UNCHECKED_WARN,
                menu::showNBToffNotif);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Do nothing — this prevents the title and inventory label from rendering
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, menu.getBackgroundTexture(), leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        NotifNBTMatchOFF.renderWidget(guiGraphics, mouseX, mouseY, partialTick, font);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
