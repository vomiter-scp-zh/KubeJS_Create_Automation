package com.vomiter.kjscauto.mixin;

import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.vomiter.kjscauto.machine.KJSCAutoRecipeIdCache;
import com.vomiter.kjscauto.threadlocal.RecipeIdTLS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BasinOperatingBlockEntity.class, remap = false)
public abstract class BasinOperatingBlockEntityMixin {

    @Shadow protected Recipe<?> currentRecipe;

    @Shadow protected abstract Object getRecipeCacheKey();

    @Inject(method = "applyBasinRecipe", at = @At("HEAD"))
    private void kjscauto$pushRecipeId(CallbackInfo ci) {
        ResourceLocation id = null;
        if (currentRecipe != null) {
            id = KJSCAutoRecipeIdCache.get(getRecipeCacheKey(), currentRecipe);
        }
        RecipeIdTLS.setRecipeId(id);
    }

    @Inject(method = "applyBasinRecipe", at = @At("RETURN"))
    private void kjscauto$clearRecipeId(CallbackInfo ci) {
        RecipeIdTLS.clearRecipeId();
    }
}