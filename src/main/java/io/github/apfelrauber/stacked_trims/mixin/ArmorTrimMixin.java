package io.github.apfelrauber.stacked_trims.mixin;

import com.mojang.serialization.DataResult;
import io.github.apfelrauber.stacked_trims.GameRules;
import io.github.apfelrauber.stacked_trims.StackedTrimsMain;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryOps;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(ArmorTrim.class)
public abstract class ArmorTrimMixin {
    private static final Text UPGRADE_TEXT;

    @Inject(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getOrCreateNbt()Lnet/minecraft/nbt/NbtCompound;"), cancellable = true)
    private static void apply(DynamicRegistryManager registryManager, ItemStack stack, ArmorTrim trim, CallbackInfoReturnable<Boolean> cir) {
        int limit = StackedTrimsMain.currentServer.getGameRules().getInt(GameRules.MAX_TRIM_STACK);

        if (limit == 0) {
            cir.setReturnValue(false);
            return;
        }

        NbtCompound nbt = stack.getOrCreateNbt();
        if (!nbt.contains("Trim")) {
            NbtList nbtList = new NbtList();
            nbtList.add(ArmorTrim.CODEC.encodeStart(RegistryOps.of(NbtOps.INSTANCE, registryManager), trim).result().orElseThrow());
            nbt.put("Trim", nbtList);
            cir.setReturnValue(true);
            return;
        }

        NbtList nbtList = nbt.getList("Trim", 10);
        if(nbtList.isEmpty()) {
            NbtElement preStacked = nbt.get("Trim");
            if(preStacked != null) {
                nbtList.add(preStacked);
            }
        }
        if (nbtList.size() >= limit) {
            cir.setReturnValue(false);
            return;
        }

        nbtList.add(ArmorTrim.CODEC.encodeStart(RegistryOps.of(NbtOps.INSTANCE, registryManager), trim).result().orElseThrow());
        nbt.put("Trim", nbtList);
        cir.setReturnValue(true);
    }

    @Inject(method = "getTrim", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getSubNbt(Ljava/lang/String;)Lnet/minecraft/nbt/NbtCompound;"), cancellable = true)
    private static void getFirstTrim(DynamicRegistryManager registryManager, ItemStack stack, CallbackInfoReturnable<Optional<ArmorTrim>> cir) {
        assert stack.getNbt() != null;
        NbtList nbtList = stack.getNbt().getList("Trim", 10); // key "Trim" is already checked by the original method
        NbtElement nbtElement;
        if (nbtList.isEmpty()) nbtElement = stack.getNbt().get("Trim");
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
        assert stack.getNbt() != null;
        NbtList nbtList = stack.getNbt().getList("Trim", 10);
        if(nbtList == null || nbtList.size() < 1) return;

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