package io.github.apfelrauber.stacked_trims.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TrimRemovalHelper {


    public static boolean canRemoveTrims(ItemStack armorStack, ItemStack toolStack) {
        if (armorStack.isEmpty() || toolStack.isEmpty()) {
            return false;
        }

        if (!toolStack.is(Items.FLINT)) {
            return false;
        }

        int trimCount = ArmorTrimHelper.getTrimCount(armorStack);
        return trimCount > 0 && toolStack.getCount() >= 1;
    }

    public static ItemStack getRemovalResult(ItemStack armorStack, ItemStack toolStack) {
        if (!canRemoveTrims(armorStack, toolStack)) {
            return ItemStack.EMPTY;
        }

        int trimCount = ArmorTrimHelper.getTrimCount(armorStack);
        int flintCount = toolStack.getCount();

        ItemStack result = armorStack.copy();

        ArmorTrimHelper.removeTrims(result, Math.min(flintCount, trimCount));

        return result;
    }

    public static int getFlintCost(ItemStack armorStack, ItemStack toolStack) {
        if (!canRemoveTrims(armorStack, toolStack)) {
            return 0;
        }

        int trimCount = ArmorTrimHelper.getTrimCount(armorStack);
        int flintCount = toolStack.getCount();

        return Math.min(trimCount, flintCount);
    }
}