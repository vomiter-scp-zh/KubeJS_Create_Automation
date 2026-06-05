package com.vomiter.kjscauto.machine;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import dev.latvian.mods.kubejs.event.EventExit;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.level.LevelEventJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MechanicalArmInteractionEventJS extends LevelEventJS {
    private final Level level;
    private final BlockPos pos;
    private final ArmBlockEntity arm;
    private final BlockPos targetPos;
    private final ItemStack heldItem;
    private final ArmInteractionPoint target;
    private final ItemStack itemToInteract;
    private BlockContainerJS targetBlock;
    private BlockContainerJS block;
    private boolean cancelled;

    public MechanicalArmInteractionEventJS(ArmBlockEntity armBlockEntity, ArmInteractionPoint interactionPoint, ItemStack itemStack, ItemStack itemToCollect){
        level = armBlockEntity.getLevel();
        pos = armBlockEntity.getBlockPos();
        arm = armBlockEntity;
        target = interactionPoint;
        targetPos = interactionPoint.getPos();
        heldItem = itemStack;
        this.itemToInteract = itemToCollect;
    }


    @Override
    public Level getLevel() {
        return level;
    }

    public BlockPos getPos() {
        return pos;
    }

    public ArmBlockEntity getArm() {
        return arm;
    }

    public BlockPos getTargetPos() {
        return targetPos;
    }

    public ItemStack getHeldItem() {
        return heldItem;
    }

    public BlockContainerJS getTargetBlock() {
        if (targetBlock == null) {
            targetBlock = new BlockContainerJS(getLevel(), targetPos);
        }
        return targetBlock;
    }

    public BlockContainerJS getBlock() {
        if (block == null) {
            block = new BlockContainerJS(getLevel(), pos);
        }
        return block;
    }

    /*
    @Override
    public Object cancel() throws EventExit {
        cancelled = true;
        return super.cancel();
    }
     */

    @HideFromJS
    public boolean kjs$isCancelled() {
        return cancelled;
    }

    public ItemStack getItemToInteract() {
        return itemToInteract;
    }

    public ArmInteractionPoint getTarget() {
        return target;
    }
}
