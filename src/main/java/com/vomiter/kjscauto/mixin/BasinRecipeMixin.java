package com.vomiter.kjscauto.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.vomiter.kjscauto.bindings.event.KJSCAutoEvents;
import com.vomiter.kjscauto.machine.BasinOperationEventJS;
import com.vomiter.kjscauto.threadlocal.RecipeIdTLS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@RemapPrefixForJS("kjs$")
@Mixin(value = BasinRecipe.class, remap = false)
public class BasinRecipeMixin {
    @Unique
    private static final ThreadLocal<BasinOperationEventJS> kjs$eventTL = new ThreadLocal<>();

    @Inject(
            method = "apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;Z)Z",
            at = @At("HEAD")
    )
    private static void kjs$createEvent(BasinBlockEntity basin, Recipe<?> recipe, boolean test, CallbackInfoReturnable<Boolean> cir) {
        if (test) return;
        kjs$eventTL.set(new BasinOperationEventJS(basin, recipe, RecipeIdTLS.getRecipeId()));
    }

    @Inject(
            method = "apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;Z)Z",
            at = @At("RETURN")
    )
    private static void kjs$clearEvent(BasinBlockEntity basin, Recipe<?> recipe, boolean test, CallbackInfoReturnable<Boolean> cir) {
        if (test) return;
        kjs$eventTL.remove();
    }

    @WrapOperation(
            method = "apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;Z)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;acceptOutputs(Ljava/util/List;Ljava/util/List;Z)Z"
            )
    )
    private static boolean kjs$onAcceptOutputs(
            BasinBlockEntity basin,
            List<ItemStack> recipeOutputItems,
            List<FluidStack> recipeOutputFluids,
            boolean simulate,
            Operation<Boolean> original,
            @Local(argsOnly = true) Recipe<?> recipe,
            @Local(argsOnly = true) boolean testParam
    ) {
        if (testParam) {
            return original.call(basin, recipeOutputItems, recipeOutputFluids, simulate);
        }

        if (simulate) {
            var event = kjs$eventTL.get();
            if (event != null) {
                event.kjs$setOutputs(recipeOutputItems);
                event.kjs$setFluidOutputs(recipeOutputFluids);

                if (KJSCAutoEvents.BASIN_OPERATION.hasListeners()) {
                    KJSCAutoEvents.BASIN_OPERATION.post(ScriptType.SERVER, event);
                    KJSCAutoEvents.BASIN_OPERATION.post(ScriptType.CLIENT, event);
                }

                if (event.kjs$isCancelled()) {
                    return false;
                }

                recipeOutputItems.clear();
                recipeOutputItems.addAll(event.getOutputs());

                recipeOutputFluids.clear();
                recipeOutputFluids.addAll(event.getFluidOutputs());
            }
        } else {
            boolean ok = original.call(basin, recipeOutputItems, recipeOutputFluids, false);
            if (!ok) return false;

            var event = kjs$eventTL.get();
            if (event != null) {
                var inputInv = basin.getInputInventory();
                for (ItemStack toInsert : event.kjs$getInputToInsert()) {
                    if (toInsert.isEmpty()) continue;

                    ItemStack remainder = ItemHandlerHelper.insertItemStacked(inputInv, toInsert.copy(), false);
                    if (!remainder.isEmpty()) {
                        if (basin.getLevel() != null) {
                            Block.popResource(basin.getLevel(), basin.getBlockPos().above(), remainder);
                        }
                    }
                }

                var inputFluid = basin.inputTank.getCapability();
                if (inputFluid != null) {
                    for (FluidStack fs : event.kjs$getFluidToInsert()) {
                        if (fs.isEmpty()) continue;
                        int filled = inputFluid.fill(fs.copy(), IFluidHandler.FluidAction.EXECUTE);
                        int left = fs.getAmount() - filled;
                        if (left > 0) {
                            // overflow 暫時略過
                        }
                    }
                }

                basin.notifyChangeOfContents();
                basin.sendData();
            }
            return true;
        }

        return original.call(basin, recipeOutputItems, recipeOutputFluids, simulate);
    }
}