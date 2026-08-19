package com.vomiter.kjscauto.custom_process;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class FanRecipeJsonObjectBuilder {
    private String type;
    private final List<Pair<Float, ItemStack>> results = new ArrayList<>();
    private Ingredient ingredient;

    @Info("This has to be identical as the name you used in fan type registration.")
    public FanRecipeJsonObjectBuilder setType(String type) {
        this.type = type;
        return this;
    }

    @Info("The ingredient of this recipe.")
    public FanRecipeJsonObjectBuilder setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
        return this;
    }

    @Info("stack: ItemStack, chance: float. The chance ranges from 0.0 to 1.0.")
    public FanRecipeJsonObjectBuilder addResult(ItemStack stack, float chance) {
        this.results.add(Pair.of(chance, stack.copy()));
        return this;
    }

    @Info("return a JsonObject for custom recipe")
    public JsonObject build() {
        if (type == null || type.isBlank()) {
            throw new IllegalStateException("Recipe type must be set before building.");
        }

        if (ingredient == null) {
            throw new IllegalStateException("Recipe ingredient must be set before building.");
        }

        JsonObject json = new JsonObject();
        json.addProperty("type", "kjscauto:" + type);

        JsonArray ingredients = new JsonArray();
        ingredients.add(encode(Ingredient.CODEC, ingredient));
        json.add("ingredients", ingredients);

        JsonArray outputResults = new JsonArray();

        for (Pair<Float, ItemStack> result : results) {
            JsonObject resultJson = encode(ItemStack.CODEC, result.getSecond()).getAsJsonObject();

            if (result.getFirst() != 1.0F) {
                resultJson.addProperty("chance", result.getFirst());
            }

            outputResults.add(resultJson);
        }

        json.add("results", outputResults);
        return json;
    }

    private static <T> JsonElement encode(com.mojang.serialization.Codec<T> codec, T value) {
        return codec.encodeStart(JsonOps.INSTANCE, value)
                .result()
                .orElseThrow(() -> new IllegalStateException("Failed to encode recipe JSON value."));
    }
}