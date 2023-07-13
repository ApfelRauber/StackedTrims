package io.github.apfelrauber.stacked_trims;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

public class StackedTrimsMain implements ModInitializer {
    public static MinecraftServer currentServer;
    @Override
    public void onInitialize() {
        GameRules.setupGamerules();
        ServerTickEvents.END_SERVER_TICK.register(new ServerTickListener());// -> minecraftServer.getGameRules();
        //ClientTickEvents.START_CLIENT_TICK.register(new ClientTickListener()); -> MinecraftClient.getInstance().world.getGameRules();
    }

    public static void onServerTick(MinecraftServer server){
        currentServer = server;
    }
}
