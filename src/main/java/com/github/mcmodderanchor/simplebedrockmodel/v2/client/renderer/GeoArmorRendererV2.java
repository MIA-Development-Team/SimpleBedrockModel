package com.github.mcmodderanchor.simplebedrockmodel.v2.client.renderer;

import com.github.mcmodderanchor.simplebedrockmodel.v2.client.renderer.BedrockModelRenderTypes;
import com.github.mcmodderanchor.simplebedrockmodel.v2.client.renderer.ICustomArmorRenderer;
import com.github.mcmodderanchor.simplebedrockmodel.v2.client.renderer.IFPArmorHandRenderer;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.runtime.BoneState;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.runtime.TreeArmorModelInstance;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.tree.TreeBedrockModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 基于 Tree 模型的实例盔甲渲染器。
 */
public class GeoArmorRendererV2 extends HumanoidModel<HumanoidRenderState> implements IFPArmorHandRenderer, ICustomArmorRenderer {
    protected final TreeBedrockModel model;
    protected final TreeArmorModelInstance instance;
    private final EquipmentSlot armorSlot;
    private final Identifier texture;

    @Nullable
    protected LivingEntity livingEntity;
    @Nullable
    protected ItemStack itemStack;
    @Nullable
    protected EquipmentSlot equipmentSlot;
    @Nullable
    protected HumanoidModel<?> original;

    public GeoArmorRendererV2(TreeBedrockModel model, EquipmentSlot armorSlot, Identifier texture) {
        this(model, new TreeArmorModelInstance(model), armorSlot, texture);
    }

    public GeoArmorRendererV2(TreeBedrockModel model, TreeArmorModelInstance instance, EquipmentSlot armorSlot, Identifier texture) {
        super(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_ARMOR.chest()));
        this.armorSlot = armorSlot;
        this.model = model;
        this.instance = instance;
        this.texture = texture;
    }

    public void preparePose(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
        this.instance.preparePose(livingEntity, itemStack, equipmentSlot, original);
        this.livingEntity = livingEntity;
        this.itemStack = itemStack;
        this.equipmentSlot = equipmentSlot;
        this.original = original;
    }

    public void scaleModelForBaby(PoseStack poseStack, LivingEntity livingEntity, float partialTick, EquipmentSlot slot,
                                  HumanoidModel<?> original) {
        if (!livingEntity.isBaby()) {
            return;
        }
        poseStack.scale(0.5F, 0.5F, 0.5F);
        poseStack.translate(0, slot == EquipmentSlot.HEAD ? 1.5F : 1.0F, 0);
    }

    @Override
    public void submitArmor(PoseStack poseStack, OrderedSubmitNodeCollector collector, RenderType renderType,
                            int packedLight, int packedOverlay, int color) {
        this.instance.preparePose(this.armorSlot, this);
        float red = ARGB.red(color) / 255.0F;
        float green = ARGB.green(color) / 255.0F;
        float blue = ARGB.blue(color) / 255.0F;
        float alpha = ARGB.alpha(color) / 255.0F;

        collector.submitCustomGeometry(poseStack, renderType, (pose, consumer) ->
                this.model.renderBoneTree(this.instance, stackFrom(pose), consumer, packedLight, packedOverlay,
                        red, green, blue, alpha, true, false));
        collector.submitCustomGeometry(poseStack, BedrockModelRenderTypes.polyMeshCutout(this.texture), (pose, consumer) ->
                this.model.renderBoneTree(this.instance, stackFrom(pose), consumer, packedLight, packedOverlay,
                        red, green, blue, alpha, false, false));
    }

    private static PoseStack stackFrom(PoseStack.Pose pose) {
        PoseStack stack = new PoseStack();
        stack.last().set(pose);
        return stack;
    }

    @Override
    public void renderArmorToBuffer(PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay,
                                    float red, float green, float blue, float alpha) {
        float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        // 26.1 的 ClientHooks 已把当前原版 HumanoidModel 姿态复制到 this。
        this.instance.preparePose(this.armorSlot, this);

        poseStack.pushPose();
        if (this.livingEntity != null && this.equipmentSlot != null && this.original != null) {
            scaleModelForBaby(poseStack, this.livingEntity, partialTick, this.equipmentSlot, this.original);
        }
        this.instance.renderToBuffer(
                poseStack,
                bufferSource,
                getRenderType(this.texture),
                BedrockModelRenderTypes.polyMeshCutout(this.texture),
                light,
                overlay,
                red,
                green,
                blue,
                alpha,
                false
        );
        poseStack.popPose();
    }

    public void afterRender(PoseStack poseStack, VertexConsumer buffer, int light, int overlay,
                            float red, float green, float blue, float alpha) {
        this.livingEntity = null;
        this.itemStack = null;
        this.equipmentSlot = null;
        this.original = null;
    }

    @Override
    public void renderFirstPersonArmorArm(@NotNull AbstractClientPlayer player, @NotNull HumanoidArm arm, @NotNull PoseStack poseStack,
                                          @NotNull SubmitNodeCollector collector, int packedLight) {
        this.instance.preparePose(EquipmentSlot.CHEST, this);
        BoneState armBone = arm == HumanoidArm.RIGHT
                ? this.instance.getArmorRightArm()
                : this.instance.getArmorLeftArm();
        if (armBone == null) {
            return;
        }

        collector.submitCustomGeometry(poseStack, getRenderType(getTexture()), (pose, consumer) ->
                renderFirstPersonArmPass(pose, consumer, armBone, packedLight, true));
        collector.submitCustomGeometry(poseStack, BedrockModelRenderTypes.polyMeshCutout(getTexture()), (pose, consumer) ->
                renderFirstPersonArmPass(pose, consumer, armBone, packedLight, false));
    }

    private void renderFirstPersonArmPass(PoseStack.Pose pose, VertexConsumer consumer, BoneState armBone,
                                          int packedLight, boolean quadsPass) {
        PoseStack stack = stackFrom(pose);
        stack.mulPose(this.instance.getGlobalTransform(armBone.parentIndex()));
        this.model.renderBone(this.instance, armBone.index(), stack, consumer, packedLight, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F, quadsPass, true);
    }

    public RenderType getRenderType(Identifier texture) {
        return RenderTypes.armorCutoutNoCull(texture);
    }

    public Identifier getTexture() {
        return this.texture;
    }

    public TreeBedrockModel getModel() {
        return this.model;
    }

    public TreeArmorModelInstance getInstance() {
        return this.instance;
    }

    public EquipmentSlot getArmorSlot() {
        return this.armorSlot;
    }

    @Nullable
    public EquipmentSlot getCurrentSlot() {
        return this.equipmentSlot;
    }
}
