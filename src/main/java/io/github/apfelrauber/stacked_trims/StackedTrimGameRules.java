package io.github.apfelrauber.stacked_trims;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class StackedTrimGameRules {
    public static GameRules.Key<net.minecraft.world.GameRules.IntRule> MAX_TRIM_STACK;
    public static GameRules.Key<GameRules.BooleanRule> ALLOW_DUPLICATE_TRIMS;

    public static void setupGamerules() {
        MAX_TRIM_STACK = GameRuleRegistry.register("maxTrimStack", GameRules.Category.MISC, GameRuleFactory.createIntRule(100, 0, 1000));
        ALLOW_DUPLICATE_TRIMS = GameRuleRegistry.register("allowDuplicateTrims", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));
    }
}
