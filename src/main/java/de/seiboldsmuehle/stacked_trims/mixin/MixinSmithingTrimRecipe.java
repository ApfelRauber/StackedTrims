package de.seiboldsmuehle.stacked_trims.mixin;

import de.seiboldsmuehle.stacked_trims.GameRules;
import de.seiboldsmuehle.stacked_trims.ServerTickListener;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.nbt.*;
import net.minecraft.recipe.SmithingTrimRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.tag.ItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(SmithingTrimRecipe.class)
public class MixinSmithingTrimRecipe {

    @Redirect(method="craft", at = @At(value="INVOKE", target = "Lnet/minecraft/item/trim/ArmorTrim;apply(Lnet/minecraft/registry/DynamicRegistryManager;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/trim/ArmorTrim;)Z"))
    public boolean apply(DynamicRegistryManager registryManager, ItemStack stack, ArmorTrim trim) {
        if (stack.isIn(ItemTags.TRIMMABLE_ARMOR) && ServerTickListener.currentServer.getGameRules().get(GameRules.MAX_TRIM_STACK).get() != 0) {
            NbtList nbtList = new NbtList();
            if(stack.getNbt().contains("Trim")){
                if(stack.getNbt().getList("Trim",10).size() < ServerTickListener.currentServer.getGameRules().get(GameRules.MAX_TRIM_STACK).get()) {
                    nbtList = stack.getNbt().getList("Trim", 10);
                    nbtList.add(ArmorTrim.CODEC.encodeStart(RegistryOps.of(NbtOps.INSTANCE, registryManager), trim).result().orElseThrow());
                    stack.getOrCreateNbt().put("Trim",nbtList);
                    return true;
                }
            }else{
                nbtList.add(ArmorTrim.CODEC.encodeStart(RegistryOps.of(NbtOps.INSTANCE, registryManager), trim).result().orElseThrow());
                stack.getOrCreateNbt().put("Trim",nbtList);
                return true;
            }
        }
        return false;
    }
}