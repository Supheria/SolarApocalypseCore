package com.supheria.solar_apocalypse_core.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;

import com.supheria.solar_apocalypse_core.client.hud.SolarHudEditScreen;

/**
 * 客户端事件处理器
 * 处理快捷键输入等客户端事件
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEventHandler {

    public static final KeyMapping HUD_EDIT_KEY = new KeyMapping(
        "key.solar_apocalypse_core.hud_edit",
        InputConstants.KEY_H,
        "key.categories.solar_apocalypse_core"
    );

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(HUD_EDIT_KEY);
    }
}

/**
 * 输入事件处理器
 * 监听键盘输入
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT)
class KeyInputHandler {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (ClientEventHandler.HUD_EDIT_KEY.consumeClick()) {
            Minecraft minecraft = Minecraft.getInstance();
            // 只在有世界且不在其他屏幕打开时才打开编辑屏幕
            if (minecraft.level != null) {
                minecraft.setScreen(new SolarHudEditScreen());
            }
        }
    }
}
