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
        
        SapModVariables.MapVariables mapVars = SapModVariables.MapVariables.get(world);
        SolarStage stage = mapVars.getCurrentStage();
        
        if (stage == SolarStage.NONE || stage == SolarStage.STAGE_6) {
            return 1.0f;
        }
        
        int cozyHeight = StageHeightConfig.getCozyHeight(stage);
        double playerY = player.getY();
        
        if (playerY >= cozyHeight) {
            return switch (stage) {
                case STAGE_2 -> 3.0f;
                case STAGE_3 -> 5.0f;
                case STAGE_4 -> 7.0f;
                case STAGE_5 -> 9.0f;
                default -> 1.0f;
            };
        }
        
        return 1.0f;
    }
}
