package io.github.apfelrauber.stacked_trims.util;

import io.github.apfelrauber.stacked_trims.config.StackedTrimsConfig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * 装饰移除工具类
 */
public class TrimRemovalHelper {
    
    /**
     * 检查是否可以移除装饰
     */
    public static boolean canRemoveTrims(ItemStack armorStack, ItemStack toolStack) {
        if (!StackedTrimsConfig.ENABLE_FLINT_REMOVAL.get()) {
            return false;
        }
        
        if (armorStack.isEmpty() || toolStack.isEmpty()) {
            return false;
        }
        
        if (!toolStack.is(Items.FLINT)) {
            return false;
        }
        
        int trimCount = TrimDataHelper.getTrimCount(armorStack);
        int requiredFlint = StackedTrimsConfig.FLINT_REMOVAL_COST.get();
        
        return trimCount > 0 && toolStack.getCount() >= requiredFlint;
    }
    
    /**
     * 计算移除结果
     */
    public static ItemStack getRemovalResult(ItemStack armorStack, ItemStack toolStack) {
        if (!canRemoveTrims(armorStack, toolStack)) {
            return ItemStack.EMPTY;
        }
        
        ItemStack result = armorStack.copy();
        int trimCount = TrimDataHelper.getTrimCount(result);
        
        // 移除所有装饰
        TrimDataHelper.removeTrims(result, trimCount);
        
        return result;
    }
    
    /**
     * 获取消耗的燧石数量
     */
    public static int getFlintCost(ItemStack armorStack, ItemStack toolStack) {
        if (!canRemoveTrims(armorStack, toolStack)) {
            return 0;
        }
        
        return StackedTrimsConfig.FLINT_REMOVAL_COST.get();
    }
}