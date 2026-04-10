package com.supheria.solar_apocalypse_core.integration.weather2;

import com.supheria.solar_apocalypse_core.network.SolarModVariables;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import weather2.ServerTickHandler;
import weather2.weathersystem.WeatherManagerServer;
import weather2.weathersystem.storm.StormObject;
import weather2.weathersystem.storm.WeatherObject;
import weather2.weathersystem.storm.WeatherObjectParticleStorm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Weather2StageStormHandler {
    private static final Map<ResourceKey<Level>, SolarStage> LAST_STAGE = new ConcurrentHashMap<>();
    private static final int STORM_MAINTENANCE_INTERVAL_TICKS = 20;
    private static final double MAX_STORM_PLAYER_DISTANCE = 256.0;
    private static final double MAX_STORM_PLAYER_DISTANCE_SQR = MAX_STORM_PLAYER_DISTANCE * MAX_STORM_PLAYER_DISTANCE;

    @SubscribeEvent
    public void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.level.isClientSide() || !(event.level instanceof ServerLevel level)) {
            return;
        }

        if (level.dimension() != Level.OVERWORLD) {
            return;
        }

        SolarStage stage = SolarModVariables.MapVariables.get(level).getSolarStage();
        if (stage == null || !stage.isAtLeast(SolarStage.STAGE_1)) {
            return;
        }

        WeatherManagerServer manager = ServerTickHandler.getWeatherManagerFor(level);
        if (manager == null) {
            return;
        }

        List<ServerPlayer> players = level.players();
        if (players.isEmpty()) {
            return;
        }

        ResourceKey<Level> dimension = level.dimension();
        SolarStage lastStage = LAST_STAGE.put(dimension, stage);
        if (lastStage != null && lastStage != stage) {
            manager.clearAllStorms();
        }

        if (level.getGameTime() % STORM_MAINTENANCE_INTERVAL_TICKS != 0) {
            return;
        }

        ensureSingleStageStorm(manager, stage, players, level.random);
    }

    private static void ensureSingleStageStorm(WeatherManagerServer manager, SolarStage stage,
                                               List<ServerPlayer> players, RandomSource random) {
        WeatherObject retainedStorm = null;
        double retainedDistance = Double.MAX_VALUE;
        for (WeatherObject weatherObject : new ArrayList<>(manager.getStormObjects())) {
            if (!isStageStorm(weatherObject, stage)) {
                manager.removeWeatherObjectAndSync(weatherObject);
                continue;
            }
            double distanceToPlayers = minDistanceToPlayersSqr(weatherObject, players);
            if (distanceToPlayers > MAX_STORM_PLAYER_DISTANCE_SQR) {
                manager.removeWeatherObjectAndSync(weatherObject);
                continue;
            }
            if (retainedStorm == null || distanceToPlayers < retainedDistance) {
                if (retainedStorm != null) {
                    manager.removeWeatherObjectAndSync(retainedStorm);
                }
                retainedStorm = weatherObject;
                retainedDistance = distanceToPlayers;
            } else {
                manager.removeWeatherObjectAndSync(weatherObject);
            }
        }

        if (retainedStorm == null) {
            spawnStageStorm(manager, stage, players, random);
        }
    }

    private static boolean spawnStageStorm(WeatherManagerServer manager, SolarStage stage,
                                           List<ServerPlayer> players, RandomSource random) {
        ServerPlayer player = players.get(random.nextInt(players.size()));

        return switch (stage) {
            case STAGE_1 -> spawnRainstorm(manager, player, StormObject.STATE_THUNDER, false);
            case STAGE_2 -> spawnRainstorm(manager, player, StormObject.STATE_HAIL, false);
            case STAGE_3 -> spawnTornado(manager, false);
            case STAGE_4 -> spawnTornado(manager, true);
            case STAGE_5 -> spawnParticleStorm(manager, player, WeatherObjectParticleStorm.StormType.SANDSTORM);
            case STAGE_6 -> spawnParticleStorm(manager, player, WeatherObjectParticleStorm.StormType.SNOWSTORM);
            case NONE -> false;
        };
    }

    private static boolean spawnRainstorm(WeatherManagerServer manager, ServerPlayer player,
                                          int intensityStage, boolean firenado) {
        StormObject storm = new StormObject(manager);
        storm.setupStorm(player);
        storm.levelCurIntensityStage = intensityStage;
        storm.levelStormIntensityMax = intensityStage;
        storm.maxIntensityStage = intensityStage;
        storm.setPrecipitating(true);
        storm.naturallySpawned = true;

        manager.addStormObject(storm);
        manager.syncStormNew(storm);
        manager.lastStormFormed = manager.getWorld().getGameTime();
        return true;
    }

    private static boolean spawnTornado(WeatherManagerServer manager, boolean firenado) {
        StormObject storm = new StormObject(manager);
        storm.setupStorm(null);
        storm.levelCurIntensityStage = StormObject.STATE_STAGE1;
        storm.levelStormIntensityMax = StormObject.STATE_STAGE4;
        storm.maxIntensityStage = StormObject.STATE_STAGE4;
        storm.naturallySpawned = true;
        storm.canBeDeadly = true;
        storm.isFirenado = firenado;
        storm.setupTornadoAwayFromPlayersAimAtPlayers();

        manager.addStormObject(storm);
        manager.syncStormNew(storm);
        manager.lastStormFormed = manager.getWorld().getGameTime();
        return true;
    }

    private static boolean spawnParticleStorm(WeatherManagerServer manager, ServerPlayer player,
                                              WeatherObjectParticleStorm.StormType type) {
        manager.spawnParticleStorm(player.blockPosition(), type);
        long gameTime = manager.getWorld().getGameTime();
        if (type == WeatherObjectParticleStorm.StormType.SANDSTORM) {
            manager.lastSandstormFormed = gameTime;
        } else if (type == WeatherObjectParticleStorm.StormType.SNOWSTORM) {
            manager.lastSnowstormFormed = gameTime;
        }
        return true;
    }

    private static boolean isStageStorm(WeatherObject weatherObject, SolarStage stage) {
        if (weatherObject == null || weatherObject.isDead) {
            return false;
        }

        return switch (stage) {
            case STAGE_1 -> isRainstorm(weatherObject, StormObject.STATE_THUNDER, false);
            case STAGE_2 -> isRainstorm(weatherObject, StormObject.STATE_HAIL, false);
            case STAGE_3 -> isTornado(weatherObject, false);
            case STAGE_4 -> isTornado(weatherObject, true);
            case STAGE_5 -> isParticleStorm(weatherObject, WeatherObjectParticleStorm.StormType.SANDSTORM);
            case STAGE_6 -> isParticleStorm(weatherObject, WeatherObjectParticleStorm.StormType.SNOWSTORM);
            case NONE -> false;
        };
    }

    private static boolean isRainstorm(WeatherObject weatherObject, int intensityStage, boolean firenado) {
        if (!(weatherObject instanceof StormObject storm)) {
            return false;
        }
        return storm.isPrecipitating() && storm.levelCurIntensityStage == intensityStage && storm.isFirenado == firenado;
    }

    private static boolean isTornado(WeatherObject weatherObject, boolean firenado) {
        if (!(weatherObject instanceof StormObject storm)) {
            return false;
        }
        if (firenado) {
            return storm.isFirenado;
        }
        return !storm.isFirenado && (storm.isTornadoFormingOrGreater() || storm.isCycloneFormingOrGreater());
    }

    private static boolean isParticleStorm(WeatherObject weatherObject, WeatherObjectParticleStorm.StormType type) {
        return weatherObject instanceof WeatherObjectParticleStorm particleStorm && particleStorm.getType() == type;
    }

    private static double minDistanceToPlayersSqr(WeatherObject weatherObject, List<ServerPlayer> players) {
        Vec3 center = weatherObject.posGround != null ? weatherObject.posGround : weatherObject.pos;
        if (center == null) {
            return Double.MAX_VALUE;
        }

        double best = Double.MAX_VALUE;
        for (ServerPlayer player : players) {
            best = Math.min(best, player.position().distanceToSqr(center));
        }
        return best;
    }
}
