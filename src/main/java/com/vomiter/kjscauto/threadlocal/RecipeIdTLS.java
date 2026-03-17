package com.vomiter.kjscauto.threadlocal;

import net.minecraft.resources.ResourceLocation;

public final class RecipeIdTLS {
    private static final ThreadLocal<ResourceLocation> RECIPE_ID = new ThreadLocal<>();

    private RecipeIdTLS() {
    }

    public static void setRecipeId(ResourceLocation id) {
        RECIPE_ID.set(id);
    }

    public static ResourceLocation getRecipeId() {
        return RECIPE_ID.get();
    }

    public static void clearRecipeId() {
        RECIPE_ID.remove();
    }
}