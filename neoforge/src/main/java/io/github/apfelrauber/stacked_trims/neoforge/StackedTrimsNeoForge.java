package io.github.apfelrauber.stacked_trims.neoforge;

import io.github.apfelrauber.stacked_trims.StackedTrims;
import io.github.apfelrauber.stacked_trims.StackedTrimGameRules;
import io.github.apfelrauber.stacked_trims.component.StackedTrimsComponents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

@Mod(StackedTrims.MOD_ID)
public class StackedTrimsNeoForge {

    private static final DeferredRegister<DataComponentType<?>> COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, StackedTrims.MOD_ID);

    private static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ArmorTrim>>> STACKED_TRIMS_COMPONENT =
            COMPONENTS.register("stacked_trims", StackedTrimsComponents::createStackedTrimsComponent);

    public StackedTrimsNeoForge(IEventBus modEventBus) {
        StackedTrims.init();

        COMPONENTS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.addListener(this::onServerTick);

        StackedTrims.LOGGER.info("Stacked Trims NeoForge initialized");
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        StackedTrimsComponents.STACKED_TRIMS = STACKED_TRIMS_COMPONENT.get();

        event.enqueueWork(() -> {
            StackedTrimGameRules.MAX_TRIM_STACK = GameRules.register(
                    "maxTrimStack",
                    GameRules.Category.MISC,
                    GameRules.IntegerValue.create(100)
            );

            StackedTrimGameRules.ALLOW_DUPLICATE_TRIMS = GameRules.register(
                    "allowDuplicateTrims",
                    GameRules.Category.MISC,
                    GameRules.BooleanValue.create(false)
            );
        });
    }

    private void onServerTick(ServerTickEvent.Post event) {
        StackedTrims.setGameRules(event.getServer().getGameRules());
    }

    public static boolean isModLoaded(String modId) {
        return ModLoadingContext.get().getActiveContainer().getModId().equals(modId);
    }
}