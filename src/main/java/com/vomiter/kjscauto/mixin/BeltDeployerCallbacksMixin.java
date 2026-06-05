package com.vomiter.kjscauto.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.deployer.BeltDeployerCallbacks;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.vomiter.kjscauto.bindings.event.KJSCAutoEvents;
import com.vomiter.kjscauto.machine.DeployerUseEventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Consumer;

@Mixin(value = BeltDeployerCallbacks.class, remap = false)
public class BeltDeployerCallbacksMixin {
    @Unique private static final Long2ObjectOpenHashMap<DeployerUseEventJS> eventJSMap = new Long2ObjectOpenHashMap<>();
    @Unique private static long kjscauto$key(DeployerBlockEntity be) { return be.getBlockPos().asLong(); }

    @Inject(method = "activate", at = @At("HEAD"))
    private static void kjscauto$addEventJS(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler,
                                   DeployerBlockEntity blockEntity, Recipe<?> recipe, CallbackInfo ci) {
        var eventJS = new DeployerUseEventJS(blockEntity, transported, handler, recipe);
        eventJSMap.put(kjscauto$key(blockEntity), eventJS);
    }

    @WrapOperation(
            method = "activate",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/foundation/recipe/RecipeApplier;applyRecipeOn(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/crafting/Recipe;Z)Ljava/util/List;"
            )
    )
    private static List<ItemStack> kjscauto$captureRecipeOutputs(
            Level level,
            ItemStack input,
            Recipe<?> recipe,
            boolean returnProcessingRemainder,
            Operation<List<ItemStack>> original,
            @Local(argsOnly = true, name = "arg0") TransportedItemStack transported,
            @Local(argsOnly = true, name = "arg2") DeployerBlockEntity deployer,
            @Local(argsOnly = true, name = "arg1") TransportedItemStackHandlerBehaviour handler
    ) {
        // 1) 先讓 Create 正常算出 outputs（ItemStack list）
        List<ItemStack> outputs = original.call(level, input, recipe, returnProcessingRemainder);

        // 2) 找到本次 activate 的 target
        var event = eventJSMap.get(kjscauto$key(deployer));
        if (event != null) {
            ItemStack remainder = transported.stack.copy();
            remainder.shrink(1);

            event.kjs$setRemainder(remainder);
            event.kjs$setOutputs(outputs);

            // 3) 在這裡 post：此時 outputs/remainder 都已經寫進 target
            if (KJSCAutoEvents.DEPLOYER_USE.hasListeners()) {
                KJSCAutoEvents.DEPLOYER_USE.post(ScriptType.SERVER, event);
                KJSCAutoEvents.DEPLOYER_USE.post(ScriptType.CLIENT, event);
            }

            return event.getOutputs();
        }

        return outputs;
    }


    @WrapOperation(
            method = "activate",
            at = @At(value="INVOKE", target="Lnet/minecraft/world/item/ItemStack;hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V", remap = true)
    )
    private static void kjscauto$deployerHurtAndBreak(
            ItemStack instance, int damage, LivingEntity entity, Consumer<LivingEntity> consumer,
            Operation<Void> original,
            @Local(argsOnly = true, name = "arg2") DeployerBlockEntity deployer
    ) {
        var event = eventJSMap.get(kjscauto$key(deployer));
        if (event == null) {
            original.call(instance, damage, entity, consumer);
            return;
        }
        int finalDamage = event.kjs$isDamageCancelled() ? 0 : event.getDamage();

        if (finalDamage > 0) {
            original.call(instance, finalDamage, entity, consumer);
        } else {
            // 取消扣耐久：不 call original
        }
    }

    @Inject(method = "activate", at = @At("RETURN"))
    private static void kjscauto$removeEventJS(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler, DeployerBlockEntity blockEntity, Recipe<?> recipe, CallbackInfo ci){
        eventJSMap.remove(kjscauto$key(blockEntity));
    }



}
