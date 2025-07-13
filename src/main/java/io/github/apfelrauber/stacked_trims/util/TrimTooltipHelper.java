package io.github.apfelrauber.stacked_trims.util;

import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraftforge.fml.ModList;

import java.util.List;
import java.util.Optional;

/**
 * 装饰提示工具类
 */
public class TrimTooltipHelper {
    
    private static final Component UPGRADE_TEXT = Component.translatable("item.smithing_template.upgrade")
            .withStyle(ChatFormatting.GRAY);
    
    /**
     * 添加装饰提示到物品提示列表
     */
    public static void addTrimTooltips(RegistryAccess registryAccess, ItemStack stack, List<Component> tooltip) {
        // 如果安装了 Better Trim Tooltips 模组，则跳过
        if (ModList.get().isLoaded("better-trim-tooltips")) {
            return;
        }
        
        Optional<List<ArmorTrim>> trims = TrimDataHelper.getTrims(registryAccess, stack);
        if (trims.isEmpty() || trims.get().isEmpty()) {
            return;
        }
        
        List<ArmorTrim> trimList = trims.get();
        
        // 添加标题
        tooltip.add(UPGRADE_TEXT);
        
        // 添加每个装饰的信息
        for (ArmorTrim trim : trimList) {
            addSingleTrimTooltip(tooltip, trim);
        }
    }
    
    /**
     * 添加单个装饰的提示
     */
    private static void addSingleTrimTooltip(List<Component> tooltip, ArmorTrim trim) {
        // 添加图案名称
        tooltip.add(CommonComponents.space()
                .append(trim.pattern().value().copyWithStyle(trim.material())));
        
        // 添加材料名称
        tooltip.add(CommonComponents.space()
                .append(trim.material().value().description()));
    }
}