package io.github.apfelrauber.stacked_trims.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

/**
 * 模组配置类
 */
@Mod.EventBusSubscriber
public class StackedTrimsConfig {
    
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    
    public static final ForgeConfigSpec.IntValue MAX_TRIM_STACK;
    public static final ForgeConfigSpec.BooleanValue ALLOW_DUPLICATE_TRIMS;
    public static final ForgeConfigSpec.BooleanValue ENABLE_FLINT_REMOVAL;
    public static final ForgeConfigSpec.IntValue FLINT_REMOVAL_COST;
    
    static {
        BUILDER.push("General Settings");
        
        MAX_TRIM_STACK = BUILDER
                .comment("Maximum number of trims that can be stacked on a single armor piece")
                .defineInRange("maxTrimStack", 10, 1, 100);
        
        ALLOW_DUPLICATE_TRIMS = BUILDER
                .comment("Allow duplicate trims on the same armor piece")
                .define("allowDuplicateTrims", false);
        
        ENABLE_FLINT_REMOVAL = BUILDER
                .comment("Enable trim removal using flint in anvil")
                .define("enableFlintRemoval", true);
        
        FLINT_REMOVAL_COST = BUILDER
                .comment("Number of flint required to remove all trims")
                .defineInRange("flintRemovalCost", 1, 1, 64);
        
        BUILDER.pop();
    }
    
    public static final ForgeConfigSpec SPEC = BUILDER.build();
}