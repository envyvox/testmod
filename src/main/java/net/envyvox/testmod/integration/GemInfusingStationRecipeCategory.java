package net.envyvox.testmod.integration;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.envyvox.testmod.TestMod;
import net.envyvox.testmod.block.ModBlocks;
import net.envyvox.testmod.recipe.GemInfusingStationRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GemInfusingStationRecipeCategory implements IRecipeCategory<GemInfusingStationRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(TestMod.MOD_ID, "gem_infusing");
    public static final ResourceLocation TEXTURE = new ResourceLocation(TestMod.MOD_ID, "textures/gui/gem_infusing_station_gui.png");
    private final IDrawable background;
    private final IDrawable icon;

    public GemInfusingStationRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.GEM_INFUSING_STATION.get()));
    }

    @Override
    public @NotNull RecipeType<GemInfusingStationRecipe> getRecipeType() {
        return JEITestModPlugin.INFUSING_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.literal("Gem Infusing Station");
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, GemInfusingStationRecipe recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 86, 15).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 86, 60).addItemStack(recipe.getResultItem());
    }
}