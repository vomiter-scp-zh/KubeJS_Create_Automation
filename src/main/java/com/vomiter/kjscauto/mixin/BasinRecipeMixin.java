package com.vomiter.kjscauto.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.vomiter.kjscauto.bindings.event.KJSCAutoEvents;
import com.vomiter.kjscauto.machine.BasinOperationEventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@RemapPrefixForJS("kjs$")
@Mixin(value = BasinRecipe.class, remap = false)
public class BasinRecipeMixin {
    @Unique private static final ThreadLocal<BasinOperationEventJS> kjs$eventTL = new ThreadLocal<>();

    @Inject(method = "apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;Z)Z",
            at = @At("HEAD"))
    private static void kjs$createEvent(BasinBlockEntity basin, Recipe<?> recipe, boolean test, CallbackInfoReturnable<Boolean> cir) {
        if (test) return;
        kjs$eventTL.set(new BasinOperationEventJS(basin, recipe));
    }

    @Inject(method = "apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;Z)Z",
            at = @At("RETURN"))
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

        // 只在 simulate==true 的那輪 post 一次：此時 outputs/fluidOutputs 已經算好
        // simulate == false 時才把額外的材料回填
        if (simulate) {
            var event = kjs$eventTL.get();
            if (event != null) {
                // 把 Create 算出的 outputs 塞進 event（copy）
                event.kjs$setOutputs(recipeOutputItems);
                event.kjs$setFluidOutputs(recipeOutputFluids);

                if (KJSCAutoEvents.BASIN_OPERATION.hasListeners()) {
                    KJSCAutoEvents.BASIN_OPERATION.post(ScriptType.SERVER, event);
                }

                // 如果腳本取消：直接讓 apply 失敗
                if (event.kjs$isCancelled()) {
                    return false;
                }

                // 把腳本修改後的 outputs 寫回 Create 原本的 list 物件（很重要）
                // 這樣後續 simulate==false 的那輪會沿用改過的結果
                recipeOutputItems.clear();
                recipeOutputItems.addAll(event.getOutputs());

                recipeOutputFluids.clear();
                recipeOutputFluids.addAll(event.getFluidOutputs());
            }
        }
        else  {
            boolean ok = original.call(basin, recipeOutputItems, recipeOutputFluids, false);
            if (!ok) return false;

            var event = kjs$eventTL.get();
            if (event != null) {
                // items -> inputInventory
                var inputInv = basin.getInputInventory(); // SmartInventory
                for (ItemStack toInsert : event.kjs$getInputToInsert()) {
                    if (toInsert.isEmpty()) continue;
                    // 盡量用 insert helper，避免你自己算 slot
                    ItemStack remainder = ItemHandlerHelper.insertItemStacked(inputInv, toInsert.copy(), false);
                    // 如果你需要處理塞不進去的 remainder：可以丟到 output 或掉落
                    // 這裡先保守：塞不進就丟在盆上方
                    if (!remainder.isEmpty()) {
                        assert basin.getLevel() != null;
                        Block.popResource(basin.getLevel(), basin.getBlockPos().above(), remainder);
                    }
                }

                // fluids -> inputTank
                var inputFluid = basin.inputTank.getCapability().orElse(null);
                if (inputFluid != null) {
                    for (FluidStack fs : event.kjs$getFluidToInsert()) {
                        if (fs.isEmpty()) continue;
                        int filled = inputFluid.fill(fs.copy(), IFluidHandler.FluidAction.EXECUTE);
                        int left = fs.getAmount() - filled;
                        if (left > 0) {
                            //warn overflow?
                        }
                    }
                }

                // 觸發 basin 與 operator 重新掃描/同步
                basin.notifyChangeOfContents();
                basin.sendData();
            }
            return true;
        }

        return original.call(basin, recipeOutputItems, recipeOutputFluids, simulate);
    }
}