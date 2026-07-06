package net.spudacious5705.shops.util;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.spudacious5705.shops.block.entity.AbstractShopEntity;
import net.spudacious5705.shops.screen.ShopOpenData;

public final class ShopMenuHelper {
    private ShopMenuHelper() {}

    public static void openShopMenu(ServerPlayer player, AbstractShopEntity shop, BlockPos pos, boolean openTop) {
        MenuProvider provider = shop.createScreenHandlerFactory(openTop);
        player.openMenu(new ExtendedScreenHandlerFactory<ShopOpenData>() {
            @Override
            public ShopOpenData getScreenOpeningData(ServerPlayer serverPlayer) {
                return new ShopOpenData(pos, openTop);
            }

            @Override
            public Component getDisplayName() {
                return provider.getDisplayName();
            }

            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player p) {
                return provider.createMenu(syncId, inventory, p);
            }
        });
    }
}
