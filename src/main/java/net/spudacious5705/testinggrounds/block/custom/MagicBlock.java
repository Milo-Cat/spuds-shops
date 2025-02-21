package net.spudacious5705.testinggrounds.block.custom;

import net.minecraft.block.Block;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;

public class MagicBlock extends Block {

    public static final DirectionProperty CUSTOM = Properties.FACING;
    public MagicBlock(Settings settings) {
        super(settings);
    }


}
