package com.vomiter.kjscauto.custom_process;

import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import com.vomiter.kjscauto.KJSCAuto;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class FanType {
    final String typeName;
    private ProcessingRecipeBuilder.ProcessingRecipeFactory<?> factory;
    private CustomFanProcessingType.FanIsValidAt isValidAt;
    private CustomFanProcessingType processingType;
    private CustomFanProcessingType.FanAffectEntity affectEntity;
    private CustomFanProcessingType.FanSpawnProcessingParticles spawnProcessingParticles;
    private CustomFanProcessingType.FanMorphAirFlow morphAirFlow;
    private ItemStack secondaryIcon = ItemStack.EMPTY;
    private Block catalystBlock = Blocks.AIR;
    private Fluid catalystFluid = Fluids.EMPTY;
    private Component title = Component.empty();

    public void setIsValidAt(CustomFanProcessingType.FanIsValidAt isValidAt) {
        this.isValidAt = isValidAt;
    }

    public void setProcessingType(CustomFanProcessingType processingType) {
        this.processingType = processingType;
    }

    public CustomFanProcessingType getProcessingType() {
        return processingType;
    }

    public FanType(String typeName){
        this.typeName = typeName;
    }

    static List<FanType> types = new ArrayList<>();
    public static List<FanType> getTypes(){return types;}
    public static void addType(FanType type){types.add(type);}
    public static Map<FanType, RecipeObject> getRecipeObjectMap() {
        return recipeObjectMap;
    }

    public static void putRecipeObjectMap(FanType type, RecipeObject recipe) {
        FanType.recipeObjectMap.put(type, recipe);
    }

    public void setFactory(ProcessingRecipeBuilder.ProcessingRecipeFactory<?> factory) {
        this.factory = factory;
    }

    public ProcessingRecipeBuilder.ProcessingRecipeFactory<?> getFactory() {
        return factory;
    }

    public String typeName() {
        return typeName;
    }

    public CustomFanProcessingType.FanAffectEntity getAffectEntity() {
        return affectEntity;
    }

    public void setAffectEntity(CustomFanProcessingType.FanAffectEntity affectEntity) {
        this.affectEntity = affectEntity;
    }

    public void setSpawnProcessingParticles(CustomFanProcessingType.FanSpawnProcessingParticles spawnProcessingParticles) {
        this.spawnProcessingParticles = spawnProcessingParticles;
    }

    public void setMorphAirFlow(CustomFanProcessingType.FanMorphAirFlow morphAirFlow) {
        this.morphAirFlow = morphAirFlow;
    }

    public ItemStack getSecondaryIcon() {
        return secondaryIcon;
    }

    public void setSecondaryIcon(ItemStack secondaryIcon) {
        this.secondaryIcon = secondaryIcon;
    }

    public Block getCatalystBlock() {
        return catalystBlock;
    }

    public void setCatalystBlock(Block catalystBlock) {
        this.catalystBlock = catalystBlock;
    }

    public Fluid getCatalystFluid() {
        return catalystFluid;
    }

    public void setCatalystFluid(Fluid catalystFluid) {
        this.catalystFluid = catalystFluid;
    }

    public Component getTitle() {
        return title;
    }

    public void setTitle(Component title) {
        this.title = title;
    }

    public record RecipeObject(Supplier<? extends RecipeType<KJSFanProcessingRecipe>> type, Supplier<? extends RecipeSerializer<?>> serializer){}
    private static final Map<FanType, RecipeObject> recipeObjectMap = new HashMap<>();

    public IRecipeTypeInfo getInfo(){
        return new IRecipeTypeInfo() {
            @Override
            public ResourceLocation getId() {
                return KJSCAuto.modloc(typeName);
            }

            @Override
            public <T extends RecipeSerializer<?>> T getSerializer() {
                return (T) getRecipeObjectMap().get(FanType.this).serializer.get();
            }

            @Override
            public <T extends RecipeType<?>> T getType() {
                return (T) getRecipeObjectMap().get(FanType.this).type.get();
            }
        };
    }

    public RecipeType<KJSFanProcessingRecipe> getRecipe(){
        return  recipeObjectMap.get(this).type.get();
    }

    public static class CustomFanProcessingType implements FanProcessingType {
        private static final KJSFanProcessingRecipe.KJSFanWrapper RECIPE_WRAPPER = new KJSFanProcessingRecipe.KJSFanWrapper();
        private final FanType fanType;

        public CustomFanProcessingType(FanType type){
            this.fanType = type;
        }

        @FunctionalInterface
        public interface FanIsValidAt{
            boolean isValidAt(Level level, BlockPos pos);
        }

        @Override
        public boolean isValidAt(Level level, BlockPos pos) {
            return fanType.isValidAt.isValidAt(level, pos);
        }

        @Override
        public int getPriority() {
            return 500;
        }

        @Override
        public boolean canProcess(ItemStack stack, Level level) {
            RECIPE_WRAPPER.setItem(0, stack);
            Optional<? extends Recipe<KJSFanProcessingRecipe.KJSFanWrapper>> recipe = level.getRecipeManager().getRecipeFor(
                    fanType.getRecipe(),
                    RECIPE_WRAPPER,
                    level
            );
            return recipe.isPresent();
        }

        @Override
        @Nullable
        public List<ItemStack> process(ItemStack stack, Level level) {
            RECIPE_WRAPPER.setItem(0, stack);
            Optional<? extends Recipe<KJSFanProcessingRecipe.KJSFanWrapper>> recipe = level.getRecipeManager().getRecipeFor(
                    fanType.getRecipe(),
                    RECIPE_WRAPPER,
                    level
            );
            return recipe.map(recipeWrapperRecipe -> RecipeApplier.applyRecipeOn(level, stack, recipeWrapperRecipe, true)).orElse(null);
        }

        @FunctionalInterface
        public interface FanSpawnProcessingParticles{
            void spawnProcessingParticles(Level level, Vec3 pos);
        }
        @Override
        public void spawnProcessingParticles(Level level, Vec3 pos) {
            Optional.ofNullable(fanType.spawnProcessingParticles).ifPresent(i -> i.spawnProcessingParticles(level, pos));
        }

        @FunctionalInterface
        public interface FanMorphAirFlow{
            void morphAirFlow(AirFlowParticleAccess particleAccess, RandomSource random);
        }
        @Override
        public void morphAirFlow(AirFlowParticleAccess particleAccess, RandomSource random) {
            Optional.ofNullable(fanType.morphAirFlow).ifPresent(i -> i.morphAirFlow(particleAccess, random));
        }

        @FunctionalInterface
        public interface FanAffectEntity{
            void affectEntity(Entity entity, Level level);
        }
        @Override
        public void affectEntity(Entity entity, Level level) {
            if (level.isClientSide) return;
            Optional.ofNullable(fanType.affectEntity).ifPresent(i -> i.affectEntity(entity, level));

        }

        public RecipeType<? extends Recipe<KJSFanProcessingRecipe.KJSFanWrapper>> getRecipeType() {
            return fanType.getRecipe();
        }
    }

}
