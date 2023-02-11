package de.seiboldsmuehle.stacked_trims.mixin;

import com.mojang.serialization.DataResult;
import de.seiboldsmuehle.stacked_trims.ArmorTrimArray;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.item.trim.ArmorTrimPattern;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;
import java.util.Optional;

@Mixin(ArmorTrim.class)
public class MixinArmorTrim {
    //replace get Trim
    /**
     * @author ApfelRauber
     * @reason I found no other way to give the Item Renderer and Model Renderer different getTrim methods, then Mixin Injecting the Model Renderer with a full custom getTrimArray method, and Overwriting the normal getTrim method, to fix Code that tries using getTrim without expecting an Array in return.
     */
    @Overwrite
    public static Optional<ArmorTrim> getTrim(DynamicRegistryManager registryManager, ItemStack stack) {
        if (stack.isIn(ItemTags.TRIMMABLE_ARMOR) && stack.getNbt() != null && stack.getNbt().contains("Trim") && stack.getNbt().getList("Trim",10).size() >= 1) {
            NbtList nbtList = stack.getNbt().getList("Trim",10);
            DataResult dataResult = ArmorTrim.CODEC.parse(RegistryOps.of(NbtOps.INSTANCE, registryManager), nbtList.get(0));
            ArmorTrim armorTrim = (ArmorTrim)dataResult.result().orElse((Object)null);
            return Optional.ofNullable(armorTrim);
        } else {
            return Optional.empty();
        }
    }

    //replace append Tooltip
    private static final Text UPGRADE_TEXT;
    /**
     * @author ApfelRauber
     * @reason Changed so much about the Tooltip, might as well Overwrite the appendTooltip method.
     */
    @Overwrite
    public static void appendTooltip(ItemStack stack, DynamicRegistryManager registryManager, List<Text> tooltip) {
        Optional<ArmorTrim[]> optional = ArmorTrimArray.getTrim(registryManager, stack);
        if (optional.isPresent()) {
            ArmorTrimArray.getTrim(registryManager, stack).ifPresent(armorTrims -> {
                tooltip.add(UPGRADE_TEXT);
                for(int i = 0; i < stack.getNbt().getList("Trim",10).size(); i++){
                    ArmorTrim armorTrim = armorTrims[i];
                    tooltip.add(ScreenTexts.space().append(((ArmorTrimPattern)armorTrim.getPattern().value()).getDescription(armorTrim.getMaterial())));
                    tooltip.add(ScreenTexts.space().append(((ArmorTrimMaterial)armorTrim.getMaterial().value()).description()));
                }
            });
        }
    }
    static {
        UPGRADE_TEXT = Text.translatable(Util.createTranslationKey("item", new Identifier("smithing_template.upgrade"))).formatted(Formatting.GRAY);
    }
}
