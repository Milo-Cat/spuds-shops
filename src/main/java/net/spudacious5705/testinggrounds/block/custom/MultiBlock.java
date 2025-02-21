package net.spudacious5705.testinggrounds.block.custom;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.spudacious5705.testinggrounds.block.entity.MultiBlockEntity;
import org.jetbrains.annotations.Nullable;

public class MultiBlock extends BlockWithEntity implements BlockEntityProvider {

    //Public static IntProperty p = Properties.

    public MultiBlock(Settings settings) {
        super(settings);
    }


    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MultiBlockEntity(pos, state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

    }
}
