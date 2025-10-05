package org.august.vulkanlight.client.light.model;
import net.minecraft.util.math.Vec3d;

public class LightSource {
    private final Vec3d pos;
    private final int level;

    public LightSource(Vec3d pos, int level) {
        this.pos = pos;
        this.level = level;
    }

    public Vec3d getPos() {
        return pos;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return String.format("LightSource{pos=(%.1f, %.1f, %.1f), level=%d}",
                pos.x, pos.y, pos.z, level);
    }
}