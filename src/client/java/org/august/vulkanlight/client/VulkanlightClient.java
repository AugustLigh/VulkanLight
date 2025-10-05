package org.august.vulkanlight.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.august.vulkanlight.client.light.DynamicLightManager;
import org.august.vulkanlight.client.renderer.ChunkRebuildScheduler;

public class VulkanlightClient implements ClientModInitializer {
    private static final int UPDATE_INTERVAL = 1;

    private final DynamicLightManager lightManager = new DynamicLightManager();
    private final ChunkRebuildScheduler chunkScheduler = new ChunkRebuildScheduler();

    private int tickCounter = 0;
    private BlockPos lastPlayerPos = null;
    private ItemStack lastMainHand = ItemStack.EMPTY;
    private ItemStack lastOffHand = ItemStack.EMPTY;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    private void onClientTick(MinecraftClient client) {
        tickCounter++;
        if (tickCounter < UPDATE_INTERVAL) return;
        tickCounter = 0;

        PlayerEntity player = client.player;
        if (player == null) {
            handlePlayerDisconnect();
            return;
        }

        updatePlayerLight(client, player);
    }

    private void handlePlayerDisconnect() {
        lightManager.clear();
        lastPlayerPos = null;
        lastMainHand = ItemStack.EMPTY;
        lastOffHand = ItemStack.EMPTY;
    }

    private void updatePlayerLight(MinecraftClient client, PlayerEntity player) {
        ItemStack currentMainHand = player.getMainHandStack();
        ItemStack currentOffHand = player.getOffHandStack();

        int oldLightLevel = lightManager.getLightLevel(player);
        lightManager.updatePlayerLight(player);
        int newLightLevel = lightManager.getLightLevel(player);

        lightManager.cleanupInvalidLights(player.getWorld());

        BlockPos currentPos = player.getBlockPos();
        boolean positionChanged = hasPositionChanged(currentPos);
        boolean lightChanged = oldLightLevel != newLightLevel;
        boolean itemChanged = hasItemChanged(currentMainHand, currentOffHand);

        if (positionChanged || lightChanged || itemChanged) {
            chunkScheduler.scheduleRebuild(client, currentPos);
            updateTrackedState(currentPos, currentMainHand, currentOffHand,
                    positionChanged, itemChanged);
        }
    }

    private boolean hasPositionChanged(BlockPos currentPos) {
        return lastPlayerPos == null || !currentPos.equals(lastPlayerPos);
    }

    private boolean hasItemChanged(ItemStack currentMainHand, ItemStack currentOffHand) {
        return !ItemStack.areEqual(lastMainHand, currentMainHand) ||
                !ItemStack.areEqual(lastOffHand, currentOffHand);
    }

    private void updateTrackedState(BlockPos pos, ItemStack mainHand, ItemStack offHand,
                                    boolean posChanged, boolean itemChanged) {
        if (posChanged) {
            lastPlayerPos = pos;
        }
        if (itemChanged) {
            lastMainHand = mainHand.copy();
            lastOffHand = offHand.copy();
        }
    }

    public static float getDynamicLightLevel(BlockPos pos) {
        return DynamicLightManager.getDynamicLightLevel(pos);
    }
}