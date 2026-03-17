package com.vomiter.kjscauto.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.foundation.recipe.trie.RecipeTrieFinder;
import com.vomiter.kjscauto.machine.KJSCAutoRecipeIdCache;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.function.Predicate;

@Mixin(value = RecipeTrieFinder.class, remap = false)
public class RecipeTrieFinderMixin {

    @WrapOperation(
            method = "lambda$get$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/foundation/recipe/RecipeFinder;get(Ljava/lang/Object;Lnet/minecraft/world/level/Level;Ljava/util/function/Predicate;)Ljava/util/List;"
            )
    )
    private static List<RecipeHolder<? extends Recipe<?>>> kjscauto$captureRecipeIds(
            Object cacheKey,
            Level world,
            Predicate<RecipeHolder<? extends Recipe<?>>> conditions,
            Operation<List<RecipeHolder<? extends Recipe<?>>>> original
    ) {
        List<RecipeHolder<? extends Recipe<?>>> list = original.call(cacheKey, world, conditions);

        KJSCAutoRecipeIdCache.clear(cacheKey);
        for (RecipeHolder<? extends Recipe<?>> holder : list) {
            KJSCAutoRecipeIdCache.put(cacheKey, holder.value(), holder.id());
        }

        return list;
    }
}