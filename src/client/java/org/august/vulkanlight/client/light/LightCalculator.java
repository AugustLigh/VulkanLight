package org.august.vulkanlight.client.light;

import net.minecraft.util.math.BlockPos;
import org.august.vulkanlight.client.light.model.LightSource;


public final class LightCalculator {

    private static final double ATTENUATION_POWER = 2.0;

    private LightCalculator() {
        throw new AssertionError("Utility class");
    }

    public static double calculateLightRadius(int lightLevel) {
        return 3.0 + (lightLevel / 15.0) * 9.0;
    }

    public static float calculateLightAt(LightSource source, BlockPos pos) {
        double lightRadius = calculateLightRadius(source.getLevel());
        double maxDistanceSq = lightRadius * lightRadius;

        double distanceSq = source.getPos().squaredDistanceTo(
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5
        );

        if (distanceSq > maxDistanceSq) {
            return 0.0f;
        }

        double distance = Math.sqrt(distanceSq);

        double attenuation = 1.0 - (distance / lightRadius);
        attenuation = Math.max(0, Math.pow(attenuation, ATTENUATION_POWER));

        return (float) (source.getLevel() * attenuation);
    }


    public static float clampLightLevel(float light) {
        return Math.max(0, Math.min(15, light));
    }
}