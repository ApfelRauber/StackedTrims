package de.seiboldsmuehle.stacked_trims;

import com.mojang.serialization.DataResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.tag.ItemTags;

import java.util.Optional;

public class ArmorTrimArray {

    public static Optional<ArmorTrim[]> getTrim(DynamicRegistryManager registryManager, ItemStack stack) {
        if (stack.isIn(ItemTags.TRIMMABLE_ARMOR) && stack.getNbt() != null && stack.getNbt().contains("Trim") && stack.getNbt().getList("Trim",10).size() >= 1) {
            NbtList nbtList = stack.getNbt().getList("Trim",10);

            DataResult[] dataResults = new DataResult[stack.getNbt().getList("Trim",10).size()];
            ArmorTrim[] armorTrims = new ArmorTrim[stack.getNbt().getList("Trim",10).size()];
            for(int i = 0; i < stack.getNbt().getList("Trim",10).size(); i++){
                dataResults[i] = ArmorTrim.CODEC.parse(RegistryOps.of(NbtOps.INSTANCE, registryManager), nbtList.get(i));
                armorTrims[i] = (ArmorTrim)dataResults[i].result().orElse((Object)null);
            }

            return Optional.ofNullable(armorTrims);
        } else {
            return Optional.empty();
        }
    }
}
