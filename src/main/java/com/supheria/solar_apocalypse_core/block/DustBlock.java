package com.supheria.solar_apocalypse_core.block;

import com.supheria.solar_apocalypse_core.init.SolarModBlocks;
import com.supheria.solar_apocalypse_core.transforms.dirt.DirtChain;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.Collections;

public class DustBlock extends FallingBlock {
    public DustBlock() {
        super(BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.SNARE).mapColor(MapColor.SAND).sound(SoundType.SAND)
                .strength(0.4f, 0.4f).speedFactor(0.8f).jumpFactor(0.9f).randomTicks());
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 15;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 5;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> dropsOriginal = super.getDrops(state, builder);
        if (!dropsOriginal.isEmpty())
            return dropsOriginal;
        return Collections.singletonList(new ItemStack(SolarModBlocks.DUST.get()));
    }

    @Override
    public void randomTick(BlockState blockstate, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(blockstate, level, pos, random);
        Player entity = Minecraft.getInstance().player;
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        DirtChain.DUST.call(level, x, y, z);
    }
}
