package com.supheria.solar_apocalypse_core.handlers;

import com.supheria.solar_apocalypse_core.init.SapModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 碎泥土右键锄头交互处理器（原 CrushedRightClickedProcedure）。
 */
@Mod.EventBusSubscriber
public class CrushedDirtInteraction {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != event.getEntity().getUsedItemHand())
            return;
        execute(event.getLevel(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), event.getEntity());
    }

    public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null)
            return;
        if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == SapModBlocks.CRUSHED_DIRT.get()
                && ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Items.NETHERITE_HOE
                || (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Items.DIAMOND_HOE
                || (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Items.IRON_HOE
                || (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Items.GOLDEN_HOE
                || (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Items.STONE_HOE
                || (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).is(ItemTags.create(new ResourceLocation("right_click_tools_example:internal_hoe"))) == true
                || (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).is(ItemTags.create(new ResourceLocation("forge:external_hoe"))) == true)
                && !((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Items.WOODEN_HOE)) {
            if (entity instanceof LivingEntity _entity)
                _entity.swing(InteractionHand.MAIN_HAND, true);
            if (!(new Object() {
                public boolean checkGamemode(Entity _ent) {
                    if (_ent instanceof ServerPlayer _serverPlayer) {
                        return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                    } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                        return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                    }
                    return false;
                }
            }.checkGamemode(entity))) {
                {
                    ItemStack _ist = (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY);
                    if (_ist.hurt(1, RandomSource.create(), null)) {
                        _ist.shrink(1);
                        _ist.setDamageValue(0);
                    }
                }
            }
            if (world instanceof Level _level) {
                if (!_level.isClientSide()) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.hoe.till")), SoundSource.BLOCKS, 1, 1);
                } else {
                    _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.hoe.till")), SoundSource.BLOCKS, 1, 1, false);
                }
            }
            world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
            world.setBlock(BlockPos.containing(x, y, z), Blocks.COARSE_DIRT.defaultBlockState(), 3);
        } else if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == SapModBlocks.CRUSHED_DIRT.get()
                && ((entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == Items.NETHERITE_HOE
                || (entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == Items.DIAMOND_HOE
                || (entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == Items.IRON_HOE
                || (entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == Items.GOLDEN_HOE
                || (entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == Items.STONE_HOE
                || (entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).is(ItemTags.create(new ResourceLocation("right_click_tools_example:internal_hoe"))) == true
                || (entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).is(ItemTags.create(new ResourceLocation("forge:external_hoe"))) == true)
                && !((entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == Items.WOODEN_HOE)) {
            if (entity instanceof LivingEntity _entity)
                _entity.swing(InteractionHand.OFF_HAND, true);
            if (!(new Object() {
                public boolean checkGamemode(Entity _ent) {
                    if (_ent instanceof ServerPlayer _serverPlayer) {
                        return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                    } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                        return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                    }
                    return false;
                }
            }.checkGamemode(entity))) {
                {
                    ItemStack _ist = (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY);
                    if (_ist.hurt(1, RandomSource.create(), null)) {
                        _ist.shrink(1);
                        _ist.setDamageValue(0);
                    }
                }
            }
            if (world instanceof Level _level) {
                if (!_level.isClientSide()) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.hoe.till")), SoundSource.BLOCKS, 1, 1);
                } else {
                    _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.hoe.till")), SoundSource.BLOCKS, 1, 1, false);
                }
            }
            world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
            world.setBlock(BlockPos.containing(x, y, z), Blocks.COARSE_DIRT.defaultBlockState(), 3);
        }
    }
}
