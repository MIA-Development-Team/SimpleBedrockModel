package example.client.render.entity;

import com.github.mcmodderanchor.simplebedrockmodel.v2.client.renderer.BedrockModelRenderTypes;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.baked.BakedBedrockModel;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.runtime.BakedModelInstance;
import com.github.mcmodderanchor.simplebedrockmodel.v2.resource.BedrockAnimationResources;
import com.github.mcmodderanchor.simplebedrockmodel.v2.resource.BedrockModelResources;
import com.maydaymemory.mae.basic.ArrayPoseBuilder;
import com.maydaymemory.mae.basic.ZYXBoneTransformFactory;
import com.maydaymemory.mae.blend.EulerAdditiveBlender;
import com.maydaymemory.mae.blend.SimpleEulerAdditiveBlender;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import example.animation.ZtiAnimationContext;
import example.animation.ZtiAnimationInstance;
import example.client.render.BedrockRenderSubmitter;
import example.entity.Zti;
import example.resource.KnownResources;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

import java.util.WeakHashMap;

/** BakedModelInstance 实体渲染及 idle/running/attack 状态机示例。 */
public final class ZtiRenderer extends EntityRenderer<Zti, ZtiRenderer.State> {
    private static final EulerAdditiveBlender BLENDER =
            new SimpleEulerAdditiveBlender(new ZYXBoneTransformFactory(), ArrayPoseBuilder::new);

    private final WeakHashMap<Zti, BakedModelInstance> instances = new WeakHashMap<>();
    private final WeakHashMap<Zti, ZtiAnimationInstance> animations = new WeakHashMap<>();
    private BakedBedrockModel model;

    public ZtiRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 1.0F;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(Zti entity, State state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        ensureLoaded();
        if (model == null) {
            state.instance = null;
            return;
        }
        BakedModelInstance instance = instances.computeIfAbsent(entity, ignored -> model.createInstance());
        ZtiAnimationInstance animation = animations.computeIfAbsent(entity, ZtiAnimationInstance::new);
        animation.renderTick();
        instance.resetPose();
        instance.applyPose(BLENDER.blend(instance.getBindPose(), animation.getStateMachine().getPose()));
        state.instance = instance;
        state.bodyYaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        state.overlay = OverlayTexture.pack(0.0F, entity.hurtTime > 0 || entity.deathTime > 0);
    }

    private void ensureLoaded() {
        if (model != null) {
            return;
        }
        model = BedrockModelResources.getInstance().getBakedModel(KnownResources.ZTI_MODEL);
        var animationFile = BedrockAnimationResources.getInstance().getAnimationFile(KnownResources.ZTI_ANIMATION);
        if (model != null && animationFile != null) {
            ZtiAnimationContext.initialize(animationFile, model);
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.instance == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - state.bodyYaw));
        BedrockRenderSubmitter.submit(state.instance, poseStack, collector,
                RenderTypes.entityCutout(KnownResources.ZTI_TEXTURE),
                BedrockModelRenderTypes.polyMeshCutout(KnownResources.ZTI_TEXTURE),
                state.lightCoords, state.overlay);
        poseStack.popPose();
        super.submit(state, poseStack, collector, camera);
    }

    public static final class State extends EntityRenderState {
        private @Nullable BakedModelInstance instance;
        private float bodyYaw;
        private int overlay;
    }
}
