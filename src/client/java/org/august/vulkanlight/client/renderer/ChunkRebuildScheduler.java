package org.august.vulkanlight.client.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;


public class ChunkRebuildScheduler {

    private static final int CHUNK_UPDATE_RADIUS = 2;

    public void scheduleRebuild(MinecraftClient client, BlockPos pos) {
        if (client.worldRenderer == null) return;

        int sectionX = ChunkSectionPos.getSectionCoord(pos.getX());
        int sectionY = ChunkSectionPos.getSectionCoord(pos.getY());
        int sectionZ = ChunkSectionPos.getSectionCoord(pos.getZ());

        for (int dx = -CHUNK_UPDATE_RADIUS; dx <= CHUNK_UPDATE_RADIUS; dx++) {
            for (int dy = -CHUNK_UPDATE_RADIUS; dy <= CHUNK_UPDATE_RADIUS; dy++) {
                for (int dz = -CHUNK_UPDATE_RADIUS; dz <= CHUNK_UPDATE_RADIUS; dz++) {
                    client.worldRenderer.scheduleBlockRenders(
                            sectionX + dx,
                            sectionY + dy,
                            sectionZ + dz
                    );
                }
            }
        }
    }

    public void scheduleRebuildForMove(MinecraftClient client, BlockPos oldPos, BlockPos newPos) {
        scheduleRebuild(client, oldPos);
        if (!oldPos.equals(newPos)) {
            scheduleRebuild(client, newPos);
        }
    }
}
