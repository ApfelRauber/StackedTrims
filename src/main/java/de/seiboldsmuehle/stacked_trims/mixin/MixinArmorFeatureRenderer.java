package de.seiboldsmuehle.stacked_trims.mixin;

import de.seiboldsmuehle.stacked_trims.ArmorTrimArray;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ArmorFeatureRenderer.class)
public abstract class MixinArmorFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    public MixinArmorFeatureRenderer(FeatureRendererContext<T, M> context, SpriteAtlasTexture armorTrimsAtlas) {
        super(context);
        this.armorTrimsAtlas = armorTrimsAtlas;
    }

    @Shadow
    private final SpriteAtlasTexture armorTrimsAtlas;
    @Shadow
    private void renderTrim(ArmorMaterial armorMaterial, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, ArmorTrim armorTrim, boolean bl, A bipedEntityModel, boolean bl2, float f, float g, float h) {
        Sprite sprite = armorTrimsAtlas.getSprite(bl2 ? armorTrim.getLeggingsModelId(armorMaterial) : armorTrim.getGenericModelId(armorMaterial));
        VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(ItemRenderer.getDirectItemGlintConsumer(vertexConsumerProvider, TexturedRenderLayers.getArmorTrims(), true, bl));
        bipedEntityModel.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, f, g, h, 1.0F);
    }

    @Inject(method="renderArmor", at = @At(value="INVOKE", target = "Lnet/minecraft/world/World;getEnabledFeatures()Lnet/minecraft/resource/featuretoggle/FeatureSet;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void mixinRenderTrim(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci, ItemStack itemStack, ArmorItem armorItem, boolean bl, boolean bl2){
        if (entity.world.getEnabledFeatures().contains(FeatureFlags.UPDATE_1_20)) {
            ArmorTrimArray.getTrim(entity.world.getRegistryManager(), itemStack).ifPresent((armorTrims) -> {
                for(int i = 0; i < armorTrims.length; i++){
                    renderTrim(armorItem.getMaterial(), matrices, vertexConsumers, light, armorTrims[i], bl2, model, bl, 1.0F, 1.0F, 1.0F);
                }
            });
        }
        ci.cancel();
    }
}
