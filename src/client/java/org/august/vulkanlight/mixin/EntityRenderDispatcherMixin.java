package org.august.vulkanlight.mixin;


import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.august.vulkanlight.client.VulkanlightClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Inject(
            method = "getLight",
            at = @At("RETURN"),
            cancellable = true
    )
    private void addDynamicLightToEntity(Entity entity, float tickDelta, CallbackInfoReturnable<Integer> cir) {
        BlockPos pos = entity.getBlockPos();
        float dynamicLight = VulkanlightClient.getDynamicLightLevel(pos);

        if (dynamicLight > 0) {
            int packed = cir.getReturnValue();
            int blockLight = packed & 0xFF;
            int skyLight = (packed >> 16) & 0xFF;

            int dynamicLightValue = (int) (dynamicLight * 16);
            blockLight = Math.min(240, Math.max(blockLight, dynamicLightValue));

            int newPacked = blockLight | (skyLight << 16);
            cir.setReturnValue(newPacked);
        }
    }
}
