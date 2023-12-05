package io.github.apfelrauber.stacked_trims.sidedgamerulegetter;

import io.github.apfelrauber.stacked_trims.StackedTrims;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class ServerGameRulesGetter implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        ServerTickEvents.END_SERVER_TICK.register(server -> StackedTrims.setGameRules(server.getGameRules()));
    }
}
