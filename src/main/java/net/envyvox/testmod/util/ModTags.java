package net.envyvox.testmod.util;

import net.envyvox.testmod.TestMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> NEEDS_ZIRCON_TOOL = tag("needs_zircon_tool");

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(new ResourceLocation(TestMod.MOD_ID, name));
        }

        private static TagKey<Block> forgeTag(String name) {
            return BlockTags.create(new ResourceLocation("forge", name));
        }
    }
}
