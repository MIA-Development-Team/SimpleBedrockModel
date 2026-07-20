package example.client.render;

import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.baked.BakedBedrockModel;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.runtime.BakedModelInstance;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.runtime.TreeModelInstance;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.tree.TreeBedrockModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;

/** 将 Bedrock 的 QUADS/TRIANGLES 两个 pass 接入 26.1 的 submit 渲染管线。 */
public final class BedrockRenderSubmitter {
    private BedrockRenderSubmitter() {
    }

    public static void submit(TreeModelInstance instance, PoseStack poseStack, SubmitNodeCollector collector,
                              RenderType quads, RenderType triangles, int light, int overlay) {
        TreeBedrockModel model = instance.baseModel();
        collector.submitCustomGeometry(poseStack, quads, (pose, consumer) ->
                model.renderBoneTree(instance, stackFrom(pose), consumer, light, overlay,
                        1, 1, 1, 1, true));
        collector.submitCustomGeometry(poseStack, triangles, (pose, consumer) ->
                model.renderBoneTree(instance, stackFrom(pose), consumer, light, overlay,
                        1, 1, 1, 1, false));
    }

    public static void submit(BakedModelInstance instance, PoseStack poseStack, SubmitNodeCollector collector,
                              RenderType quads, RenderType triangles, int light, int overlay) {
        BakedBedrockModel model = instance.baseModel();
        collector.submitCustomGeometry(poseStack, quads, (pose, consumer) ->
                model.renderBoneTree(instance, stackFrom(pose), consumer, light, overlay,
                        1, 1, 1, 1, true));
        collector.submitCustomGeometry(poseStack, triangles, (pose, consumer) ->
                model.renderBoneTree(instance, stackFrom(pose), consumer, light, overlay,
                        1, 1, 1, 1, false));
    }

    private static PoseStack stackFrom(PoseStack.Pose pose) {
        PoseStack stack = new PoseStack();
        stack.last().set(pose);
        return stack;
    }
}
