package net.envyvox.testmod.world.feature;

import net.envyvox.testmod.TestMod;
import net.envyvox.testmod.block.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class ModPlacedFeatures {
    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES =
            DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, TestMod.MOD_ID);

    public static final RegistryObject<PlacedFeature> RED_MAPLE_CHECKED = PLACED_FEATURES.register("red_maple_checked",
            () -> new PlacedFeature(ModConfiguredFeatures.RED_MAPLE.getHolder().get(),
                    List.of(PlacementUtils.filteredByBlockSurvival(ModBlocks.RED_MAPLE_SAPLING.get()))));

    public static final RegistryObject<PlacedFeature> RED_MAPLE_PLACED = PLACED_FEATURES.register("red_maple_placed",
            () -> new PlacedFeature(ModConfiguredFeatures.RED_MAPLE_SPAWN.getHolder().get(),
                    VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1f, 2))));

    public static void register(IEventBus eventBus) {
        PLACED_FEATURES.register(eventBus);
    }
}
