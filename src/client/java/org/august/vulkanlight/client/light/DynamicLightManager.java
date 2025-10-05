package org.august.vulkanlight.client.light;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.august.vulkanlight.client.light.model.LightSource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class DynamicLightManager {

    private static final Map<Entity, LightSource> DYNAMIC_LIGHTS = new ConcurrentHashMap<>();

    public void updatePlayerLight(PlayerEntity player) {
        int lightLevel = getLightLevelFromPlayer(player);

        if (lightLevel > 0) {
            DYNAMIC_LIGHTS.put(player, new LightSource(player.getPos(), lightLevel));
        } else {
            DYNAMIC_LIGHTS.remove(player);
        }
    }

    public int getLightLevel(Entity entity) {
        LightSource light = DYNAMIC_LIGHTS.get(entity);
        return light != null ? light.getLevel() : 0;
    }

    public void clear() {
        DYNAMIC_LIGHTS.clear();
    }

    public void cleanupInvalidLights(World currentWorld) {
        DYNAMIC_LIGHTS.entrySet().removeIf(entry ->
                entry.getKey().isRemoved() || entry.getKey().getWorld() != currentWorld
        );
    }

    private int getLightLevelFromPlayer(PlayerEntity player) {
        int lightLevel = getLightLevelFromItem(player.getMainHandStack());
        if (lightLevel > 0) return lightLevel;
        return getLightLevelFromItem(player.getOffHandStack());
    }

    private int getLightLevelFromItem(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        return LightItems.getLightLevel(stack.getItem());
    }


    public static float getDynamicLightLevel(BlockPos pos) {
        if (DYNAMIC_LIGHTS.isEmpty()) {
            return 0.0f;
        }

        float maxLight = 0;

        for (LightSource lightSource : DYNAMIC_LIGHTS.values()) {
            float effectiveLight = LightCalculator.calculateLightAt(lightSource, pos);

            if (effectiveLight > maxLight) {
                maxLight = effectiveLight;
            }
        }

        return LightCalculator.clampLightLevel(maxLight);
    }
}