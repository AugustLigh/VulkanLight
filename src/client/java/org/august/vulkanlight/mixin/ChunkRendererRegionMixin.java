package org.august.vulkanlight.mixin;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.august.vulkanlight.client.VulkanlightClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockRenderView.class)
public interface ChunkRendererRegionMixin {
    @Inject(
            method = "getLightLevel",
            at = @At("RETURN"),
            cancellable = true
    )
    default void addDynamicLight(net.minecraft.world.LightType type, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        float dynamicLight = VulkanlightClient.getDynamicLightLevel(pos);

        if (dynamicLight > 0) {
            int vanillaLight = cir.getReturnValue();

            if (type == net.minecraft.world.LightType.BLOCK) {
                int dynamicLightInt = (int) Math.ceil(dynamicLight);
                cir.setReturnValue(Math.max(vanillaLight, dynamicLightInt));
            }
        }
    }
}