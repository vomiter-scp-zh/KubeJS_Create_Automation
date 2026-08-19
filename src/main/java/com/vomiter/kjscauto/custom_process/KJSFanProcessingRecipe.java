package com.vomiter.kjscauto.custom_process;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;

public class KJSFanProcessingRecipe extends ProcessingRecipe<KJSFanProcessingRecipe.KJSFanWrapper> {
    public KJSFanProcessingRecipe(IRecipeTypeInfo typeInfo, ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(typeInfo, params);
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 12;
    }

    @Override
    public boolean matches(@NotNull KJSFanWrapper inv, @NotNull Level level) {
        if (inv.isEmpty())
            return false;
        return ingredients.get(0)
                .test(inv.getItem(0));
    }

    public static class KJSFanWrapper extends RecipeWrapper {
        public KJSFanWrapper() {
            super(new ItemStackHandler(1));
        }
    }

}
