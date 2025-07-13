package io.github.apfelrauber.stacked_trims.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.apfelrauber.stacked_trims.StackedTrimGameRules;
import io.github.apfelrauber.stacked_trims.util.ArmorTrimHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(SmithingTrimRecipe.class)
public class SmithingTrimRecipeMixin {

    @Inject(
            method = "assemble(Lnet/minecraft/world/item/crafting/SmithingRecipeInput;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"),
            cancellable = true
    )
    public void handleStackedTrims(SmithingRecipeInput input, HolderLookup.Provider registries,
                                   CallbackInfoReturnable<ItemStack> cir,
                                   @Local ItemStack baseStack,
                                   @Local(ordinal = 0) Optional<Holder.Reference<TrimMaterial>> materialOpt,
                                   @Local(ordinal = 1) Optional<Holder.Reference<TrimPattern>> patternOpt) {

        if (materialOpt.isEmpty() || patternOpt.isEmpty()) {
            return;
        }

        ArmorTrim newTrim = new ArmorTrim(materialOpt.get(), patternOpt.get());

        if (!StackedTrimGameRules.allowDuplicateTrims() &&
                ArmorTrimHelper.hasTrim(baseStack, newTrim)) {
            cir.setReturnValue(ItemStack.EMPTY);
            return;
        }

        ItemStack result = baseStack.copyWithCount(1);
        int maxStack = StackedTrimGameRules.getMaxTrimStack();

        if (maxStack == 0) {
            cir.setReturnValue(ItemStack.EMPTY);
            return;
        }

        if (ArmorTrimHelper.addTrim(result, newTrim, maxStack)) {
            cir.setReturnValue(result);
        } else {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }
}