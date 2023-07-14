package io.github.apfelrauber.stacked_trims;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;

public class StackedTrimGameRules {
    public static net.minecraft.world.GameRules.Key<net.minecraft.world.GameRules.IntRule> MAX_TRIM_STACK;

    public static void setupGamerules() {
        MAX_TRIM_STACK = GameRuleRegistry.register("maxTrimStack", net.minecraft.world.GameRules.Category.MISC, GameRuleFactory.createIntRule(100, 0, 1000));
    }
}
