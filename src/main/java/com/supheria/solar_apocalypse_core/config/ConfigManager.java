package com.supheria.solar_apocalypse_core.config;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 配置文件管理器
 * 负责将配置文件放入 config/solar/ 子文件夹
 */
@Mod.EventBusSubscriber(modid = "solar_apocalypse_core", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigManager {

    @SubscribeEvent
    public static void onFMLLoadComplete(FMLLoadCompleteEvent event) {
        ensureSolarConfigDirectory();
    }

    /**
     * 确保 solar 配置目录存在并将配置文件移动到其中
     */
    public static void ensureSolarConfigDirectory() {
        Path configDir = FMLPaths.CONFIGDIR.get();
        Path solarDir = configDir.resolve("solar");

        // 创建 solar 子目录
        try {
            Files.createDirectories(solarDir);
        } catch (IOException e) {
            return; // 创建失败，不继续处理
        }

        // 需要移动的配置文件
        String[] configFiles = {
            "solar_apocalypse_core-common.toml",
            "solar_apocalypse_core-client.toml"
        };

        // 尝试移动配置文件到 solar 子目录
        for (String fileName : configFiles) {
            Path sourceFile = configDir.resolve(fileName);
            Path targetFile = solarDir.resolve(fileName);

            if (Files.exists(sourceFile) && !sourceFile.equals(targetFile)) {
                try {
                    Files.move(sourceFile, targetFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    // 移动失败，记录但继续
                }
            }
        }
    }
}
