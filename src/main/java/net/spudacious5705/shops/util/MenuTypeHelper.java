package net.spudacious5705.shops.util;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.spudacious5705.shops.screen.ShopOpenData;

public final class MenuTypeHelper {
    private MenuTypeHelper() {}

    @FunctionalInterface
    public interface ExtendedMenuFactory<T extends AbstractContainerMenu> {
        T create(int syncId, Inventory playerInv, ShopOpenData data);
    }

    public static <T extends AbstractContainerMenu> MenuType<T> create(ExtendedMenuFactory<T> factory) {
        return new ExtendedScreenHandlerType<>(factory::create, ShopOpenData.STREAM_CODEC);
    }
}
