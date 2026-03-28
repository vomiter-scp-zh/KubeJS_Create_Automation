package com.vomiter.kjscauto.mixin;

import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = Contraption.class, remap = false)
public interface ContraptionAccessor {
    @Accessor("blocks")
    Map<BlockPos, StructureTemplate.StructureBlockInfo> getBlocks();
}