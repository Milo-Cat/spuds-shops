package net.spudacious5705.shops.permission;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Defines PermissionManager functions that need to be exposed
 * by the containing block.
 **/
public interface IBlockPermissions<T extends BlockEntity> {
    Component cantBreakMessage();

    PermissionManager<T>.player_ID_Records_Delegate getRecordsDelegate(Player player);

    PermissionLevel userSignIn(Player player);


}
