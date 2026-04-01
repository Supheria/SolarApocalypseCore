package com.supheria.solar_apocalypse_core.item;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;


public class CoordinatesOfTheSunItem extends Item {
    public CoordinatesOfTheSunItem() {
        super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isFoil(ItemStack itemstack) {
        return true;
    }

}
