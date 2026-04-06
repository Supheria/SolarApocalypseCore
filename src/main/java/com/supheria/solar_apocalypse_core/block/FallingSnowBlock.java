package com.supheria.solar_apocalypse_core.block;

import com.supheria.solar_apocalypse_core.transforms.fluid.SnowMelt;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;

public class FallingSnowBlock extends FallingBlock {
    public static final IntegerProperty MASS = IntegerProperty.create("mass", 1, 8);

    public FallingSnowBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).sound(SoundType.SNOW).strength(0.2f).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(MASS, 1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(MASS);
    }

    @Override
    public void onLand(Level level, BlockPos pos, BlockState fallingState, BlockState replacedState, net.minecraft.world.entity.item.FallingBlockEntity fallingBlock) {
        if (level instanceof ServerLevel serverLevel) {
            SnowMelt.landFallingSnow(serverLevel, pos, fallingState, replacedState);
            return;
        }
        super.onLand(level, pos, fallingState, replacedState, fallingBlock);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
        SnowMelt.triggerVisibleFall(level, pos, state);
    }
}
