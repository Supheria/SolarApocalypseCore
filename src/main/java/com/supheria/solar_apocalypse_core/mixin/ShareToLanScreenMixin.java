package com.supheria.solar_apocalypse_core.mixin;

import com.supheria.solar_apocalypse_core.world.CheatLockManager;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ShareToLanScreen;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShareToLanScreen.class)
public abstract class ShareToLanScreenMixin {
    private static final GameType[] SOLAR_APOCALYPSE_CORE_SAFE_LAN_GAMEMODES = new GameType[]{GameType.SURVIVAL};

    @Shadow
    private boolean commands;

    @Shadow
    private GameType gameMode;

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void solarApocalypseCore$lockLanCheatToggle(CallbackInfo ci) {
        ShareToLanScreen self = (ShareToLanScreen) (Object) this;
        IntegratedServer server = self.getMinecraft().getSingleplayerServer();
        if (server == null || !CheatLockManager.isCheatLockEnabled(server)) {
            return;
        }

        this.commands = false;
        this.gameMode = GameType.SURVIVAL;
    }

    @ModifyArg(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/ShareToLanScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;",
                    ordinal = 0
            ),
            index = 0
    )
    private GuiEventListener solarApocalypseCore$disableLanGamemodeButton(GuiEventListener widget) {
        ShareToLanScreen self = (ShareToLanScreen) (Object) this;
        IntegratedServer server = self.getMinecraft().getSingleplayerServer();
        if (server == null || !CheatLockManager.isCheatLockEnabled(server)) {
            return widget;
        }

        if (widget instanceof CycleButton<?> rawButton) {
            @SuppressWarnings("unchecked")
            CycleButton<GameType> button = (CycleButton<GameType>) rawButton;
            button.setValue(GameType.SURVIVAL);
            button.active = false;
        }

        return widget;
    }

    @ModifyArg(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/ShareToLanScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;",
                    ordinal = 1
            ),
            index = 0
    )
    private GuiEventListener solarApocalypseCore$disableLanCheatButton(GuiEventListener widget) {
        ShareToLanScreen self = (ShareToLanScreen) (Object) this;
        IntegratedServer server = self.getMinecraft().getSingleplayerServer();
        if (server == null || !CheatLockManager.isCheatLockEnabled(server)) {
            return widget;
        }

        if (widget instanceof CycleButton<?> rawButton) {
            @SuppressWarnings("unchecked")
            CycleButton<Boolean> button = (CycleButton<Boolean>) rawButton;
            button.setValue(false);
            button.active = false;
        }

        return widget;
    }

    @ModifyArg(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/CycleButton$Builder;withValues([Ljava/lang/Object;)Lnet/minecraft/client/gui/components/CycleButton$Builder;",
                    ordinal = 0
            ),
            index = 0
    )
    private Object[] solarApocalypseCore$restrictLanGamemodeValues(Object[] values) {
        ShareToLanScreen self = (ShareToLanScreen) (Object) this;
        IntegratedServer server = self.getMinecraft().getSingleplayerServer();
        return server != null && CheatLockManager.isCheatLockEnabled(server)
                ? SOLAR_APOCALYPSE_CORE_SAFE_LAN_GAMEMODES
                : values;
    }

    @ModifyArg(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/CycleButton$Builder;withInitialValue(Ljava/lang/Object;)Lnet/minecraft/client/gui/components/CycleButton$Builder;",
                    ordinal = 0
            ),
            index = 0
    )
    private Object solarApocalypseCore$restrictLanGamemodeInitialValue(Object value) {
        ShareToLanScreen self = (ShareToLanScreen) (Object) this;
        IntegratedServer server = self.getMinecraft().getSingleplayerServer();
        if (server == null || !CheatLockManager.isCheatLockEnabled(server) || !(value instanceof GameType gameType)) {
            return value;
        }

        return GameType.SURVIVAL;
    }
}
