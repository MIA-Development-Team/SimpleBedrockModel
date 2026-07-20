package example.client.render.blockentity;

import com.github.mcmodderanchor.simplebedrockmodel.v2.client.renderer.BedrockModelRenderTypes;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.runtime.TreeModelInstance;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.tree.TreeBedrockModel;
import com.github.mcmodderanchor.simplebedrockmodel.v2.resource.BedrockAnimationResources;
import com.github.mcmodderanchor.simplebedrockmodel.v2.resource.BedrockModelResources;
import com.maydaymemory.mae.basic.ArrayPoseBuilder;
import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.basic.ZYXBoneTransformFactory;
import com.maydaymemory.mae.blend.EulerAdditiveBlender;
import com.maydaymemory.mae.blend.SimpleEulerAdditiveBlender;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import example.animation.TestBlockAnimationContext;
import example.block.blockentity.TestBlockEntity;
import example.client.render.BedrockRenderSubmitter;
import example.resource.KnownResources;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.WeakHashMap;

/** TreeModelInstance 方块实体渲染和双动画平滑切换示例。 */
public final class TreeTestBlockEntityRenderer implements BlockEntityRenderer<TestBlockEntity, TreeTestBlockEntityRenderer.State> {
    private static final EulerAdditiveBlender BLENDER =
            new SimpleEulerAdditiveBlender(new ZYXBoneTransformFactory(), ArrayPoseBuilder::new);

    private final WeakHashMap<TestBlockEntity, TreeModelInstance> instances = new WeakHashMap<>();
    private TreeBedrockModel model;

    public TreeTestBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(TestBlockEntity blockEntity, State state, float partialTicks,
                                   Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        ensureLoaded();
        if (model == null) {
            state.instance = null;
            return;
        }
        TreeModelInstance instance = instances.computeIfAbsent(blockEntity, ignored -> model.createInstance());
        blockEntity.getAnimationInstance().renderTick();
        Pose animationPose = blockEntity.getAnimationInstance().getStateMachine().getPose();
        instance.resetPose();
        if (animationPose != null) {
            instance.applyPose(BLENDER.blend(instance.getBindPose(), animationPose));
        }
        state.instance = instance;
        state.facing = blockEntity.getBlockState().hasProperty(BlockStateProperties.HORIZONTAL_FACING)
                ? blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING)
                : Direction.NORTH;
    }

    private void ensureLoaded() {
        if (model != null) {
            return;
        }
        model = BedrockModelResources.getInstance().getTreeModel(KnownResources.TEST);
        var animationFile = BedrockAnimationResources.getInstance().getAnimationFile(KnownResources.TEST);
        if (model != null && animationFile != null) {
            TestBlockAnimationContext.initialize(animationFile, model);
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.instance == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.facing.toYRot()));
        BedrockRenderSubmitter.submit(state.instance, poseStack, collector,
                RenderTypes.entityCutout(KnownResources.TEST_TEXTURE),
                BedrockModelRenderTypes.polyMeshCutout(KnownResources.TEST_TEXTURE),
                state.lightCoords, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    public static final class State extends BlockEntityRenderState {
        private @Nullable TreeModelInstance instance;
        private Direction facing = Direction.NORTH;
    }
}
