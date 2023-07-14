package io.github.apfelrauber.stacked_trims.mixin;

import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {

    protected AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method = "updateResult", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void injectUpdateResult(CallbackInfo ci) {
        ItemStack itemStack1 = this.input.getStack(0);
        ItemStack itemStack2 = this.input.getStack(1);
        ItemStack itemStack3 = itemStack1.copy();

        if(itemStack1.isEmpty() || itemStack2.isEmpty() || itemStack1.getNbt() == null) return;
        if (!(itemStack2.isOf(Items.FLINT)) || itemStack1.getNbt().getList("Trim",10) == null) return;

        NbtCompound nbtCompound = itemStack1.getNbt();
        if (nbtCompound == null || nbtCompound.getList("Trim", 10).isEmpty()) return;

        itemStack3.getNbt().getList("Trim", 10).clear();
        itemStack3.getNbt().remove("Trim");

        if (itemStack1.getNbt().getList("Trim",10).size() > itemStack2.getCount()) {
            this.output.setStack(0, ItemStack.EMPTY);
            this.sendContentUpdates();
            ci.cancel();
            return;
        }

        this.output.setStack(0, itemStack3);
        this.sendContentUpdates();
        ci.cancel();
    }

    @Inject(method = "onTakeOutput", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void injectOnTakeOutput(PlayerEntity player, ItemStack stack, CallbackInfo ci){
        if(!this.input.getStack(1).isOf(Items.FLINT) || this.input.getStack(0).getNbt().getList("Trim",10) == null) return;

        this.input.getStack(1).decrement(this.input.getStack(0).getNbt().getList("Trim",10).size());
        this.input.getStack(0).decrement(1);

        this.context.run((world, pos) -> {
            BlockState blockState = world.getBlockState(pos);
            if (!player.getAbilities().creativeMode && blockState.isIn(BlockTags.ANVIL) && player.getRandom().nextFloat() < 0.12F) {
                BlockState blockState2 = AnvilBlock.getLandingState(blockState);
                if (blockState2 == null) {
                    world.removeBlock(pos, false);
                    world.syncWorldEvent(1029, pos, 0);
                } else {
                    world.setBlockState(pos, blockState2, 2);
                    world.syncWorldEvent(1030, pos, 0);
                }
            } else {
                world.syncWorldEvent(1030, pos, 0);
            }
        });
        ci.cancel();
    }

    @Inject(method = "canTakeOutput", at = @At("HEAD"), cancellable = true)
    public void canTakeOutput(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> cir) {
        ItemStack itemStack1 = this.input.getStack(0);
        ItemStack itemStack2 = this.input.getStack(1);

        if (itemStack1.isEmpty() || itemStack2.isEmpty() || itemStack1.getNbt() == null) return;
        if (!(itemStack2.isOf(Items.FLINT)) || itemStack1.getNbt().getList("Trim",10) == null) return;

        if (itemStack1.getNbt().getList("Trim",10).size() > itemStack2.getCount()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
        else {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
