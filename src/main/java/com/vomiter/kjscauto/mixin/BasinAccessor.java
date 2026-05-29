package com.vomiter.kjscauto.mixin;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = BasinBlockEntity.class, remap = false)
public interface BasinAccessor {
    @Invoker("getHeatLevel")
    BlazeBurnerBlock.HeatLevel kjscauto$getHeatLevel();
}
