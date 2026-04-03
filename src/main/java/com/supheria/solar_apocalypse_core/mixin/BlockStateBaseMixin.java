package com.supheria.solar_apocalypse_core.mixin;

import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.ImmutableMap;
import com.supheria.solar_apocalypse_core.BlockTransform;
import com.supheria.solar_apocalypse_core.SolarApocalypseCoreMod;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;

@Mixin(BlockStateBase.class)
public abstract class BlockStateBaseMixin extends StateHolder<Block, BlockState> {

    @Shadow
    private boolean isRandomlyTicking;
    private boolean isonPlace;

    @Unique
    private boolean sap$isOriginalRandomlyTicking;
    @Unique
    private boolean sap$isOriginalonPlace;

    @Unique
    protected BlockTransform sap$procedure;
    @Unique
    protected BlockTransform sap$Nprocedure;

    @Unique
    private static final ThreadLocal<Boolean> sap$isExecutingOnPlace = ThreadLocal.withInitial(() -> false);

    protected BlockStateBaseMixin(Block p_61117_, ImmutableMap<Property<?>, Comparable<?>> p_61118_, MapCodec<BlockState> p_61119_) {
        super(p_61117_, p_61118_, p_61119_);
    }

    @Inject(method = "initCache", at = @At("TAIL"))
    private void initCacheTail(CallbackInfo callbackInfo) {
        this.sap$procedure = SolarApocalypseCoreMod.getBlockTransform(this.asState());
        this.sap$Nprocedure = SolarApocalypseCoreMod.getNBlockTransform(this.asState());
        this.isRandomlyTicking = this.isRandomlyTicking || this.sap$procedure != null;
        this.isonPlace = this.isonPlace || this.sap$Nprocedure != null;
    }

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void randomTick(ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo callbackInfo) {
        if (this.sap$procedure != null) {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            this.sap$procedure.call(level, x, y, z);
        }

        if (!this.sap$isOriginalRandomlyTicking) {
            callbackInfo.cancel();
        }
    }
    @Inject(method = "onPlace", at = @At("TAIL"), cancellable = true)
    private void onPlace(Level level, BlockPos pos, BlockState p_60699_, boolean p_60700_, CallbackInfo callbackInfo) {
        if (this.sap$Nprocedure != null && !sap$isExecutingOnPlace.get()) {
            sap$isExecutingOnPlace.set(true);
            try {
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                this.sap$Nprocedure.call(level, x, y, z);
            } finally {
                sap$isExecutingOnPlace.set(false);
            }
        }

        if (!this.sap$isOriginalonPlace) {
            callbackInfo.cancel();
        }
    }

    @Shadow
    private BlockState asState() {
        return null;
    }
}
