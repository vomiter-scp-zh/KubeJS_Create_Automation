package com.vomiter.kjscauto.contraption;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.vomiter.kjscauto.mixin.ContraptionAccessor;
import dev.latvian.mods.kubejs.core.LevelKJS;
import dev.latvian.mods.kubejs.event.EventExit;
import dev.latvian.mods.kubejs.level.KubeLevelEvent;
import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ContraptionBeforeDisassembleEventJS implements KubeLevelEvent {

    final Level level;
    final AbstractContraptionEntity contraptionEntity;
    final StructureTransform transform;
    final AABB contraptionBounds;

    private boolean cancelled;
    private List<BlockPos> targetPositions;
    private List<LevelBlock> targetBlocks;

    public ContraptionBeforeDisassembleEventJS(Level level,
                                               AbstractContraptionEntity contraptionEntity,
                                               StructureTransform transform,
                                               AABB contraptionBounds) {
        this.level = level;
        this.contraptionEntity = contraptionEntity;
        this.transform = transform;
        this.contraptionBounds = contraptionBounds;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Info("The contraption entity that is about to disassemble.")
    public AbstractContraptionEntity getContraptionEntity() {
        return contraptionEntity;
    }

    @Info("The StructureTransform that will be used when placing blocks back into the world.")
    public StructureTransform getTransform() {
        return transform;
    }

    @Info("The world-space bounding box of the contraption at the moment before disassembly.")
    public AABB getContraptionBounds() {
        return contraptionBounds;
    }

    @Info("Minimum world position of the contraption bounds.")
    public BlockPos getMinPos() {
        return BlockPos.containing(contraptionBounds.minX, contraptionBounds.minY, contraptionBounds.minZ);
    }

    @Info("Maximum world position of the contraption bounds.")
    public BlockPos getMaxPos() {
        return BlockPos.containing(contraptionBounds.maxX, contraptionBounds.maxY, contraptionBounds.maxZ);
    }

    @Info("All target world positions this contraption will try to place blocks into during disassembly.")
    public List<BlockPos> getTargetPositions() {
        if (targetPositions == null) {
            targetPositions = new ArrayList<>();

            Contraption contraption = contraptionEntity.getContraption();
            if (contraption != null) {
                Map<BlockPos, StructureTemplate.StructureBlockInfo> blocks = ((ContraptionAccessor) contraption).getBlocks();
                for (StructureTemplate.StructureBlockInfo info : blocks.values()) {
                    targetPositions.add(transform.apply(info.pos()));
                }
            }
        }

        return targetPositions;
    }

    @Info("All world blocks at target positions this contraption will try to place into during disassembly.")
    public List<LevelBlock> getTargetBlocks() {
        if (targetBlocks == null) {
            targetBlocks = new ArrayList<>();
            for (BlockPos pos : getTargetPositions()) {
                targetBlocks.add(((LevelKJS)getLevel()).kjs$getBlock(pos));
            }
        }

        return targetBlocks;
    }

    public boolean testTargetBlock(Predicate<LevelBlock> predicate) {
        for (BlockPos pos : getTargetPositions()) {
            var block = ((LevelKJS)level).kjs$getBlock(pos);
            if(predicate.test(block)) return true;
        }
        return false;
    }

    @Info("If canceled, this contraption disassembly will not proceed.")
    @Override
    public Object cancel(Context context) throws EventExit {
        cancelled = true;
        return KubeLevelEvent.super.cancel(context);
    }

    @HideFromJS
    public boolean kjs$isCancelled() {
        return cancelled;
    }
}