package com.supheria.solar_apocalypse_core.handlers;

import com.supheria.solar_apocalypse_core.init.SapModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * 太阳坐标仪方块交互处理器（原 SunKeepProcedure）。
 */
public class SunBlockInteraction {

    public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null)
            return;
        if ((entity instanceof net.minecraft.world.entity.LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Blocks.AIR.asItem()
                && world.canSeeSkyFromBelowWater(BlockPos.containing(x, y, z))) {
            if (entity instanceof Player _player) {
                ItemStack _setstack = new ItemStack(SapModItems.COORDINATES_OF_THE_SUN.get());
                _setstack.setCount(4);
                ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
            }
            world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
        }
    }

    private SunBlockInteraction() {}
}
