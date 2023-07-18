package io.github.apfelrauber.stacked_trims.sidedgamerulegetter;

import io.github.apfelrauber.stacked_trims.StackedTrims;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

public class ServerGameRulesGetter implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        ServerTickEvents.END_SERVER_TICK.register(new ServerTickEvents.EndTick() {
            @Override
            public void onEndTick(MinecraftServer server) {
                StackedTrims.setGameRules(server.getGameRules());
            }
        });
    }
}
