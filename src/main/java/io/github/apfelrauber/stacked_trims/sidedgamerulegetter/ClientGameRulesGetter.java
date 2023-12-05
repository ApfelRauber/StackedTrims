package io.github.apfelrauber.stacked_trims.sidedgamerulegetter;

import io.github.apfelrauber.stacked_trims.StackedTrims;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class ClientGameRulesGetter implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(client.world != null) StackedTrims.setGameRules(client.world.getGameRules());
        });
    }
}
