package org.august.vulkanlight.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.vulkanmod.render.chunk.build.light.data.QuadLightData;
import net.vulkanmod.render.chunk.build.renderer.BlockRenderer;
import net.vulkanmod.render.model.quad.ModelQuadView;
import net.vulkanmod.render.vertex.TerrainBuilder;
import org.august.vulkanlight.client.VulkanlightClient;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockRenderer.class, remap = false)
public class BlockRendererMixin {

    @Unique
    private BlockPos testvulkanlight$currentBlockPos;

    @Inject(
            method = "renderBlock",
            at = @At("HEAD")
    )
    private void captureBlockPos(
            BlockState blockState,
            BlockPos blockPos,
            Vector3f pos,
            CallbackInfo ci
    ) {
        this.testvulkanlight$currentBlockPos = blockPos;
    }


    @Inject(
            method = "bufferQuad",
            at = @At("HEAD")
    )
    private void modifyQuadLightHead(
            TerrainBuilder terrainBuilder,
            Vector3f pos,
            ModelQuadView quad,
            QuadLightData quadLightData,
            CallbackInfo ci
    ) {
        if (this.testvulkanlight$currentBlockPos == null || quadLightData == null) {
            return;
        }

        for (int i = 0; i < 4; i++) {
            Vector3f vertexWorldPos = testvulkanlight$getVertexWorldPos(quad, i);
            float dynamicLight = testvulkanlight$calculateSmoothLight(vertexWorldPos);

            if (dynamicLight > 0.01f) {
                int originalLight = quadLightData.lm[i];
                int blockLight = originalLight & 0xFFFF;
                int skyLight = (originalLight >> 16) & 0xFFFF;

                int dynamicLightPacked = (int) (dynamicLight * 16.0f);
                blockLight = Math.max(blockLight, dynamicLightPacked);

                quadLightData.lm[i] = (skyLight << 16) | blockLight;
            }
        }
    }


    @Unique
    private Vector3f testvulkanlight$getVertexWorldPos(ModelQuadView quad, int vertexIndex) {
        float x = quad.getX(vertexIndex);
        float y = quad.getY(vertexIndex);
        float z = quad.getZ(vertexIndex);

        return new Vector3f(
                this.testvulkanlight$currentBlockPos.getX() + x,
                this.testvulkanlight$currentBlockPos.getY() + y,
                this.testvulkanlight$currentBlockPos.getZ() + z
        );
    }


    @Unique
    private float testvulkanlight$calculateSmoothLight(Vector3f vertexPos) {
        BlockPos basePos = new BlockPos(
                (int) Math.floor(vertexPos.x),
                (int) Math.floor(vertexPos.y),
                (int) Math.floor(vertexPos.z)
        );

        float centerLight = VulkanlightClient.getDynamicLightLevel(basePos);

        if (centerLight < 0.01f) {
            return 0.0f;
        }

        float totalLight = centerLight;
        float totalWeight = 1.0f;

        int[][] offsets = {
                {-1, 0, 0}, {1, 0, 0},
                {0, -1, 0}, {0, 1, 0},
                {0, 0, -1}, {0, 0, 1}
        };

        for (int[] offset : offsets) {
            BlockPos samplePos = basePos.add(offset[0], offset[1], offset[2]);
            float sampleLight = VulkanlightClient.getDynamicLightLevel(samplePos);

            if (sampleLight > 0.01f) {
                float weight = 0.3f;
                totalLight += sampleLight * weight;
                totalWeight += weight;
            }
        }

        return totalLight / totalWeight;
    }
}