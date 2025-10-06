package io.github.apfelrauber.stacked_trims;

import io.github.apfelrauber.stacked_trims.config.StackedTrimsConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(StackedTrims.MOD_ID)
public class StackedTrims {

    public static final String MOD_ID = "stacked_trims";
    public static final String MOD_NAME = "Stacked Trims";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public StackedTrims() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册配置
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, StackedTrimsConfig.SPEC);


        // 注册事件监听器
        MinecraftForge.EVENT_BUS.register(this);

        LOGGER.info("Stacked Trims mod initialized");
    }

    /**
     * 创建模组资源位置
     */
    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}