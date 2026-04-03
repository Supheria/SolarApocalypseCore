package com.supheria.solar_apocalypse_core.procedures.stats;

import com.supheria.solar_apocalypse_core.SolarApocalypseCoreMod;
import com.supheria.solar_apocalypse_core.config.solar.StageHeightConfig;
import com.supheria.solar_apocalypse_core.network.SapModVariables;
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
        execute(event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), event.getEntity());
    }

    public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null)
            return;
        if (entity instanceof LivingEntity)
            return;
        if (entity instanceof FallingBlockEntity)
            return;
        if (entity instanceof Player)
            return;
        if (entity instanceof Snowball
                && SapModVariables.MapVariables.get(world).solarStage < 6) {
            entity.setSecondsOnFire(1);
            SolarApocalypseCoreMod.queueServerWork(20, () -> {
                entity.remove(Entity.RemovalReason.KILLED);
            });
        }
        if (entity instanceof Projectile
                && SapModVariables.MapVariables.get(world).solarStage < 6) {
            if (!(entity instanceof Snowball)) {
                entity.setSecondsOnFire(5);
                SolarApocalypseCoreMod.queueServerWork(100, () -> {
                    entity.remove(Entity.RemovalReason.KILLED);
                });
            }
        }
        if (!(entity instanceof Projectile)) {
            int stage = (int) SapModVariables.MapVariables.get(world).solarStage;
            SolarApocalypseCoreMod.queueServerWork(1, () -> {
                if (SapModVariables.MapVariables.get(world).solarStage == 2
                        && !(SapModVariables.MapVariables.get(world).TodayTime > 12566 && SapModVariables.MapVariables.get(world).TodayTime < 23450)
                        && !world.getLevelData().isRaining()) {
                    if (world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z))) {
                        entity.setSecondsOnFire(10);
                        SolarApocalypseCoreMod.queueServerWork(20, () -> {
                            entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), (float) (1));
                            SolarApocalypseCoreMod.queueServerWork(20, () -> {
                                entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), (float) (1));
                                SolarApocalypseCoreMod.queueServerWork(20, () -> {
                                    entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), (float) (1));
                                    SolarApocalypseCoreMod.queueServerWork(20, () -> {
                                        entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), (float) (1));
                                        SolarApocalypseCoreMod.queueServerWork(20, () -> {
                                            entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), (float) (10));
                                        });
                                    });
                                });
                            });
                        });
                    } else if (y > StageHeightConfig.getExtraDamageHeight(stage)) {
                        entity.setSecondsOnFire(10);
                        SolarApocalypseCoreMod.queueServerWork(20, () -> {
                            entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), (float) (1));
                            SolarApocalypseCoreMod.queueServerWork(20, () -> {
                                entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), (float) (1));
                                SolarApocalypseCoreMod.queueServerWork(20, () -> {
                                    entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), (float) (1));
                                    SolarApocalypseCoreMod.queueServerWork(20, () -> {
                                        entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), (float) (1));
                                        SolarApocalypseCoreMod.queueServerWork(120, () -> {
                                            entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), (float) (10));
                                        });
                                    });
                                });
                            });
                        });
                    }
                }
                if (SapModVariables.MapVariables.get(world).solarStage >= 3 && SapModVariables.MapVariables.get(world).solarStage < 6
                        && world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1, z))) {
                    entity.setSecondsOnFire(5);
                    SolarApocalypseCoreMod.queueServerWork(5, () -> {
                        entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), (float) (0.5));
                        SolarApocalypseCoreMod.queueServerWork(5, () -> {
                            entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), (float) (10));
                        });
                    });
                }
                if (stage == 3
                        && y > StageHeightConfig.getSafeHeight(stage)) {
                    entity.setSecondsOnFire(5);
                    SolarApocalypseCoreMod.queueServerWork(20, () -> {
                        entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), (float) (1));
                        SolarApocalypseCoreMod.queueServerWork(20, () -> {
                            entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), (float) (1));
                            SolarApocalypseCoreMod.queueServerWork(20, () -> {
                                entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), (float) (1));
                                SolarApocalypseCoreMod.queueServerWork(20, () -> {
                                    entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), (float) (1));
                                    SolarApocalypseCoreMod.queueServerWork(20, () -> {
                                        entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), (float) (10));
                                    });
                                });
                            });
                        });
                    });
                    if (y > StageHeightConfig.getExtraDamageHeight(stage)) {
                        SolarApocalypseCoreMod.queueServerWork(5, () -> {
                            entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), (float) (1));
                            SolarApocalypseCoreMod.queueServerWork(5, () -> {
                                entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), (float) (10));
                            });
                        });
                    }
                }
                if (stage == 4
                        && y > StageHeightConfig.getSafeHeight(stage)) {
                    entity.setSecondsOnFire(5);
                    SolarApocalypseCoreMod.queueServerWork(20, () -> {
                        entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), (float) (1));
                        SolarApocalypseCoreMod.queueServerWork(20, () -> {
                            entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), (float) (1));
                            SolarApocalypseCoreMod.queueServerWork(10, () -> {
                                entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), (float) (10));
                            });
                        });
                    });
                    if (y > StageHeightConfig.getExtraDamageHeight(stage)) {
                        SolarApocalypseCoreMod.queueServerWork(5, () -> {
                            entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), (float) (1));
                            SolarApocalypseCoreMod.queueServerWork(5, () -> {
                                entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), (float) (10));
                            });
                        });
                    }
                }
                if (stage == 5
                        && y > StageHeightConfig.getSafeHeight(stage)) {
                    entity.setSecondsOnFire(5);
                    SolarApocalypseCoreMod.queueServerWork(5, () -> {
                        entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), (float) (1));
                        SolarApocalypseCoreMod.queueServerWork(5, () -> {
                            entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), (float) (10));
                        });
                    });
                    if (y > StageHeightConfig.getExtraDamageHeight(stage)) {
                        SolarApocalypseCoreMod.queueServerWork(1, () -> {
                            entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), (float) (1));
                            SolarApocalypseCoreMod.queueServerWork(1, () -> {
                                entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), (float) (10));
                            });
                        });
                    }
                }
            });
        }
    }
}
