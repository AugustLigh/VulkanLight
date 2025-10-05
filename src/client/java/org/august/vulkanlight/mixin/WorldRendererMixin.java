package org.august.vulkanlight.mixin;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.august.vulkanlight.client.VulkanlightClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Inject(
            method = "getLightmapCoordinates(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/util/math/BlockPos;)I",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void addDynamicLight(BlockRenderView world, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        int packed = cir.getReturnValue();
        float dynamicLight = VulkanlightClient.getDynamicLightLevel(pos);

        if (dynamicLight > 0) {
            int blockLight = packed & 0xFF;
            int skyLight = (packed >> 16) & 0xFF;

            int dynamicLightValue = (int) (dynamicLight * 16);
            blockLight = Math.min(240, Math.max(blockLight, dynamicLightValue));

            int newPacked = blockLight | (skyLight << 16);
            cir.setReturnValue(newPacked);
        }
    }
}