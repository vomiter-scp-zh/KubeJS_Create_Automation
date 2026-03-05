package com.vomiter.kjscauto.machine;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import dev.latvian.mods.kubejs.event.EventExit;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.level.LevelEventJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ContraptionBeforeBlockDestroyEventJS extends LevelEventJS {

    final Level level;
    final AbstractContraptionEntity contraptionEntity;
    final BlockPos actorPos;      // contraption local pos
    final BlockState actorState;  // drill/saw 本體的 state（Mounted on contraption）
    final BlockPos targetPos;

    final float animationSpeed;   // context.getAnimationSpeed()
    final float breakSpeed;       // getBlockBreakingSpeed(context)
    final float hardness;         // stateToBreak.getDestroySpeed(...)

    private boolean cancelled;
    BlockContainerJS targetBlock;

    public ContraptionBeforeBlockDestroyEventJS(Level level,
                                                AbstractContraptionEntity contraptionEntity,
                                                BlockPos actorPos,
                                                BlockState actorState,
                                                BlockPos targetPos,
                                                float animationSpeed,
                                                float breakSpeed,
                                                float hardness) {
        this.level = level;
        this.contraptionEntity = contraptionEntity;
        this.actorPos = actorPos;
        this.actorState = actorState;
        this.targetPos = targetPos;
        this.animationSpeed = animationSpeed;
        this.breakSpeed = breakSpeed;
        this.hardness = hardness;
        getTargetBlock();
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Info("The contraption entity performing the breaking action.")
    public AbstractContraptionEntity getContraptionEntity() {
        return contraptionEntity;
    }

    @Info("Local position of the actor block inside the contraption (if available).")
    public BlockPos getActorPos() {
        return actorPos;
    }

    @Info("BlockState of the actor block (e.g. create:mechanical_drill/create:mechanical_saw) mounted on the contraption.")
    public BlockState getActorState() {
        return actorState;
    }

    @Info("The position of the target block to be destroyed in the world.")
    public BlockPos getTargetPos() {
        return targetPos;
    }

    @Info("The target block to be destroyed.")
    public BlockContainerJS getTargetBlock() {
        if (targetBlock == null) targetBlock = new BlockContainerJS(getLevel(), targetPos);
        return targetBlock;
    }

    @Info("Animation speed of the contraption context (Create internal; usually based on kinetic speed).")
    public float getAnimationSpeed() {
        return animationSpeed;
    }

    @Info("Computed break speed used by the contraption breaker.")
    public float getBreakSpeed() {
        return breakSpeed;
    }

    @Info("Block hardness at the target position (destroy speed).")
    public float getHardness() {
        return hardness;
    }

    @Info("If canceled, the target block will not be destroyed.")
    @Override
    public Object cancel() throws EventExit {
        cancelled = true;
        return super.cancel();
    }

    @HideFromJS
    public boolean kjs$isCancelled() {
        return cancelled;
    }
}