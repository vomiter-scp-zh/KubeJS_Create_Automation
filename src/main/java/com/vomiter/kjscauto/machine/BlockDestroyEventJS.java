package com.vomiter.kjscauto.machine;

import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import dev.latvian.mods.kubejs.core.LevelKJS;
import dev.latvian.mods.kubejs.event.EventExit;
import dev.latvian.mods.kubejs.level.KubeLevelEvent;
import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BlockDestroyEventJS implements KubeLevelEvent {

    private final Level level;
    private final BlockBreakingKineticBlockEntity breaker;
    private final BlockPos targetPos;
    private final BlockPos pos;

    private final float kineticSpeed;
    private final float breakSpeed;
    private final float hardness;

    private boolean cancelled;
    private LevelBlock targetBlock;
    private LevelBlock block;

    public BlockDestroyEventJS(Level level,
                               BlockBreakingKineticBlockEntity breaker,
                               BlockPos targetPos,
                               BlockState state,
                               float kineticSpeed,
                               float breakSpeed,
                               float hardness) {
        this.level = level;
        this.breaker = breaker;
        this.targetPos = targetPos;
        this.kineticSpeed = kineticSpeed;
        this.breakSpeed = breakSpeed;
        this.hardness = hardness;
        this.pos = breaker.getBlockPos();
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Info("The breaker block entity (e.g. a saw/drill block entity).")
    public BlockBreakingKineticBlockEntity getBreaker() {
        return breaker;
    }

    @Info("The drill/saw block.")
    public LevelBlock getBlock(){
        if (block == null) block = ((LevelKJS)getLevel()).kjs$getBlock(breaker.getBlockPos());
        return block;
    }

    @Info("The position of the drill/saw block.")
    public BlockPos getPos(){
        return pos;
    }

    @Info("The position of the target block to be destroyed.")
    public BlockPos getTargetPos() {
        return targetPos;
    }

    @Info("The target block to be destroyed")
    public LevelBlock getTargetBlock() {
        if (targetBlock == null) targetBlock = ((LevelKJS)getLevel()).kjs$getBlock(targetPos);
        return targetBlock;
    }

    @Info("Kinetic speed of the breaker (raw, same sign as Create speed).")
    public float getKineticSpeed() {
        return kineticSpeed;
    }

    @Info("Computed break speed used by the breaker (usually abs(speed)/100).")
    public float getBreakSpeed() {
        return breakSpeed;
    }

    @Info("Block hardness at the target position (destroy speed).")
    public float getHardness() {
        return hardness;
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

    // ---- override controls (JS 可以直接呼叫) ----


    // ---- internal getters for mixin side ----
}