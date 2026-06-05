package com.vomiter.kjscauto.machine;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import net.minecraft.world.item.ItemStack;

public class MechanicalArmDepositEventJS extends MechanicalArmInteractionEventJS {
    private ItemStack remainder;
    public MechanicalArmDepositEventJS(
            ArmBlockEntity armBlockEntity,
            ArmInteractionPoint interactionPoint,
            ItemStack itemStack,
            ItemStack itemToCollect,
            ItemStack remainder
            ) {
        super(
                armBlockEntity,
                interactionPoint,
                itemStack,
                itemToCollect
        );
        this.remainder = remainder;
    }

    public ItemStack getRemainder() {
        return remainder;
    }

    public void setRemainder(ItemStack remainder) {
        this.remainder = remainder;
    }
}
