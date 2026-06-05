package com.vomiter.kjscauto.machine;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.level.LevelEventJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MechanicalArmDepositEventJS extends MechanicalArmCollectEventJS {
    public MechanicalArmDepositEventJS(ArmBlockEntity armBlockEntity, ArmInteractionPoint interactionPoint, ItemStack itemStack, ItemStack itemToCollect) {
        super(armBlockEntity, interactionPoint, itemStack, itemToCollect);
    }
}
