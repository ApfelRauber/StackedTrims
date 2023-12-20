package io.github.apfelrauber.stacked_trims.mixin;

import io.github.apfelrauber.stacked_trims.ArmorTrimList;
import io.github.apfelrauber.stacked_trims.StackedTrimGameRules;
import io.github.apfelrauber.stacked_trims.StackedTrims;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.recipe.SmithingTrimRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(SmithingTrimRecipe.class)
public class SmithingTrimRecipeMixin {
    @Inject(method = "craft", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/trim/ArmorTrim;getTrim(Lnet/minecraft/registry/DynamicRegistryManager;Lnet/minecraft/item/ItemStack;)Ljava/util/Optional;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void allowDuplicateTrims(Inventory inventory, DynamicRegistryManager registryManager, CallbackInfoReturnable<ItemStack> cir, ItemStack itemStack, Optional optional, Optional optional2){
        if(!StackedTrims.currentGameRules.getBoolean(StackedTrimGameRules.ALLOW_DUPLICATE_TRIMS)) return;

        ItemStack itemStack2 = itemStack.copy();
        itemStack2.setCount(1);
        ArmorTrim armorTrim = new ArmorTrim((RegistryEntry)optional.get(), (RegistryEntry)optional2.get());
        if (ArmorTrim.apply(registryManager, itemStack2, armorTrim)) {
            cir.setReturnValue(itemStack2);
            cir.cancel();
        }
    }

    @Inject(method = "craft", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void checkForDuplicateTrims(Inventory inventory, DynamicRegistryManager registryManager, CallbackInfoReturnable<ItemStack> cir, ItemStack itemStack, Optional optional, Optional optional2){
        ArmorTrimList.getTrims(registryManager, itemStack).ifPresent((armorTrims) -> {
            for (ArmorTrim armorTrim : armorTrims) {
                if (armorTrim.equals((RegistryEntry)optional2.get(), (RegistryEntry)optional.get())) {
                    cir.setReturnValue(ItemStack.EMPTY);
                    cir.cancel();
                    return;
                }
            }
        });
    }
}
