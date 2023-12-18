package io.github.apfelrauber.stacked_trims.mixin;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apfelrauber.stacked_trims.StackedTrimGameRules;
import io.github.apfelrauber.stacked_trims.StackedTrims;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.item.trim.ArmorTrimPattern;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
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
import net.minecraft.util.dynamic.Codecs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(ArmorTrim.class)
public abstract class ArmorTrimMixin {
    private static final Text UPGRADE_TEXT;
    @Shadow private static final Codec<ArmorTrim> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(ArmorTrimMaterial.ENTRY_CODEC.fieldOf("material").forGetter(ArmorTrim::getMaterial), ArmorTrimPattern.ENTRY_CODEC.fieldOf("pattern").forGetter(ArmorTrim::getPattern)).apply(instance, ArmorTrim::new);
    });

    @Inject(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getOrCreateNbt()Lnet/minecraft/nbt/NbtCompound;"), cancellable = true)
    private static void apply(DynamicRegistryManager registryManager, ItemStack stack, ArmorTrim trim, CallbackInfoReturnable<Boolean> cir) {
        if (StackedTrims.currentGameRules == null) {
            cir.setReturnValue(false);
            return;
        }

        int limit = StackedTrims.currentGameRules.getInt(StackedTrimGameRules.MAX_TRIM_STACK);

        if (limit == 0) {
            cir.setReturnValue(false);
            return;
        }

        NbtCompound nbt = stack.getOrCreateNbt();
        if (!nbt.contains("Trims")) {
            NbtList nbtList = new NbtList();
            nbtList.add(ArmorTrim.CODEC.encodeStart(RegistryOps.of(NbtOps.INSTANCE, registryManager), trim).result().orElseThrow());
            // There is a Trim NBT but no Trims NBT, that means Stacked Trims was installed after Trims were applied. This code merges these old trims.
            if(nbt.contains("Trim")) {
                NbtList nbtList1 = nbt.getList("Trim", 10);
                NbtElement nbtElement = nbt.get("Trim");
                if(nbtElement != null) {
                    nbtList.add(nbtElement);
                } else {
                    nbtList.add(nbtList1);
                }
                assert stack.getNbt() != null;
                stack.getNbt().remove("Trim");
            }
            stack.getOrCreateNbt().put("Trim", (NbtElement)CODEC.encodeStart(RegistryOps.of(NbtOps.INSTANCE, registryManager), trim).result().orElseThrow());
            nbt.put("Trims", nbtList);
            cir.setReturnValue(true);
            return;
        }

        NbtList nbtList = nbt.getList("Trims", 10);
        if(nbtList.isEmpty()) {
            NbtElement preStacked = nbt.get("Trims");
            if(preStacked != null) {
                nbtList.add(preStacked);
            }
        }
        if (nbtList.size() >= limit) {
            cir.setReturnValue(false);
            return;
        }

        nbtList.add(ArmorTrim.CODEC.encodeStart(RegistryOps.of(NbtOps.INSTANCE, registryManager), trim).result().orElseThrow());
        nbt.put("Trims", nbtList);
        stack.getOrCreateNbt().put("Trim", (NbtElement)CODEC.encodeStart(RegistryOps.of(NbtOps.INSTANCE, registryManager), trim).result().orElseThrow());
        cir.setReturnValue(true);
    }

    @Inject(method = "getTrim", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getSubNbt(Ljava/lang/String;)Lnet/minecraft/nbt/NbtCompound;"), cancellable = true)
    private static void getFirstTrim(DynamicRegistryManager registryManager, ItemStack stack, boolean suppressError, CallbackInfoReturnable<Optional<ArmorTrim>> cir) {
        assert stack.getNbt() != null;
        if(!stack.getNbt().contains("Trims")) {
            cir.setReturnValue(Optional.empty());
            return;
        }

        NbtList nbtList = stack.getNbt().getList("Trims", 10);
        NbtElement nbtElement;
        if (nbtList.isEmpty()) nbtElement = stack.getNbt().get("Trims");
        else nbtElement = nbtList.get(0);
        if(nbtElement == null) {
            cir.setReturnValue(Optional.empty());
            return;
        }
        DataResult<ArmorTrim> result = ArmorTrim.CODEC.parse(RegistryOps.of(NbtOps.INSTANCE, registryManager), nbtElement);
        cir.setReturnValue(result.result());
    }

    @Inject(method = "appendTooltip", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", shift = At.Shift.BEFORE), cancellable = true)
    private static void appendAdditionalTooltips(ItemStack stack, DynamicRegistryManager registryManager, List<Text> tooltip, CallbackInfo ci) {
        if(StackedTrims.isBetterTrimTooltipsEnables) return; // If BetterTrimTooltips is installed it will handle the tooltip generation instead.
        assert stack.getNbt() != null;
        NbtList nbtList = stack.getNbt().getList("Trims", 10);
        if(nbtList == null || nbtList.isEmpty()) return;

        for (NbtElement nbtElement : nbtList) {
            DataResult<ArmorTrim> result = ArmorTrim.CODEC.parse(RegistryOps.of(NbtOps.INSTANCE, registryManager), nbtElement);
            ArmorTrim armorTrim = result.result().orElse(null);
            if (armorTrim == null) continue;
            tooltip.add(UPGRADE_TEXT);
            break;
        }

        for (NbtElement nbtElement : nbtList) {
            DataResult<ArmorTrim> result = ArmorTrim.CODEC.parse(RegistryOps.of(NbtOps.INSTANCE, registryManager), nbtElement);
            ArmorTrim armorTrim = result.result().orElse(null);
            if (armorTrim == null) continue;

            tooltip.add(ScreenTexts.space().append(armorTrim.getPattern().value().getDescription(armorTrim.getMaterial())));
            tooltip.add(ScreenTexts.space().append(armorTrim.getMaterial().value().description()));
        }
        ci.cancel();
    }

    static {
        UPGRADE_TEXT = Text.translatable(Util.createTranslationKey("item", new Identifier("smithing_template.upgrade"))).formatted(Formatting.GRAY);
    }
}