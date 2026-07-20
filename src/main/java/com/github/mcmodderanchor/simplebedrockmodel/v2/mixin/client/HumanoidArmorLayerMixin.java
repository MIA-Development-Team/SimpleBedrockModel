package com.github.mcmodderanchor.simplebedrockmodel.v2.mixin.client;

import com.github.mcmodderanchor.simplebedrockmodel.v2.client.renderer.ICustomArmorRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** 将自定义盔甲模型接入 26.1 的提交式装备渲染管线。 */
@Mixin(EquipmentLayerRenderer.class)
public class HumanoidArmorLayerMixin {

    @Redirect(
            method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"
            )
    )
    private <S> void sbm$submitCustomArmor(OrderedSubmitNodeCollector collector, Model<? super S> model, S state,
                                           PoseStack poseStack, RenderType renderType, int packedLight,
                                           int packedOverlay, int color, @Nullable TextureAtlasSprite sprite,
                                           int outlineColor,
                                           ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
        if (model instanceof ICustomArmorRenderer renderer) {
            // 自定义 Bedrock UV 不使用原版盔甲纹饰图集；纹饰 pass 在此跳过。
            if (sprite == null) {
                renderer.submitArmor(poseStack, collector, renderType, packedLight, packedOverlay, color);
            }
            return;
        }
        collector.submitModel(model, state, poseStack, renderType, packedLight, packedOverlay, color,
                sprite, outlineColor, crumblingOverlay);
    }
}
