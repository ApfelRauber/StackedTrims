package io.github.apfelrauber.stacked_trims.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.apfelrauber.stacked_trims.util.TrimDataHelper;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

/**
 * 人形护甲层 Mixin (客户端)
 * 处理多个装饰的渲染
 */
@OnlyIn(Dist.CLIENT)
@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>>
        extends RenderLayer<T, M> {

    @Shadow @Final
    private TextureAtlas armorTrimAtlas;

    @Shadow
    protected abstract void renderModel(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                                        ArmorItem armorItem, net.minecraft.client.model.Model model, boolean innerTexture,
                                        float red, float green, float blue, ResourceLocation armorResource);

    @Shadow
    protected abstract net.minecraft.client.model.Model getArmorModelHook(T entity, ItemStack itemStack, EquipmentSlot slot, A originalModel);

    @Shadow
    public abstract ResourceLocation getArmorResource(Entity entity, ItemStack stack, EquipmentSlot slot, String type);

    @Shadow
    protected abstract void setPartVisibility(A model, EquipmentSlot slot);

    @Shadow
    protected abstract boolean usesInnerModel(EquipmentSlot slot);

    protected HumanoidArmorLayerMixin(RenderLayerParent<T, M> context) {
        super(context);
    }

    /**
     * 完全重写 renderArmorPiece 方法以支持堆叠装饰
     */
    @Inject(method = "renderArmorPiece", at = @At("HEAD"), cancellable = true)
    private void onRenderArmorPiece(PoseStack poseStack, MultiBufferSource bufferSource, T livingEntity,
                                    EquipmentSlot slot, int packedLight, A model, CallbackInfo ci) {
        ItemStack itemStack = livingEntity.getItemBySlot(slot);
        Item item = itemStack.getItem();

        if (item instanceof ArmorItem armorItem) {
            if (armorItem.getEquipmentSlot() == slot) {
                this.getParentModel().copyPropertiesTo(model);
                this.setPartVisibility(model, slot);
                net.minecraft.client.model.Model armorModel = getArmorModelHook(livingEntity, itemStack, slot, model);
                boolean innerTexture = this.usesInnerModel(slot);

                // 渲染护甲本体
                if (armorItem instanceof DyeableLeatherItem dyeableItem) {
                    int color = dyeableItem.getColor(itemStack);
                    float red = (float)(color >> 16 & 255) / 255.0F;
                    float green = (float)(color >> 8 & 255) / 255.0F;
                    float blue = (float)(color & 255) / 255.0F;
                    this.renderModel(poseStack, bufferSource, packedLight, armorItem, armorModel, innerTexture, red, green, blue, this.getArmorResource(livingEntity, itemStack, slot, null));
                    this.renderModel(poseStack, bufferSource, packedLight, armorItem, armorModel, innerTexture, 1.0F, 1.0F, 1.0F, this.getArmorResource(livingEntity, itemStack, slot, "overlay"));
                } else {
                    this.renderModel(poseStack, bufferSource, packedLight, armorItem, armorModel, innerTexture, 1.0F, 1.0F, 1.0F, this.getArmorResource(livingEntity, itemStack, slot, null));
                }

                // 渲染装饰 - 支持堆叠装饰
                Optional<List<ArmorTrim>> stackedTrims = TrimDataHelper.getTrims(livingEntity.level().registryAccess(), itemStack);

                if (stackedTrims.isPresent() && !stackedTrims.get().isEmpty()) {
                    // 渲染所有堆叠的装饰
                    for (int i = 0; i < stackedTrims.get().size(); i++) {
                        ArmorTrim trim = stackedTrims.get().get(i);

                        // 保存当前变换矩阵
                        poseStack.pushPose();

                        // 为每个装饰添加微小的深度偏移以避免 Z-fighting
                        float depthOffset = i * 0.0001f;
                        poseStack.translate(0.0, 0.0, depthOffset);

                        // 直接实现装饰渲染逻辑，避免调用可能有递归问题的 renderTrim 方法
                        this.renderTrimDirect(armorItem.getMaterial(), poseStack, bufferSource, packedLight, trim, armorModel, innerTexture);

                        // 恢复变换矩阵
                        poseStack.popPose();
                    }
                } else {
                    // 如果没有堆叠装饰，尝试使用原版装饰
                    ArmorTrim.getTrim(livingEntity.level().registryAccess(), itemStack).ifPresent(trim -> {
                        this.renderTrimDirect(armorItem.getMaterial(), poseStack, bufferSource, packedLight, trim, armorModel, innerTexture);
                    });
                }

                // 渲染附魔光效
                if (itemStack.hasFoil()) {
                    this.renderGlintDirect(poseStack, bufferSource, packedLight, armorModel);
                }
            }
        }

        // 取消原方法执行
        ci.cancel();
    }

    /**
     * 直接实现装饰渲染逻辑，避免递归调用
     */
    private void renderTrimDirect(ArmorMaterial armorMaterial, PoseStack poseStack, MultiBufferSource bufferSource,
                                  int packedLight, ArmorTrim trim, net.minecraft.client.model.Model model, boolean innerTexture) {
        TextureAtlasSprite sprite = this.armorTrimAtlas.getSprite(innerTexture ? trim.innerTexture(armorMaterial) : trim.outerTexture(armorMaterial));
        VertexConsumer vertexConsumer = sprite.wrap(bufferSource.getBuffer(Sheets.armorTrimsSheet()));
        model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * 直接实现光效渲染逻辑
     */
    private void renderGlintDirect(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, net.minecraft.client.model.Model model) {
        model.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.armorEntityGlint()), packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}