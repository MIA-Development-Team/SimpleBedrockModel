package com.github.mcmodderanchor.simplebedrockmodel.v2.mixin.client;

import com.github.mcmodderanchor.simplebedrockmodel.v2.client.renderer.GuiEntityRenderContext;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.render.pip.GuiEntityRenderer;
import net.minecraft.client.renderer.state.gui.pip.GuiEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 为物品栏等 GUI 中的正交实体预览提供明确的渲染上下文。 */
@Mixin(GuiEntityRenderer.class)
public class GuiEntityRendererMixin {
    @Inject(method = "renderToTexture", at = @At("HEAD"))
    private void sbm$enterGuiEntityView(GuiEntityRenderState state, PoseStack poseStack, CallbackInfo ci) {
        GuiEntityRenderContext.enter();
    }

    @Inject(method = "renderToTexture", at = @At("RETURN"))
    private void sbm$exitGuiEntityView(GuiEntityRenderState state, PoseStack poseStack, CallbackInfo ci) {
        GuiEntityRenderContext.exit();
    }
}
