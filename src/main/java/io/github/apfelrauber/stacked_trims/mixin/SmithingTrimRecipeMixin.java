package io.github.apfelrauber.stacked_trims.mixin;

import io.github.apfelrauber.stacked_trims.config.StackedTrimsConfig;
import io.github.apfelrauber.stacked_trims.util.TrimDataHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

/**
 * 锻造台装饰配方 Mixin
 * 处理堆叠装饰的添加逻辑
 */
@Mixin(SmithingTrimRecipe.class)
public class SmithingTrimRecipeMixin {

    @Inject(method = "assemble", at = @At("HEAD"), cancellable = true)
    public void onAssemble(Container container, RegistryAccess registryAccess, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack armorStack = container.getItem(1);
        ItemStack materialStack = container.getItem(2);
        ItemStack patternStack = container.getItem(0);

        if (armorStack.isEmpty() || materialStack.isEmpty() || patternStack.isEmpty()) {
            return;
        }

        // 获取装饰材料和图案
        Optional<Holder<TrimMaterial>> materialOpt = getTrimMaterialFromIngredient(registryAccess, materialStack);
        Optional<Holder<TrimPattern>> patternOpt = getTrimPatternFromTemplate(registryAccess, patternStack);

        if (materialOpt.isEmpty() || patternOpt.isEmpty()) {
            return;
        }

        ArmorTrim newTrim = new ArmorTrim(materialOpt.get(), patternOpt.get());
        ItemStack result = armorStack.copy();
        result.setCount(1);

        // 获取配置值
        int maxStack = StackedTrimsConfig.MAX_TRIM_STACK.get();
        boolean allowDuplicates = StackedTrimsConfig.ALLOW_DUPLICATE_TRIMS.get();

        // 尝试添加装饰
        if (TrimDataHelper.addTrim(registryAccess, result, newTrim, maxStack, allowDuplicates)) {
            cir.setReturnValue(result);
        } else {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }

    @Inject(method = "matches", at = @At("HEAD"), cancellable = true)
    public void onMatches(Container container, Level level, CallbackInfoReturnable<Boolean> cir) {
        ItemStack armorStack = container.getItem(1);
        ItemStack materialStack = container.getItem(2);
        ItemStack patternStack = container.getItem(0);

        if (armorStack.isEmpty() || materialStack.isEmpty() || patternStack.isEmpty()) {
            cir.setReturnValue(false);
            return;
        }

        // 检查是否可以添加更多装饰
        int currentTrims = TrimDataHelper.getTrimCount(armorStack);
        int maxStack = StackedTrimsConfig.MAX_TRIM_STACK.get();

        if (currentTrims >= maxStack) {
            cir.setReturnValue(false);
            return;
        }

        // 检查重复装饰
        if (!StackedTrimsConfig.ALLOW_DUPLICATE_TRIMS.get()) {
            Optional<Holder<TrimMaterial>> materialOpt = getTrimMaterialFromIngredient(level.registryAccess(), materialStack);
            Optional<Holder<TrimPattern>> patternOpt = getTrimPatternFromTemplate(level.registryAccess(), patternStack);

            if (materialOpt.isPresent() && patternOpt.isPresent()) {
                ArmorTrim newTrim = new ArmorTrim(materialOpt.get(), patternOpt.get());
                Optional<List<ArmorTrim>> existingTrims = TrimDataHelper.getTrims(level.registryAccess(), armorStack);

                if (existingTrims.isPresent()) {
                    for (ArmorTrim existingTrim : existingTrims.get()) {
                        if (existingTrim.material().equals(newTrim.material()) &&
                                existingTrim.pattern().equals(newTrim.pattern())) {
                            cir.setReturnValue(false);
                            return;
                        }
                    }
                }
            }
        }

        cir.setReturnValue(true);
    }
    /**
     * 从物品获取装饰材料
     */
    private static Optional<Holder<TrimMaterial>> getTrimMaterialFromIngredient(RegistryAccess registryAccess, ItemStack ingredient) {
        Registry<TrimMaterial> registry = registryAccess.registryOrThrow(Registries.TRIM_MATERIAL);

        for (Holder<TrimMaterial> holder : registry.holders().toList()) {
            TrimMaterial material = holder.value();
            if (material.ingredient().value().equals(ingredient.getItem())) {
                return Optional.of(holder);
            }
        }

        return Optional.empty();
    }

    /**
     * 从模板获取装饰图案
     */
    private static Optional<Holder<TrimPattern>> getTrimPatternFromTemplate(RegistryAccess registryAccess, ItemStack template) {
        Registry<TrimPattern> registry = registryAccess.registryOrThrow(Registries.TRIM_PATTERN);

        for (Holder<TrimPattern> holder : registry.holders().toList()) {
            TrimPattern pattern = holder.value();
            if (pattern.templateItem().equals(template.getItem())) {
                return Optional.of(holder);
            }
        }

        return Optional.empty();
    }
}