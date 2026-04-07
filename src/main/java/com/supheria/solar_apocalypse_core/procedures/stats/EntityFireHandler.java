package com.supheria.solar_apocalypse_core.procedures.stats;

import com.supheria.solar_apocalypse_core.SolarApocalypseCoreMod;
import com.supheria.solar_apocalypse_core.config.solar.StageHeightConfig;
import com.supheria.solar_apocalypse_core.network.SolarModVariables;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 实体加入世界时的灼烧伤害处理器（原 EntityFireProcedure）。
 */
@Mod.EventBusSubscriber
public class EntityFireHandler {

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        execute(event.getEntity().level(), event.getEntity().getY(), event.getEntity());
    }

    public static void execute(LevelAccessor world, double y, Entity entity) {
        if (entity == null || entity instanceof LivingEntity || entity instanceof FallingBlockEntity || entity instanceof Player) {
            return;
        }

        SolarStage stage = SolarModVariables.MapVariables.get(world).getSolarStage();
        if (!stage.isAtLeast(SolarStage.STAGE_2) || stage.isAtLeast(SolarStage.STAGE_6)) {
            return;
        }

        if (y <= StageHeightConfig.getSafeHeight(stage)) {
            return;
        }

        if (stage == SolarStage.STAGE_2 && !world.canSeeSkyFromBelowWater(BlockPos.containing(entity.getX(), y + 1, entity.getZ()))) {
            return;
        }

        int fireSeconds = StageHeightConfig.getFireSeconds(stage);
        float fireDamage = StageHeightConfig.getFireDamage(stage);

        if (fireSeconds <= 0 || fireDamage <= 0.0f) {
            return;
        }

        if (entity instanceof Snowball) {
            entity.setSecondsOnFire(fireSeconds);
            SolarApocalypseCoreMod.queueServerWork(Math.max(1, fireSeconds) * 20, () -> entity.remove(Entity.RemovalReason.KILLED));
            return;
        }

        if (entity instanceof Projectile) {
            entity.setSecondsOnFire(fireSeconds);
            SolarApocalypseCoreMod.queueServerWork(Math.max(1, fireSeconds) * 20, () -> entity.remove(Entity.RemovalReason.KILLED));
            return;
        }

        entity.setSecondsOnFire(fireSeconds);
        entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), fireDamage);
    }
}
