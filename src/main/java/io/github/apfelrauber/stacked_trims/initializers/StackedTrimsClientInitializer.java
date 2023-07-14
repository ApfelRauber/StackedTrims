package io.github.apfelrauber.stacked_trims.initializers;

import io.github.apfelrauber.stacked_trims.StackedTrimsMain;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.GameRules;

public class StackedTrimsClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(new ClientTickEvents.EndTick() {
            @Override
            public void onEndTick(MinecraftClient client) {
                if(client.world != null) StackedTrimsMain.setGameRules(client.world.getGameRules());
            }
        });
    }
}
