package com.github.mcmodderanchor.simplebedrockmodel.v2.client.compat.sodium;

import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.baked.BakedGeometryChunk;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.tree.ICube;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.tree.PolyMesh;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.neoforged.fml.ModList;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public final class SodiumCompat {
    public static final String SODIUM = "sodium";
    private static boolean sodiumInstalled = ModList.get().isLoaded(SODIUM);
    private static final ChunkVertexWriter WRITER = selectWriter();

    private SodiumCompat() {
    }

    public static void init() {
        sodiumInstalled = ModList.get().isLoaded(SODIUM);
    }

    public static boolean isSodiumInstalled() {
        return sodiumInstalled;
    }

    public static boolean writeQuads(BakedGeometryChunk chunk, VertexConsumer consumer, int lightmap, int overlay,
                                     float red, float green, float blue, float alpha, Matrix4f finalPose, Matrix3f finalNormal) {
        return writeQuads(chunk, consumer, lightmap, overlay, red, green, blue, alpha, finalPose, finalNormal, false);
    }

    public static boolean writeQuads(BakedGeometryChunk chunk, VertexConsumer consumer, int lightmap, int overlay,
                                     float red, float green, float blue, float alpha, Matrix4f finalPose, Matrix3f finalNormal,
                                     boolean skipNormalVisibilityCull) {
        if (WRITER instanceof SodiumBakedChunkWriter sodiumWriter) {
            return sodiumWriter.writeQuads(chunk, consumer, lightmap, overlay, red, green, blue, alpha, finalPose, finalNormal, skipNormalVisibilityCull);
        }
        return WRITER.writeQuads(chunk, consumer, lightmap, overlay, red, green, blue, alpha, finalPose, finalNormal);
    }

    public static boolean writeVertices(BakedGeometryChunk chunk, VertexConsumer consumer, int lightmap, int overlay,
                                        float red, float green, float blue, float alpha, Matrix4f finalPose, Matrix3f finalNormal) {
        return WRITER.writeVertices(chunk, consumer, lightmap, overlay, red, green, blue, alpha, finalPose, finalNormal);
    }

    public static boolean writeCubes(ICube[] cubes, VertexConsumer consumer, int lightmap, int overlay,
                                     float red, float green, float blue, float alpha, Matrix4f finalPose, Matrix3f finalNormal) {
        return writeCubes(cubes, consumer, lightmap, overlay, red, green, blue, alpha, finalPose, finalNormal, false);
    }

    public static boolean writeCubes(ICube[] cubes, VertexConsumer consumer, int lightmap, int overlay,
                                     float red, float green, float blue, float alpha, Matrix4f finalPose, Matrix3f finalNormal,
                                     boolean skipNormalVisibilityCull) {
        if (com.github.mcmodderanchor.simplebedrockmodel.v2.client.compat.sodium.SodiumCompat.isSodiumInstalled()) {
            return SodiumTreeWriterHolder.WRITER.writeCubes(cubes, consumer, lightmap, overlay, red, green, blue, alpha, finalPose, finalNormal, skipNormalVisibilityCull);
        }
        return false;
    }

    public static boolean writePolyMeshes(PolyMesh[] polyMeshes, VertexConsumer consumer, int lightmap, int overlay,
                                          float red, float green, float blue, float alpha, Matrix4f finalPose, Matrix3f finalNormal) {
        if (com.github.mcmodderanchor.simplebedrockmodel.v2.client.compat.sodium.SodiumCompat.isSodiumInstalled()) {
            return SodiumTreeWriterHolder.WRITER.writePolyMeshes(polyMeshes, consumer, lightmap, overlay, red, green, blue, alpha, finalPose, finalNormal);
        }
        return false;
    }

    private static ChunkVertexWriter selectWriter() {
        if (com.github.mcmodderanchor.simplebedrockmodel.v2.client.compat.sodium.SodiumCompat.isSodiumInstalled()) {
            return new SodiumBakedChunkWriter();
        }
        return ChunkVertexWriter.NOOP;
    }

    private static final class SodiumTreeWriterHolder {
        private static final SodiumTreeGeometryWriter WRITER = new SodiumTreeGeometryWriter();
    }

}
