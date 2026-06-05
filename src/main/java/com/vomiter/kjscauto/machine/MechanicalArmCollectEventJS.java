package com.vomiter.kjscauto.machine;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import net.minecraft.world.item.ItemStack;

public class MechanicalArmCollectEventJS extends MechanicalArmInteractionEventJS {
    private Integer extractAmount;
    public MechanicalArmCollectEventJS(
            ArmBlockEntity armBlockEntity,
            ArmInteractionPoint interactionPoint,
            ItemStack itemStack,
            ItemStack itemToCollect,
            Integer extractAmount
            ) {
        super(
                armBlockEntity,
                interactionPoint,
                itemStack,
                itemToCollect
        );
        this.extractAmount = extractAmount;
    }

    public Integer getExtractAmount() {
        return extractAmount;
    }

    public void setExtractAmount(Integer extractAmount) {
        this.extractAmount = extractAmount;
    }
}
