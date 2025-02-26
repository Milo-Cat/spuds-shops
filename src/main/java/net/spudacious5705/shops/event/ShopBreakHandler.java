package net.spudacious5705.shops.event;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.spudacious5705.shops.block.entity.ShopEntity;


public class ShopBreakHandler implements AttackBlockCallback {
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
        BlockEntity be = world.getBlockEntity(pos);
        if(be instanceof ShopEntity shop){
            if(!shop.canBreak(player)){
                if(world.isClient()) {
                    player.sendMessage(Text.of("Cannot break"), true);
                }
                return ActionResult.SUCCESS;}
        }
        return ActionResult.PASS;
    }
}
