package com.vomiter.kjscauto.custom_process;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.vomiter.kjscauto.KJSCAuto;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class AllCustomRecipes {
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZER_REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, KJSCAuto.MODID);
    private static final DeferredRegister<RecipeType<?>> TYPE_REGISTER = DeferredRegister.create(Registries.RECIPE_TYPE, KJSCAuto.MODID);
    static {
        FanType.getTypes().forEach(
                type -> {
                    Supplier<RecipeSerializer<?>> serializerSupplier = () -> new ProcessingRecipeSerializer<>(type.getFactory());
                    RegistryObject<RecipeType<KJSFanProcessingRecipe>> recipeType = TYPE_REGISTER.register(type.typeName(), () -> RecipeType.simple(KJSCAuto.modloc(type.typeName())));
                    var serializerType = SERIALIZER_REGISTER.register(type.typeName(), serializerSupplier);
                    FanType.putRecipeObjectMap(type, new FanType.RecipeObject(recipeType, serializerType));
                }
        );
    }
    public static void register(IEventBus bus){
        SERIALIZER_REGISTER.register(bus);
        TYPE_REGISTER.register(bus);
        KJSCAuto.LOGGER.info("KJSCauto Custom Recipes Registered.");
    }

}
