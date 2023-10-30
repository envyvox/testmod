package net.envyvox.testmod.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.envyvox.testmod.TestMod;
import net.envyvox.testmod.recipe.GemInfusingStationRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;


@JeiPlugin
public class JEITestModPlugin implements IModPlugin {
    public static RecipeType<GemInfusingStationRecipe> INFUSING_TYPE =
            new RecipeType<>(GemInfusingStationRecipeCategory.UID, GemInfusingStationRecipe.class);

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return new ResourceLocation(TestMod.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new GemInfusingStationRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
        List<GemInfusingStationRecipe> recipes = recipeManager.getAllRecipesFor(GemInfusingStationRecipe.Type.INSTANCE);
        registration.addRecipes(INFUSING_TYPE, recipes);
    }
}
