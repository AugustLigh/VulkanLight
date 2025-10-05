package org.august.vulkanlight.client.light;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.Map;


public final class LightItems {

    private static final Map<Item, Integer> LIGHT_LEVELS = Map.ofEntries(
            Map.entry(Items.TORCH, 14),
            Map.entry(Items.SOUL_TORCH, 10),
            Map.entry(Items.LANTERN, 15),
            Map.entry(Items.SOUL_LANTERN, 10),
            Map.entry(Items.GLOWSTONE, 15),
            Map.entry(Items.GLOWSTONE_DUST, 8),
            Map.entry(Items.GLOW_BERRIES, 8),
            Map.entry(Items.JACK_O_LANTERN, 15),
            Map.entry(Items.LAVA_BUCKET, 15),
            Map.entry(Items.REDSTONE_TORCH, 7),
            Map.entry(Items.BLAZE_ROD, 10),
            Map.entry(Items.BLAZE_POWDER, 7),
            Map.entry(Items.MAGMA_BLOCK, 3),
            Map.entry(Items.SHROOMLIGHT, 15),
            Map.entry(Items.OCHRE_FROGLIGHT, 15),
            Map.entry(Items.VERDANT_FROGLIGHT, 15),
            Map.entry(Items.PEARLESCENT_FROGLIGHT, 15)
    );

    private LightItems() {
        throw new AssertionError("Utility class");
    }


    public static int getLightLevel(Item item) {
        return LIGHT_LEVELS.getOrDefault(item, 0);
    }


    public static boolean isLightSource(Item item) {
        return LIGHT_LEVELS.containsKey(item);
    }
}