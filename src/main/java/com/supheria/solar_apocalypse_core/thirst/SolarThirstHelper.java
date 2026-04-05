package com.supheria.solar_apocalypse_core.thirst;

import com.supheria.solar_apocalypse_core.config.solar.StageHeightConfig;
import com.supheria.solar_apocalypse_core.network.SapModVariables;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;

public class SolarThirstHelper {

    public static float getExhaustionMultiplier(Player player) {
        if (player == null || player.isCreative() || player.isSpectator()) {
            return 1.0f;
        }

        LevelAccessor world = player.level();
        if (world.isClientSide()) {
            return 1.0f;
        }

        SolarStage stage = SapModVariables.MapVariables.get(world).getCurrentStage();
        if (!isDehydrationActive(stage, player.getY())) {
            return 1.0f;
        }

        return switch (stage) {
            case STAGE_2 -> 3.0f;
            case STAGE_3 -> 5.0f;
            case STAGE_4 -> 7.0f;
            case STAGE_5 -> 9.0f;
            default -> 1.0f;
        };
    }

    public static boolean isDehydrationActive(LevelAccessor world, double y) {
        return isDehydrationActive(SapModVariables.MapVariables.get(world).getCurrentStage(), y);
    }

    public static boolean isDehydrationActive(SolarStage stage, double y) {
        return isDehydrationStage(stage) && y >= StageHeightConfig.getCozyHeight(stage);
    }

    public static boolean isDehydrationStage(SolarStage stage) {
        return stage != null && stage.isAtLeast(SolarStage.STAGE_2) && stage.isBefore(SolarStage.STAGE_6);
    }
}
