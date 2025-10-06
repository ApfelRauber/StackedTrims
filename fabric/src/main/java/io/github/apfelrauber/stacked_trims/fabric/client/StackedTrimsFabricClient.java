package io.github.apfelrauber.stacked_trims.fabric.client;

import io.github.apfelrauber.stacked_trims.StackedTrims;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

public class StackedTrimsFabricClient implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        // 注册客户端事件
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.level != null) {
                StackedTrims.setGameRules(client.level.getGameRules());
            }
        });
        
        StackedTrims.LOGGER.info("Stacked Trims Fabric Client initialized");
    }
}