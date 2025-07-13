package io.github.apfelrauber.stacked_trims.fabric;

import io.github.apfelrauber.stacked_trims.StackedTrims;
import io.github.apfelrauber.stacked_trims.StackedTrimGameRules;
import io.github.apfelrauber.stacked_trims.component.StackedTrimsComponents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameRules;

public class StackedTrimsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        StackedTrims.init();

        // 注册数据组件
        StackedTrimsComponents.STACKED_TRIMS = Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                ResourceLocation.fromNamespaceAndPath(StackedTrims.MOD_ID, "stacked_trims"),
                StackedTrimsComponents.createStackedTrimsComponent()
        );

        // 注册游戏规则
        StackedTrimGameRules.MAX_TRIM_STACK = GameRuleRegistry.register(
                "maxTrimStack",
                GameRules.Category.MISC,
                GameRuleFactory.createIntRule(100, 0, 1000)
        );

        StackedTrimGameRules.ALLOW_DUPLICATE_TRIMS = GameRuleRegistry.register(
                "allowDuplicateTrims",
                GameRules.Category.MISC,
                GameRuleFactory.createBooleanRule(false)
        );

        // 注册服务器事件
        ServerTickEvents.END_SERVER_TICK.register(server ->
                StackedTrims.setGameRules(server.getGameRules())
        );

        StackedTrims.LOGGER.info("Stacked Trims Fabric initialized");
    }

    /**
     * 重写模组加载检查
     */
    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}