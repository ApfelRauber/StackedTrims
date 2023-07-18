package io.github.apfelrauber.stacked_trims;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.GameRules;

public class StackedTrims implements ModInitializer {
    public static GameRules currentGameRules;
    public static final Boolean isBetterTrimTooltipsEnables = FabricLoader.getInstance().isModLoaded("better-trim-tooltips");
    @Override
    public void onInitialize() {
        StackedTrimGameRules.setupGamerules();
    }

    public static void setGameRules(GameRules gameRules){
        currentGameRules = gameRules;
    }
}
