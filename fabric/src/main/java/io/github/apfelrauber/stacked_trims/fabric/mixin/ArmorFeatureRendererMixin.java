package io.github.apfelrauber.stacked_trims.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.apfelrauber.stacked_trims.util.ArmorTrimHelper;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>>
        extends RenderLayer<T, M> {

    @Shadow
    @Final
    private TextureAtlas armorTrimAtlas;

    @Shadow
    protected abstract void renderTrim(Holder<ArmorMaterial> armorMaterial, PoseStack poseStack,
                                       MultiBufferSource bufferSource, int packedLight,
                                       ArmorTrim trim, A model, boolean innerTexture);

    @Shadow
    protected abstract void renderGlint(PoseStack poseStack, MultiBufferSource vertexConsumers,
                                        int light, A model);

    protected ArmorFeatureRendererMixin(RenderLayerParent<T, M> context) {
        super(context);
    }

    @Inject(
            method = "renderArmorPiece",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;hasFoil()Z",
                    shift = At.Shift.BEFORE),
            cancellable = true
    )
    private void renderStackedTrims(PoseStack poseStack, MultiBufferSource bufferSource,
                                    T livingEntity, EquipmentSlot slot, int packedLight,
                                    A model, CallbackInfo ci,
                                    @Local ItemStack itemStack, @Local ArmorItem armorItem, @Local boolean innerTexture) {

        ArmorTrimHelper.getTrims(itemStack).ifPresent(armorTrims -> {
            for (ArmorTrim armorTrim : armorTrims) {
                renderTrim(armorItem.getMaterial(), poseStack, bufferSource,
                        packedLight, armorTrim, model, innerTexture);
            }
        });

        if (itemStack.hasFoil()) {
            renderGlint(poseStack, bufferSource, packedLight, model);
        }

        ci.cancel();
    }
}