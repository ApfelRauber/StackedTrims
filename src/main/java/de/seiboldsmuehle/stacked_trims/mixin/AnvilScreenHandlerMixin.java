package de.seiboldsmuehle.stacked_trims.mixin;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {

    protected AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void inject(CallbackInfo ci, ItemStack itemStack, int i, int j, int k, ItemStack itemStack2, ItemStack itemStack3, Map map) {
        if (!(itemStack3.isOf(Items.FLINT) && itemStack2.isDamageable())) return;

        NbtCompound nbtCompound = itemStack2.getNbt();
        if (nbtCompound == null || nbtCompound.getList("Trim", 10).isEmpty()) return;

        ItemStack itemStack4 = itemStack2.copy();
        assert itemStack4.getNbt() != null; // already checked
        itemStack4.getNbt().getList("Trim", 10).clear();
        this.output.setStack(0, itemStack4);
        this.sendContentUpdates();
        ci.cancel();
    }
}
