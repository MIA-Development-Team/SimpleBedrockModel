package example.client.render.item;

import com.github.mcmodderanchor.simplebedrockmodel.v2.client.animation.IFPAnimationInstance;
import com.github.mcmodderanchor.simplebedrockmodel.v2.client.renderer.AbstractGeoItemRendererV2;
import com.github.mcmodderanchor.simplebedrockmodel.v2.client.renderer.BedrockModelRenderTypes;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.animation.BedrockAnimation;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.runtime.TreeModelInstance;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.tree.TreeBedrockModel;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.time.AnimationClock;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.time.AnimationClocks;
import com.github.mcmodderanchor.simplebedrockmodel.v2.resource.BedrockModelResources;
import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.control.runner.AnimationContext;
import com.maydaymemory.mae.control.runner.AnimationRunner;
import com.maydaymemory.mae.control.runner.LoopingState;
import com.mojang.blaze3d.vertex.PoseStack;
import example.resource.KnownResources;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

/** Tree 模型物品：循环播放 deagle 的 idle 动画。 */
public final class TreeDeagleRenderer extends AbstractGeoItemRendererV2 {
    private final AnimationClock clock = AnimationClocks.client();
    private TreeBedrockModel model;
    private TreeModelInstance instance;
    private AnimationRunner runner;

    @Override
    public Identifier getSlotTexture(ItemStack stack) {
        return KnownResources.DEAGLE_TEXTURE;
    }

    @Override
    public boolean hasModel(ItemStack stack) {
        return ensureLoaded();
    }

    @Override
    protected void beforeRender(PoseStack poseStack, ItemDisplayContext context, ItemStack stack, float partialTicks) {
        super.beforeRender(poseStack, context, stack, partialTicks);
        if (context.firstPerson()) {
            poseStack.translate(-0.375F, 0.25F, -0.25F);
        }
    }

    @Override
    protected void renderModel(PoseStack poseStack, ItemDisplayContext context, ItemStack stack,
                               MultiBufferSource buffers, int light, int overlay, float partialTicks) {
        if (!ensureLoaded()) {
            return;
        }
        if (clock.shouldTick()) {
            runner.tick();
        }
        instance.resetPose();
        instance.applyPose(runner.evaluate());
        instance.renderToBuffer(poseStack, buffers,
                RenderTypes.entityCutout(KnownResources.DEAGLE_TEXTURE),
                BedrockModelRenderTypes.polyMeshCutout(KnownResources.DEAGLE_TEXTURE), light, overlay);
    }

    private boolean ensureLoaded() {
        if (model != null) {
            return true;
        }
        model = BedrockModelResources.getInstance().getTreeModel(KnownResources.TREE_DEAGLE);
        var animations = BedrockModelResources.getInstance().getAnimations(KnownResources.TREE_DEAGLE, KnownResources.DEAGLE);
        if (model == null || animations == null) {
            return false;
        }
        BedrockAnimation idle = animations.stream().filter(a -> a.getName().equals("idle")).findFirst().orElse(null);
        if (idle == null) {
            return false;
        }
        instance = model.createInstance();
        runner = new AnimationRunner(idle, new AnimationContext(idle.getSpecifiedEndTimeS()));
        runner.setState(new LoopingState(clock));
        return true;
    }

    @Override
    public @Nullable IFPAnimationInstance createAnimationInstance(ItemStack stack, Entity entity) {
        return ensureLoaded() ? new StaticInstance(stack, instance.getBindPose()) : null;
    }

    private static final class StaticInstance implements IFPAnimationInstance {
        private ItemStack stack;
        private final Pose pose;
        private final Quaternionf camera = new Quaternionf();

        private StaticInstance(ItemStack stack, Pose pose) { this.stack = stack; this.pose = pose; }
        @Override public ItemStack currentItem() { return stack; }
        @Override public Pose getPose() { return pose; }
        @Override public void tick(float partialTicks) { }
        @Override public Quaternionf getCameraRotation() { return camera; }
        @Override public void setCameraRotation(Quaternionf rotation) { camera.set(rotation); }
        @Override public Pose getCachedPose() { return pose; }
        @Override public void updateItem(ItemStack stack) { this.stack = stack; }
        @Override public void triggerDraw() { }
        @Override public void triggerPutAway() { }
    }
}
