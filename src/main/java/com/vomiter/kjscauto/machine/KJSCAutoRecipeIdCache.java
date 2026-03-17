package com.vomiter.kjscauto.machine;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.WeakHashMap;

public final class KJSCAutoRecipeIdCache {
    private static final Map<Object, IdentityHashMap<Recipe<?>, ResourceLocation>> CACHE = new WeakHashMap<>();

    private KJSCAutoRecipeIdCache() {
    }

    public static void put(Object cacheKey, Recipe<?> recipe, ResourceLocation id) {
        if (cacheKey == null || recipe == null || id == null) {
            return;
        }
        CACHE.computeIfAbsent(cacheKey, k -> new IdentityHashMap<>()).put(recipe, id);
    }

    public static ResourceLocation get(Object cacheKey, Recipe<?> recipe) {
        if (cacheKey == null || recipe == null) {
            return null;
        }
        IdentityHashMap<Recipe<?>, ResourceLocation> map = CACHE.get(cacheKey);
        return map == null ? null : map.get(recipe);
    }

    public static void clear(Object cacheKey) {
        if (cacheKey == null) {
            return;
        }
        CACHE.remove(cacheKey);
    }

    public static void clearAll() {
        CACHE.clear();
    }
}