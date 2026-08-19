package com.vomiter.kjscauto.custom_process;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.registries.BuiltInRegistries;
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
        JsonObject json = new JsonObject();

        json.addProperty("type","kjscauto:" + type);

        JsonArray ingredients = new JsonArray();
        ingredients.add(ingredient.toJson());
        json.add("ingredients", ingredients);

        JsonArray results = new JsonArray();

        for (Pair<Float, ItemStack> result : this.results) {
            float chance = result.getFirst();
            ItemStack stack = result.getSecond();

            JsonObject resultJson = new JsonObject();

            resultJson.addProperty(
                    "item",
                    BuiltInRegistries.ITEM.getKey(stack.getItem()).toString()
            );

            if (stack.getCount() != 1) {
                resultJson.addProperty("count", stack.getCount());
            }

            if (chance != 1.0F) {
                resultJson.addProperty("chance", chance);
            }

            if (stack.hasTag()) {
                resultJson.addProperty("nbt", stack.getTag().toString());
            }

            results.add(resultJson);
        }

        json.add("results", results);

        return json;
    }
}