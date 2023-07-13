package io.github.apfelrauber.stacked_trims;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

public class ServerTickListener implements ServerTickEvents.EndTick{
    @Override
    public void onEndTick(MinecraftServer server) {
        StackedTrimsMain.onServerTick(server);
    }
}
