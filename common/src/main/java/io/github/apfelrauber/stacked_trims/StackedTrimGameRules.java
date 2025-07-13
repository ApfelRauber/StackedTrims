package io.github.apfelrauber.stacked_trims;

import net.minecraft.world.level.GameRules;

public class StackedTrimGameRules {
    public static GameRules.Key<GameRules.IntegerValue> MAX_TRIM_STACK;
    public static GameRules.Key<GameRules.BooleanValue> ALLOW_DUPLICATE_TRIMS;

    public static void init() {
        StackedTrims.LOGGER.info("Registering game rules");
    }

    public static int getMaxTrimStack() {
        GameRules gameRules = StackedTrims.getCurrentGameRules();
        if (gameRules == null || MAX_TRIM_STACK == null) {
            return 100;
        }
        return gameRules.getInt(MAX_TRIM_STACK);
    }

    public static boolean allowDuplicateTrims() {
        GameRules gameRules = StackedTrims.getCurrentGameRules();
        if (gameRules == null || ALLOW_DUPLICATE_TRIMS == null) {
            return false;
        }
        return gameRules.getBoolean(ALLOW_DUPLICATE_TRIMS);
    }
}