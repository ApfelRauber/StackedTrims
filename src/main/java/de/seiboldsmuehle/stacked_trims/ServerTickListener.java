package de.seiboldsmuehle.stacked_trims;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

public class ServerTickListener implements ServerTickEvents.StartTick {
    public static MinecraftServer currentServer;

    @Override
    public void onStartTick(MinecraftServer minecraftServer) {
        currentServer = minecraftServer;
    }
}
