package com.vomiter.kjscauto.machine;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.vomiter.kjscauto.mixin.BasinAccessor;
import dev.latvian.mods.kubejs.core.LevelKJS;
import dev.latvian.mods.kubejs.event.EventExit;
import dev.latvian.mods.kubejs.level.KubeLevelEvent;
import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import java.util.ArrayList;
import java.util.List;

public class BasinOperationEventJS implements KubeLevelEvent {
    private final BasinBlockEntity basin;
    private final Level level;
    private final BlockPos pos;
    private final Recipe<?> recipe;
    private final HeatLevel heatLevel;
    private final ResourceLocation recipeId;


    private List<ItemStack> outputs = List.of();
    private List<FluidStack> fluidOutputs = List.of();

    private boolean cancelled;
    private LevelBlock block;

    private final List<ItemStack> inputToInsert = new ArrayList<>();
    private final List<FluidStack> fluidToInsert = new ArrayList<>();

    public BasinOperationEventJS(BasinBlockEntity basin, Recipe<?> recipe, ResourceLocation recipeId) {
        this.basin = basin;
        this.level = basin.getLevel();
        this.pos = basin.getBlockPos();
        this.recipe = recipe;
        this.recipeId = recipeId;
        this.heatLevel = ((BasinAccessor)basin).getHeatLevel();
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Info("The basin block container.")
    public LevelBlock getBlock() {
        if (block == null) block = ((LevelKJS)level).kjs$getBlock(pos);
        return block;
    }

    @Info("The basin block entity.")
    public BasinBlockEntity getBasin() {
        return basin;
    }

    @Info("Basin position.")
    public BlockPos getPos() {
        return pos;
    }

    @Info("The recipe being applied.")
    public Recipe<?> getRecipe() {
        return recipe;
    }

    @Info("Current heat level as seen by the basin.")
    public HeatLevel getHeatLevel() {
        return heatLevel;
    }

    @HideFromJS
    public void kjs$setOutputs(List<ItemStack> outputs) {
        List<ItemStack> copied = new ArrayList<>(outputs.size());
        for (ItemStack s : outputs) copied.add(s.copy());
        this.outputs = copied;
    }

    @HideFromJS
    public void kjs$setFluidOutputs(List<FluidStack> fluids) {
        List<FluidStack> copied = new ArrayList<>(fluids.size());
        for (FluidStack f : fluids) copied.add(f.copy());
        this.fluidOutputs = copied;
    }

    @Info("All item outputs (this list may include crafting remainders because Create merges them into the same list).")
    public List<ItemStack> getOutputs() {
        return outputs;
    }

    @Info("All fluid outputs.")
    public List<FluidStack> getFluidOutputs() {
        return fluidOutputs;
    }

    @Override
    public Object cancel(Context cx) throws EventExit {
        cancelled = true;
        return KubeLevelEvent.super.cancel(cx);
    }

    @HideFromJS
    public boolean kjs$isCancelled() {
        return cancelled;
    }

    public void addInput(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return;
        inputToInsert.add(stack.copy());
    }

    public void addInputFluid(FluidStack fluid) {
        if (fluid == null || fluid.isEmpty()) return;
        fluidToInsert.add(fluid.copy());
    }

    public ResourceLocation getRecipeId(){
        return recipeId;
    }

    @HideFromJS
    public List<ItemStack> kjs$getInputToInsert() { return inputToInsert; }

    @HideFromJS
    public List<FluidStack> kjs$getFluidToInsert() { return fluidToInsert; }

}