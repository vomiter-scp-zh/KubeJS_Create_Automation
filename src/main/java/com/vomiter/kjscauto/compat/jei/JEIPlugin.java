package com.vomiter.kjscauto.compat.jei;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.vomiter.kjscauto.KJSCAuto;
import com.vomiter.kjscauto.custom_process.FanType;
import com.vomiter.kjscauto.custom_process.KJSFanProcessingRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    final List<CustomFanRecipeCategory> CATEGORIES = new ArrayList<>();
    private void loadCategories() {

        CATEGORIES.clear();
        FanType.getTypes().forEach(type -> {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                    KJSCAuto.MODID,
                    type.typeName()
            );

            var recipeType = RecipeType.<KJSFanProcessingRecipe>createRecipeHolderType(id);            var title = Component.translatable("kjscauto.jei." + type.typeName());
            var background = new EmptyBackground(178, 72);
            var icon = new DoubleItemIcon(AllItems.PROPELLER::asStack, type::getSecondaryIcon);
            var info = new CustomFanRecipeCategory.Info<>(
                    recipeType,
                    title,
                    background,
                    icon,
                    List::of,
                    List.of(AllBlocks.ENCASED_FAN::asStack)
            );
            CustomFanRecipeCategory category
                    = new CustomFanRecipeCategory(info, type);

            CATEGORIES.add(category);
        });
    }

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return KJSCAuto.modloc("jei_plugin");
    }

    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registry) {
        loadCategories();
        registry.addRecipeCategories(CATEGORIES.toArray(CreateRecipeCategory[]::new));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        CATEGORIES.forEach(createRecipeCategory -> {
            KJSCAuto.LOGGER.info("in JEI, fanType = {}", createRecipeCategory.getFanType().typeName());
            KJSCAuto.LOGGER.info("recipes = {}", Minecraft.getInstance().level.getRecipeManager()
                    .getAllRecipesFor(createRecipeCategory.getFanType().getRecipe())
            );

            registration.addRecipes(
                    createRecipeCategory.getRecipeType(),
                    Minecraft.getInstance().level.getRecipeManager()
                            .getAllRecipesFor(createRecipeCategory.getFanType().getRecipe())
            );
        });
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        CATEGORIES.forEach(c -> c.registerCatalysts(registration));
    }

}
