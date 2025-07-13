package io.github.apfelrauber.stacked_trims.mixin;

import io.github.apfelrauber.stacked_trims.util.TrimTooltipHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * 物品堆叠 Mixin
 * 处理装饰提示显示
 */
@Mixin(ItemStack.class)
public class ItemStackMixin {
    
    @Inject(method = "getTooltipLines", at = @At("RETURN"))
    public void onGetTooltipLines(Player player, TooltipFlag flag, CallbackInfoReturnable<List<Component>> cir) {
        if (player != null && player.level() != null) {
            ItemStack stack = (ItemStack) (Object) this;
            List<Component> tooltip = cir.getReturnValue();
            TrimTooltipHelper.addTrimTooltips(player.level().registryAccess(), stack, tooltip);
        }
    }
}