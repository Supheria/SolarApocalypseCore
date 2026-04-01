
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package com.supheria.solar_apocalypse_core.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.effect.MobEffect;

import com.supheria.solar_apocalypse_core.potion.DehydrationMobEffect;
import com.supheria.solar_apocalypse_core.SolarApocalypseCoreMod;

public class SapModMobEffects {
	public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, SolarApocalypseCoreMod.MOD_ID);
	public static final RegistryObject<MobEffect> DEHYDRATION = REGISTRY.register("dehydration", () -> new DehydrationMobEffect());
}
