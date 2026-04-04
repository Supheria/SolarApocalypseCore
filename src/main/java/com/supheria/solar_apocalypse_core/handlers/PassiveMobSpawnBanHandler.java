package com.supheria.solar_apocalypse_core.handlers;

import com.supheria.solar_apocalypse_core.network.SapModVariables;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 在太阳第三阶段之后，禁止所有中立生物（动物、鱼类）在任何世界生成
 * 模拟生物大灭绝效果
 * 
 * 通过在实体加入世界后立即移除被动生物来实现
 */
@Mod.EventBusSubscriber
public class PassiveMobSpawnBanHandler {

	@SubscribeEvent
	public static void onEntityJoin(EntityJoinLevelEvent event) {
		Entity entity = event.getEntity();
		if (entity == null) {
			return;
		}

		if (event.getLevel() instanceof ServerLevel serverLevel) {
			SolarStage stage = SapModVariables.MapVariables.get(serverLevel).getCurrentStage();
			
			if (stage == SolarStage.NONE || stage == SolarStage.STAGE_1 || stage == SolarStage.STAGE_2) {
				return;
			}

			MobCategory category = entity.getType().getCategory();
			
			if (category == MobCategory.CREATURE || 
				category == MobCategory.WATER_AMBIENT || 
				category == MobCategory.WATER_CREATURE) {
				entity.remove(Entity.RemovalReason.DISCARDED);
				event.setCanceled(true);
			}
		}
	}
}
