package net.envyvox.testmod.block;

import net.envyvox.testmod.TestMod;
import net.envyvox.testmod.block.custom.*;
import net.envyvox.testmod.fluid.ModFluids;
import net.envyvox.testmod.item.ModCreativeModeTab;
import net.envyvox.testmod.item.ModItems;
import net.envyvox.testmod.world.feature.tree.RedMapleTreeGrower;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
            TestMod.MOD_ID);

    public static final RegistryObject<Block> ZIRCON_BLOCK = registerBlock("zircon_block",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(6f)
                    .requiresCorrectToolForDrops()), ModCreativeModeTab.TEST_TAB);
    public static final RegistryObject<Block> ZIRCON_ORE = registerBlock("zircon_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(6f)
                    .requiresCorrectToolForDrops(),
                    UniformInt.of(3, 7)), ModCreativeModeTab.TEST_TAB);
    public static final RegistryObject<Block> DEEPSLATE_ZIRCON_ORE = registerBlock("deepslate_zircon_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(6f)
                    .requiresCorrectToolForDrops(),
                    UniformInt.of(3, 7)), ModCreativeModeTab.TEST_TAB);

    public static final RegistryObject<Block> JUMPY_BLOCK = registerBlock("jumpy_block",
            () -> new JumpyBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(6f)
                    .requiresCorrectToolForDrops()), ModCreativeModeTab.TEST_TAB);

    public static final RegistryObject<Block> ZIRCON_LAMP = registerBlock("zircon_lamp",
            () -> new ZirconLampBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(6f)
                    .lightLevel(state -> state.getValue(ZirconLampBlock.LIT) ? 15 : 0)
                    .requiresCorrectToolForDrops()), ModCreativeModeTab.TEST_TAB);

    public static final RegistryObject<Block> BLUEBERRY_CROP = BLOCKS.register("blueberry_crop",
            () -> new BlueberryCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT)));

    public static final RegistryObject<LiquidBlock> SOAP_WATER_BLOCK = BLOCKS.register("soap_water_block",
            () -> new LiquidBlock(ModFluids.SOURCE_SOAP_WATER, BlockBehaviour.Properties.copy(Blocks.WATER)));

    public static final RegistryObject<Block> GEM_INFUSING_STATION = registerBlock("gem_infusing_station",
            () -> new GemInfusingStationBlock(BlockBehaviour.Properties.of(Material.METAL)
                    .strength(6f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()), ModCreativeModeTab.TEST_TAB);
    public static final RegistryObject<Block> RED_MAPLE_LOG = registerBlock("red_maple_log",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)
                    .requiresCorrectToolForDrops()), ModCreativeModeTab.TEST_TAB);
    public static final RegistryObject<Block> RED_MAPLE_WOOD = registerBlock("red_maple_wood",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD)
                    .requiresCorrectToolForDrops()), ModCreativeModeTab.TEST_TAB);
    public static final RegistryObject<Block> STRIPPED_RED_MAPLE_LOG = registerBlock("stripped_red_maple_log",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG)
                    .requiresCorrectToolForDrops()), ModCreativeModeTab.TEST_TAB);
    public static final RegistryObject<Block> STRIPPED_RED_MAPLE_WOOD = registerBlock("stripped_red_maple_wood",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_WOOD)
                    .requiresCorrectToolForDrops()), ModCreativeModeTab.TEST_TAB);

    public static final RegistryObject<Block> RED_MAPLE_PLANKS = registerBlock("red_maple_planks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)
                    .requiresCorrectToolForDrops()) {
                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 20;
                }
            }, ModCreativeModeTab.TEST_TAB);
    public static final RegistryObject<Block> RED_MAPLE_LEAVES = registerBlock("red_maple_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES)
                    .requiresCorrectToolForDrops()) {
                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 30;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 60;
                }
            }, ModCreativeModeTab.TEST_TAB);

    public static final RegistryObject<Block> RED_MAPLE_SAPLING = registerBlock("red_maple_sapling",
            () -> new SaplingBlock(new RedMapleTreeGrower(),
                    BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING)), ModCreativeModeTab.TEST_TAB);

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block,
                                                                     CreativeModeTab tab) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, tab);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block,
                                                                            CreativeModeTab tab) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
