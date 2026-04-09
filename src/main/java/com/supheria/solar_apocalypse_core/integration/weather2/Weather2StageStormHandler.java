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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Weather2StageStormHandler {
    private static final Map<ResourceKey<Level>, Long> NEXT_SPAWN_TICK = new ConcurrentHashMap<>();
    private static final Map<ResourceKey<Level>, SolarStage> LAST_STAGE = new ConcurrentHashMap<>();
    private static final int STORM_NEARBY_RADIUS = 256;
    private static final long FAILED_RETRY_TICKS = 1200L;
    private static final long[] STAGE_SPAWN_INTERVALS = {
            24000L,
            18000L,
            14000L,
            10000L,
            8000L,
            8000L
    };

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
        long gameTime = level.getGameTime();
        SolarStage lastStage = LAST_STAGE.put(dimension, stage);
        if (lastStage != null && lastStage != stage) {
            manager.clearAllStorms();
            NEXT_SPAWN_TICK.remove(dimension);
        }

        if (gameTime < NEXT_SPAWN_TICK.getOrDefault(dimension, 0L)) {
            return;
        }

        boolean spawned = trySpawnStageStorm(level, manager, stage, players, level.random);
        NEXT_SPAWN_TICK.put(dimension, gameTime + (spawned ? getSpawnInterval(stage) : FAILED_RETRY_TICKS));
    }

    private static boolean trySpawnStageStorm(ServerLevel level, WeatherManagerServer manager, SolarStage stage,
                                              List<ServerPlayer> players, RandomSource random) {
        ServerPlayer player = players.get(random.nextInt(players.size()));
        Vec3 center = player.position();

        return switch (stage) {
            case STAGE_1 -> spawnRainstorm(manager, player, center, StormObject.STATE_THUNDER, false);
            case STAGE_2 -> spawnRainstorm(manager, player, center, StormObject.STATE_HAIL, false);
            case STAGE_3 -> spawnTornado(manager, center, false);
            case STAGE_4 -> spawnTornado(manager, center, true);
            case STAGE_5 -> spawnParticleStorm(manager, player, center, WeatherObjectParticleStorm.StormType.SANDSTORM);
            case STAGE_6 -> spawnParticleStorm(manager, player, center, WeatherObjectParticleStorm.StormType.SNOWSTORM);
            case NONE -> false;
        };
    }

    private static boolean spawnRainstorm(WeatherManagerServer manager, ServerPlayer player, Vec3 center,
                                          int intensityStage, boolean firenado) {
        if (hasNearbyRainstorm(manager, center, intensityStage, firenado)) {
            return false;
        }

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

    private static boolean spawnTornado(WeatherManagerServer manager, Vec3 center, boolean firenado) {
        if (hasNearbyTornado(manager, center, firenado)) {
            return false;
        }

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

    private static boolean spawnParticleStorm(WeatherManagerServer manager, ServerPlayer player, Vec3 center,
                                              WeatherObjectParticleStorm.StormType type) {
        if (hasNearbyParticleStorm(manager, center, type)) {
            return false;
        }

        manager.spawnParticleStorm(player.blockPosition(), type);
        long gameTime = manager.getWorld().getGameTime();
        if (type == WeatherObjectParticleStorm.StormType.SANDSTORM) {
            manager.lastSandstormFormed = gameTime;
        } else if (type == WeatherObjectParticleStorm.StormType.SNOWSTORM) {
            manager.lastSnowstormFormed = gameTime;
        }
        return true;
    }

    private static boolean hasNearbyRainstorm(WeatherManagerServer manager, Vec3 center,
                                              int intensityStage, boolean firenado) {
        for (WeatherObject weatherObject : manager.getStormsAround(center, STORM_NEARBY_RADIUS)) {
            if (!(weatherObject instanceof StormObject storm) || storm.isDead) {
                continue;
            }
            if (storm.isFirenado != firenado) {
                continue;
            }
            if (storm.isPrecipitating() && storm.levelCurIntensityStage == intensityStage) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasNearbyTornado(WeatherManagerServer manager, Vec3 center, boolean firenado) {
        for (WeatherObject weatherObject : manager.getStormsAround(center, STORM_NEARBY_RADIUS)) {
            if (!(weatherObject instanceof StormObject storm) || storm.isDead) {
                continue;
            }
            if (firenado) {
                if (storm.isFirenado) {
                    return true;
                }
                continue;
            }
            if (!storm.isFirenado && (storm.isTornadoFormingOrGreater() || storm.isCycloneFormingOrGreater())) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasNearbyParticleStorm(WeatherManagerServer manager, Vec3 center,
                                                  WeatherObjectParticleStorm.StormType type) {
        for (WeatherObject weatherObject : manager.getStormsAround(center, STORM_NEARBY_RADIUS)) {
            if (!(weatherObject instanceof WeatherObjectParticleStorm particleStorm) || particleStorm.isDead) {
                continue;
            }
            if (particleStorm.getType() == type) {
                return true;
            }
        }
        return false;
    }

    private static long getSpawnInterval(SolarStage stage) {
        return STAGE_SPAWN_INTERVALS[Math.max(0, Math.min(STAGE_SPAWN_INTERVALS.length - 1, stage.ordinal() - 1))];
    }
}
