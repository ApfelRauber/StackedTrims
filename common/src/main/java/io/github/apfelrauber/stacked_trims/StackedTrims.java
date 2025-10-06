package io.github.apfelrauber.stacked_trims;

import io.github.apfelrauber.stacked_trims.component.StackedTrimsComponents;
import net.minecraft.world.level.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StackedTrims {
    public static final String MOD_ID = "stacked_trims";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static GameRules currentGameRules;
    public static final boolean isBetterTrimTooltipsEnabled = isModLoaded("better-trim-tooltips");

    public static void init() {
        LOGGER.info("Initializing Stacked Trims");
        StackedTrimGameRules.init();
    }

    public static GameRules getCurrentGameRules() {
        return currentGameRules;
    }

    public static void setGameRules(GameRules gameRules) {
        currentGameRules = gameRules;
    }

    protected static boolean isModLoaded(String modId) {
        return false;
    }
}
