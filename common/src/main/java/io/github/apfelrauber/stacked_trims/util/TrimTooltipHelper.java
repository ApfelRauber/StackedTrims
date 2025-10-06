package io.github.apfelrauber.stacked_trims.util;

import io.github.apfelrauber.stacked_trims.StackedTrims;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;

import java.util.List;

public class TrimTooltipHelper {

    private static final Component UPGRADE_TEXT = Component.translatable(
            Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.upgrade"))
    ).withStyle(ChatFormatting.GRAY);


    public static void addTrimTooltips(ItemStack stack, List<Component> tooltip) {
        if (StackedTrims.isBetterTrimTooltipsEnabled) {
            return;
        }

        ArmorTrimHelper.getTrims(stack).ifPresent(trims -> {
            if (trims.isEmpty()) {
                return;
            }

            tooltip.add(UPGRADE_TEXT);

            for (ArmorTrim trim : trims) {
                tooltip.add(CommonComponents.space()
                        .append(trim.pattern().value().copyWithStyle(trim.material())));
                tooltip.add(CommonComponents.space()
                        .append(trim.material().value().description()));
            }
        });
    }
}