package io.github.apfelrauber.stacked_trims.mixin;

import io.github.apfelrauber.stacked_trims.util.TrimRemovalHelper;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 铁砧菜单 Mixin
 * 处理使用燧石移除装饰的逻辑
 */
@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {

    protected AnvilMenuMixin(@Nullable MenuType<?> type, int syncId, Inventory playerInventory, ContainerLevelAccess context) {
        super(type, syncId, playerInventory, context);
    }

    /**
     * 拦截结果创建，处理装饰移除逻辑
     */
    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    public void onCreateResult(CallbackInfo ci) {
        ItemStack armorStack = this.inputSlots.getItem(0);
        ItemStack toolStack = this.inputSlots.getItem(1);

        // 清空结果槽
        this.resultSlots.setItem(0, ItemStack.EMPTY);

        // 检查是否可以移除装饰
        if (TrimRemovalHelper.canRemoveTrims(armorStack, toolStack)) {
            ItemStack result = TrimRemovalHelper.getRemovalResult(armorStack, toolStack);
            if (!result.isEmpty()) {
                this.resultSlots.setItem(0, result);
                this.broadcastChanges();
                ci.cancel();
            }
        }
    }

    /**
     * 拦截物品取出，处理物品消耗和铁砧损坏
     */
    @Inject(method = "onTake", at = @At("HEAD"), cancellable = true)
    public void onTakeResult(Player player, ItemStack stack, CallbackInfo ci) {
        ItemStack armorStack = this.inputSlots.getItem(0);
        ItemStack toolStack = this.inputSlots.getItem(1);

        if (TrimRemovalHelper.canRemoveTrims(armorStack, toolStack)) {
            int flintCost = TrimRemovalHelper.getFlintCost(armorStack, toolStack);

            // 消耗物品
            toolStack.shrink(flintCost);
            armorStack.shrink(1);

            // 处理铁砧损坏
            this.access.execute((level, pos) -> {
                BlockState blockState = level.getBlockState(pos);
                if (!player.getAbilities().instabuild &&
                        blockState.is(BlockTags.ANVIL) &&
                        player.getRandom().nextFloat() < 0.12F) {

                    BlockState damagedState = AnvilBlock.damage(blockState);
                    if (damagedState == null) {
                        level.removeBlock(pos, false);
                        level.levelEvent(1029, pos, 0);
                    } else {
                        level.setBlockAndUpdate(pos, damagedState);
                        level.levelEvent(1030, pos, 0);
                    }
                } else {
                    level.levelEvent(1030, pos, 0);
                }
            });

            ci.cancel();
        }
    }

    /**
     * 拦截取出权限检查
     */
    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
    public void onMayPickup(Player player, boolean present, CallbackInfoReturnable<Boolean> cir) {
        ItemStack armorStack = this.inputSlots.getItem(0);
        ItemStack toolStack = this.inputSlots.getItem(1);

        if (TrimRemovalHelper.canRemoveTrims(armorStack, toolStack)) {
            cir.setReturnValue(true);
        }
    }
}