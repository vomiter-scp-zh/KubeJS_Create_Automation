package com.vomiter.kjscauto.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.vomiter.kjscauto.bindings.event.KJSCAutoEvents;
import com.vomiter.kjscauto.machine.MechanicalArmCollectEventJS;
import com.vomiter.kjscauto.machine.MechanicalArmDepositEventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ArmBlockEntity.class, remap = false)
public abstract class ArmBlockEntityMixin {
    @Shadow
    ItemStack heldItem;

    @WrapOperation(method = "collectItem", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/mechanicalArm/ArmBlockEntity;getDistributableAmount(Lcom/simibubi/create/content/kinetics/mechanicalArm/ArmInteractionPoint;I)I"))
    private int kjscauto$collectItem(
            ArmBlockEntity instance,
            ArmInteractionPoint armInteractionPoint,
            int i,
            Operation<Integer> original
    ){
        var itemToCollect = armInteractionPoint.extract(i, true);
        var originalResult = original.call(instance, armInteractionPoint, i);
        if (originalResult != 0){
            var event = new MechanicalArmCollectEventJS((ArmBlockEntity)(Object)this, armInteractionPoint, heldItem, itemToCollect);
            if (KJSCAutoEvents.ARM_COLLECT.hasListeners()) {
                KJSCAutoEvents.ARM_COLLECT.post(ScriptType.SERVER, event);
                KJSCAutoEvents.ARM_COLLECT.post(ScriptType.CLIENT, event);
            }
            if (event.kjs$isCancelled()) return 0;
        }
        return originalResult;
    }

    @WrapOperation(method = "depositItem", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/mechanicalArm/ArmInteractionPoint;insert(Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack kjscauto$depositItem(
            ArmInteractionPoint interactionPoint, ItemStack stack, boolean simulate, Operation<ItemStack> original){
        var originalResultSim = original.call(interactionPoint, stack, true);
        if (originalResultSim.getCount() != stack.getCount()){
            var event = new MechanicalArmDepositEventJS((ArmBlockEntity)(Object)this, interactionPoint, heldItem, stack);
            if (KJSCAutoEvents.ARM_DEPOSIT.hasListeners()) {
                KJSCAutoEvents.ARM_DEPOSIT.post(ScriptType.SERVER, event);
                KJSCAutoEvents.ARM_DEPOSIT.post(ScriptType.CLIENT, event);
            }
            if(event.kjs$isCancelled()) return ItemStack.EMPTY;
            return event.getItemToInteract();
        }
        return original.call(interactionPoint, stack, simulate);
    }

}
