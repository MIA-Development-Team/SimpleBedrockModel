package example.client.render.item;

import com.github.mcmodderanchor.simplebedrockmodel.SimpleBedrockModel;
import com.github.mcmodderanchor.simplebedrockmodel.v2.client.animation.IFPAnimationInstance;
import com.github.mcmodderanchor.simplebedrockmodel.v2.client.renderer.AbstractGeoItemRenderer;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.BedrockModel;
import com.github.mcmodderanchor.simplebedrockmodel.v2.event.RegisterBedrockModelReloadListenerEvent;
import com.maydaymemory.mae.basic.Pose;
import example.resource.KnownResources;
import net.minecraft.resources.Identifier;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import com.mojang.blaze3d.vertex.PoseStack;
import net.neoforged.neoforge.client.event.ViewportEvent;

/** 使用 v2 第一人称管线渲染 Deagle 的最小测试渲染器。 */
public final class DeagleWithoutLevelRenderer extends AbstractGeoItemRenderer<BedrockModel> {
    private static BedrockModel model;

    @EventBusSubscriber(modid = SimpleBedrockModel.MOD_ID, value = Dist.CLIENT)
    public static final class ModelReloadListener {
        private ModelReloadListener() {
        }

        @SubscribeEvent
        public static void register(RegisterBedrockModelReloadListenerEvent event) {
            event.register(models -> {
                model = models.get(KnownResources.DEAGLE);
                if (model == null) {
                    return;
                }
                // 这两个骨骼只提供手臂挂点，不属于枪械可见几何。
                var leftHand = model.getBone("lefthand_pos");
                var rightHand = model.getBone("righthand_pos");
                if (leftHand != null) {
                    leftHand.visible = false;
                }
                if (rightHand != null) {
                    rightHand.visible = false;
                }
            });
        }
    }

    @Override
    public @Nullable Pair<BedrockModel, RenderType> getModelAndRenderType(ItemStack stack) {
        return model == null ? null : Pair.of(model, RenderTypes.entityCutout(KnownResources.DEAGLE_TEXTURE));
    }

    @Override
    public @Nullable Pair<BedrockModel, RenderType> getLodModelAndRenderType(ItemStack stack) {
        return null;
    }

    @Override
    public Identifier getSlotTexture(ItemStack stack) {
        return KnownResources.DEAGLE_TEXTURE;
    }

    @Override
    public boolean blockViewBobbing() {
        // 该测试渲染器没有枪械相机动画，保留原版视角移动。
        return false;
    }

    @Override
    public void applyLevelCameraAnimation(ViewportEvent.ComputeCameraAngles event, ItemStack stack,
                                          Quaternionf animateRot, float partialTicks) {
        // bind pose 测试不应修改玩家视角。
    }

    @Override
    public void renderFirstPerson(LocalPlayer player, ItemStack stack, ItemDisplayContext context,
                                  PoseStack poseStack, MultiBufferSource buffers, int light, float partialTick) {
        if (model == null) {
            return;
        }
        poseStack.pushPose();
        // 保持旧示例经过实机调校的 Deagle 第一人称位置。
        poseStack.translate(0.125F, -0.5F, -1.03125F);
        model.applyPose(model.getBindPose());
        model.renderToBuffer(
                poseStack,
                buffers.getBuffer(RenderTypes.entityCutout(KnownResources.DEAGLE_TEXTURE)),
                light,
                OverlayTexture.NO_OVERLAY
        );
        poseStack.popPose();
    }

    @Override
    public @Nullable IFPAnimationInstance createAnimationInstance(ItemStack stack, Entity entity) {
        return model == null ? null : new BindPoseInstance(stack, model.getBindPose());
    }

    private static final class BindPoseInstance implements IFPAnimationInstance {
        private ItemStack stack;
        private final Pose pose;
        private final Quaternionf cameraRotation = new Quaternionf();

        private BindPoseInstance(ItemStack stack, Pose pose) {
            this.stack = stack;
            this.pose = pose;
        }

        @Override public ItemStack currentItem() { return stack; }
        @Override public Pose getPose() { return pose; }
        @Override public void tick(float partialTicks) { }
        @Override public Quaternionf getCameraRotation() { return cameraRotation; }
        @Override public void setCameraRotation(Quaternionf rotation) { cameraRotation.set(rotation); }
        @Override public Pose getCachedPose() { return pose; }
        @Override public void updateItem(ItemStack stack) { this.stack = stack; }
        @Override public void triggerDraw() { }
        @Override public void triggerPutAway() { }
    }
}
