package io.github.apfelrauber.stacked_trims;

import net.fabricmc.api.ModInitializer;
import net.minecraft.world.GameRules;

public class StackedTrimsMain implements ModInitializer {
    public static GameRules currentGameRules;
    @Override
    public void onInitialize() {
        StackedTrimGameRules.setupGamerules();
    }

    public static void setGameRules(GameRules gameRules){
        currentGameRules = gameRules;
    }
}
