package io.github.apfelrauber.stacked_trims;

import com.mojang.serialization.DataResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.tag.ItemTags;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArmorTrimList {

    public static Optional<List<ArmorTrim>> getTrims(DynamicRegistryManager registryManager, ItemStack stack) {
        if (!stack.isIn(ItemTags.TRIMMABLE_ARMOR)) return Optional.empty();

        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains("Trim")) return Optional.empty();

        NbtList nbtList = nbt.getList("Trim", 10);
        if (nbtList.isEmpty()) return Optional.empty();

        List<ArmorTrim> armorTrims = new ArrayList<>(nbtList.size());
        for (NbtElement element : nbtList) {
            DataResult<ArmorTrim> result = ArmorTrim.CODEC.parse(RegistryOps.of(NbtOps.INSTANCE, registryManager), element);
            result.result().ifPresent(armorTrims::add);
        }

        return Optional.of(armorTrims);
    }
}
