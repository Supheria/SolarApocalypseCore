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

/**
 * 把太阳灾变方块转换系统接入所有 {@link BlockStateBase} 的核心 Mixin。
 *
 * <p>本类在状态缓存建立时为每个方块状态预先绑定可用的转换过程，随后分别在
 * {@code randomTick} 和 {@code onPlace} 生命周期中触发不同的规则链。
 */
@Mixin(BlockStateBase.class)
public abstract class BlockStateBaseMixin extends StateHolder<Block, BlockState> {

    @Shadow
    private boolean isRandomlyTicking;
    private boolean hasOnPlaceTransform;

    /**
     * 当前实现中该标记未在注入流程里显式回填原始值，因此默认保持 false，
     * 随后用于在执行完模组 randomTick 逻辑后取消原始流程。
     */
    @Unique
    private boolean solar$isOriginalRandomlyTicking;
    /**
     * 与 {@link #solar$isOriginalRandomlyTicking} 类似，当前实现默认保持 false，
     * 使 onPlace 注入在执行完模组规则后取消原始尾部流程。
     */
    @Unique
    private boolean solar$isOriginalOnPlace;

    /**
     * 绑定在当前方块状态上的随机刻转换过程。
     * 只要该字段不为 null，就说明这个状态需要接入灾变规则的 randomTick 触发链。
     */
    @Unique
    protected BlockTransform solar$procedure;
    /**
     * 绑定在当前方块状态上的放置时转换过程。
     * 这条链用于需要在方块落地后立即修正的规则，而不是等待随机刻触发。
     */
    @Unique
    protected BlockTransform solar$onPlaceProcedure;

    /**
     * 防止 onPlace 中再次 setBlock 造成递归回流。
     * 当前实现用线程局部标记保护同一线程内的重入触发。
     */
    @Unique
    private static final ThreadLocal<Boolean> solar$isExecutingOnPlace = ThreadLocal.withInitial(() -> false);

    protected BlockStateBaseMixin(Block p_61117_, ImmutableMap<Property<?>, Comparable<?>> p_61118_, MapCodec<BlockState> p_61119_) {
        super(p_61117_, p_61118_, p_61119_);
    }

    /**
     * 在方块状态缓存建立完成后预计算可用的灾变转换过程。
     *
     * <p>这样后续进入随机刻或放置回调时，就不需要再次遍历整套方块分类规则。
     * 同时会把原本不参与 random tick 的状态提升为可随机刻更新，以便灾变规则能够接管它们。
     */
    @Inject(method = "initCache", at = @At("TAIL"))
    private void initCacheTail(CallbackInfo callbackInfo) {
        this.solar$procedure = SolarApocalypseCoreMod.getBlockTransform(this.asState());
        this.solar$onPlaceProcedure = SolarApocalypseCoreMod.getNBlockTransform(this.asState());
        this.isRandomlyTicking = this.isRandomlyTicking || this.solar$procedure != null;
        this.hasOnPlaceTransform = this.hasOnPlaceTransform || this.solar$onPlaceProcedure != null;
    }

    /**
     * 在随机刻进入时优先执行模组绑定的方块转换。
     *
     * <p>当某个状态原本并不依赖原版 random tick 时，会在这里直接取消后续原始流程，
     * 使这次调度仅服务于太阳灾变规则。
     */
    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void randomTick(ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo callbackInfo) {
        if (this.solar$procedure != null) {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            this.solar$procedure.call(level, x, y, z);
        }

        if (!this.solar$isOriginalRandomlyTicking) {
            callbackInfo.cancel();
        }
    }
    /**
     * 在方块放置完成后立即执行需要同步修正的转换规则。
     *
     * <p>这条链主要覆盖不能等待随机刻的情形，例如放下去就必须立刻蒸发、删除或纠正的方块。
     */
    @Inject(method = "onPlace", at = @At("TAIL"), cancellable = true)
    private void onPlace(Level level, BlockPos pos, BlockState p_60699_, boolean p_60700_, CallbackInfo callbackInfo) {
        // onPlace 规则内部可能再次 setBlock；若不拦截，会重新触发 onPlace 并递归回流。
        if (this.solar$onPlaceProcedure != null && !solar$isExecutingOnPlace.get()) {
            solar$isExecutingOnPlace.set(true);
            try {
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                this.solar$onPlaceProcedure.call(level, x, y, z);
            } finally {
                solar$isExecutingOnPlace.set(false);
            }
        }

        if (!this.solar$isOriginalOnPlace) {
            callbackInfo.cancel();
        }
    }

    @Shadow
    private BlockState asState() {
        return null;
    }
}
