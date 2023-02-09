package de.seiboldsmuehle.stacked_trims.mixin;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.tag.ItemTags;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;
import java.util.Optional;

@Mixin(ArmorFeatureRenderer.class)
public class MixinArmorFeatureRenderer {
    @Redirect(method="renderArmor", at = @At(value="INVOKE", target = "Lnet/minecraft/item/trim/ArmorTrim;getTrim(Lnet/minecraft/registry/DynamicRegistryManager;Lnet/minecraft/item/ItemStack;)Ljava/util/Optional;"))
    public Optional<ArmorTrim> getTrim(DynamicRegistryManager registryManager, ItemStack stack) {
        if (stack.isIn(ItemTags.TRIMMABLE_ARMOR) && stack.getNbt() != null && stack.getNbt().contains("Trim")) {

            NbtCompound nbtCompound = stack.getNbt().getCompound("Trim");
            NbtList nbtList = stack.getNbt().getList("Trim",10);

            nbtList.get(0);

            DataResult var10000 = ArmorTrim.CODEC.parse(RegistryOps.of(NbtOps.INSTANCE, registryManager), nbtCompound);
            ArmorTrim armorTrim = (ArmorTrim)var10000.result().orElse((Object)null);
            return Optional.ofNullable(armorTrim);
        } else {
            return Optional.empty();
        }
    }
}
