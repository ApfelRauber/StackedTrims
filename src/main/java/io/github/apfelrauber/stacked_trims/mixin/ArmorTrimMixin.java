package io.github.apfelrauber.stacked_trims.mixin;

import io.github.apfelrauber.stacked_trims.config.StackedTrimsConfig;
import io.github.apfelrauber.stacked_trims.util.TrimDataHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * 护甲装饰 Mixin
 * 重定向原版装饰方法到我们的堆叠系统
 */
@Mixin(ArmorTrim.class)
public class ArmorTrimMixin {

    /**
     * 重定向装饰设置方法
     */
    @Inject(method = "setTrim", at = @At("HEAD"), cancellable = true)
    private static void onSetTrim(RegistryAccess registryAccess, ItemStack stack, ArmorTrim trim, CallbackInfoReturnable<Boolean> cir) {
        int maxStack = StackedTrimsConfig.MAX_TRIM_STACK.get();
        boolean allowDuplicates = StackedTrimsConfig.ALLOW_DUPLICATE_TRIMS.get();

        boolean success = TrimDataHelper.addTrim(registryAccess, stack, trim, maxStack, allowDuplicates);
        cir.setReturnValue(success);
    }

    /**
     * 重定向装饰获取方法
     */
    @Inject(method = "getTrim", at = @At("HEAD"), cancellable = true)
    private static void onGetTrim(RegistryAccess registryAccess, ItemStack stack, CallbackInfoReturnable<Optional<ArmorTrim>> cir) {
        Optional<ArmorTrim> lastTrim = TrimDataHelper.getLastTrim(registryAccess, stack);
        cir.setReturnValue(lastTrim);
    }
}