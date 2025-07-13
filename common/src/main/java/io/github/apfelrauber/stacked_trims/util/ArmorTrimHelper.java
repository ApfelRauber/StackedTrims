package io.github.apfelrauber.stacked_trims.util;

import io.github.apfelrauber.stacked_trims.component.StackedTrimsComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ArmorTrimHelper {

    public static Optional<List<ArmorTrim>> getTrims(ItemStack stack) {
        if (!stack.is(ItemTags.TRIMMABLE_ARMOR)) {
            return Optional.empty();
        }

        List<ArmorTrim> trims = stack.get(StackedTrimsComponents.STACKED_TRIMS);
        if (trims == null || trims.isEmpty()) {
            ArmorTrim singleTrim = stack.get(DataComponents.TRIM);
            if (singleTrim != null) {
                return Optional.of(List.of(singleTrim));
            }
            return Optional.empty();
        }

        // Return the inverted list to maintain the rendering order
        List<ArmorTrim> reversed = new ArrayList<>(trims);
        Collections.reverse(reversed);
        return Optional.of(reversed);
    }

    public static boolean addTrim(ItemStack stack, ArmorTrim newTrim) {
        return addTrim(stack, newTrim, Integer.MAX_VALUE);
    }

    public static boolean addTrim(ItemStack stack, ArmorTrim newTrim, int maxStack) {
        if (!stack.is(ItemTags.TRIMMABLE_ARMOR)) {
            return false;
        }

        List<ArmorTrim> currentTrims = stack.getOrDefault(
                StackedTrimsComponents.STACKED_TRIMS,
                new ArrayList<>()
        );

        if (currentTrims.size() >= maxStack) {
            return false;
        }

        List<ArmorTrim> newTrims = new ArrayList<>(currentTrims);
        newTrims.add(newTrim);

        stack.set(StackedTrimsComponents.STACKED_TRIMS, newTrims);

        stack.set(DataComponents.TRIM, newTrim);

        return true;
    }

    public static Optional<ArmorTrim> getLastTrim(ItemStack stack) {
        return getTrims(stack)
                .filter(trims -> !trims.isEmpty())
                .map(trims -> trims.get(trims.size() - 1));
    }

    public static boolean hasTrim(ItemStack stack, ArmorTrim trim) {
        return getTrims(stack)
                .map(trims -> trims.stream().anyMatch(t -> t.equals(trim)))
                .orElse(false);
    }

    public static void removeTrims(ItemStack stack, int count) {
        List<ArmorTrim> currentTrims = stack.getOrDefault(
                StackedTrimsComponents.STACKED_TRIMS,
                new ArrayList<>()
        );

        if (currentTrims.isEmpty()) {
            return;
        }

        List<ArmorTrim> newTrims = new ArrayList<>(currentTrims);
        int toRemove = Math.min(count, newTrims.size());


        for (int i = 0; i < toRemove; i++) {
            if (!newTrims.isEmpty()) {
                newTrims.removeLast();
            }
        }

        if (newTrims.isEmpty()) {
            stack.remove(StackedTrimsComponents.STACKED_TRIMS);
            stack.remove(DataComponents.TRIM);
        } else {
            stack.set(StackedTrimsComponents.STACKED_TRIMS, newTrims);
            stack.set(DataComponents.TRIM, newTrims.getLast());
        }
    }

    public static int getTrimCount(ItemStack stack) {
        return getTrims(stack).map(List::size).orElse(0);
    }
}