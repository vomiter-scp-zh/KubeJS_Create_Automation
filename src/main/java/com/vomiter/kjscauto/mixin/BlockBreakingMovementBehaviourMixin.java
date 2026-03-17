package com.vomiter.kjscauto.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.kinetics.base.BlockBreakingMovementBehaviour;
import com.vomiter.kjscauto.bindings.event.ContraptionEvents;
import com.vomiter.kjscauto.machine.ContraptionAfterBlockDestroyEventJS;
import com.vomiter.kjscauto.machine.ContraptionBeforeBlockDestroyEventJS;
import com.vomiter.kjscauto.threadlocal.ContraptionBlockDestroyTLS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@RemapPrefixForJS("kjs$")
@Mixin(value = BlockBreakingMovementBehaviour.class, remap = false)
public class BlockBreakingMovementBehaviourMixin {
    @Unique
    private static final Long2ObjectOpenHashMap<ContraptionBeforeBlockDestroyEventJS> eventJSMap = new Long2ObjectOpenHashMap<>();
    @Unique private static long key(MovementContext ctx) { return ctx.hashCode(); }


    @Inject(
        method = "tickBreaker(Lcom/simibubi/create/content/contraptions/behaviour/MovementContext;)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/base/BlockBreakingMovementBehaviour;destroyBlock(Lcom/simibubi/create/content/contraptions/behaviour/MovementContext;Lnet/minecraft/core/BlockPos;)V"
        ),
        cancellable = true
    )
    private void kjs$beforeContraptionDestroy(MovementContext context, CallbackInfo ci) {
        if (context == null) return;
        Level world = context.world;
        if (world == null || world.isClientSide) return;
        if (context.contraption == null || context.contraption.entity == null) return;
        AbstractContraptionEntity contraptionEntity = context.contraption.entity;

        CompoundTag data = context.data;
        if (data == null || !data.contains("BreakingPos")) return;

        BlockPos breakingPos = NbtUtils.readBlockPos(data, "BreakingPos").orElseThrow();
        BlockState stateToBreak = world.getBlockState(breakingPos);
        float hardness = stateToBreak.getDestroySpeed(world, breakingPos);

        float animationSpeed = Math.abs(context.getAnimationSpeed());
        float lowerLimit = 1f / 128f;
        float breakSpeed = net.minecraft.util.Mth.clamp(animationSpeed / 500f, lowerLimit, 16f);

        BlockPos actorLocalPos = context.localPos;

        var event = new ContraptionBeforeBlockDestroyEventJS(
            world,
            contraptionEntity,
            actorLocalPos,
            context.state,
            breakingPos,
            context.getAnimationSpeed(),
            breakSpeed,
            hardness
        );

        if (ContraptionEvents.BEFORE_BLOCK_DESTROY.hasListeners()) {
            ContraptionEvents.BEFORE_BLOCK_DESTROY.post(ScriptType.SERVER, event);
        }
        eventJSMap.put(key(context), event);

        if (event.kjs$isCancelled()) {
            // 等價於 BlockBreakingMovementBehaviour.cancelStall() + 清除動畫
            int id = data.getInt("BreakerId");

            data.remove("Progress");
            data.remove("TicksUntilNextProgress");
            data.remove("BreakingPos");

            world.destroyBlockProgress(id, breakingPos, -1);
            context.stall = false;

            ci.cancel(); // 阻止 destroyBlock 發生
        }
    }

    @WrapOperation(
            method = "destroyBlock(Lcom/simibubi/create/content/contraptions/behaviour/MovementContext;Lnet/minecraft/core/BlockPos;)V",
            at = @At(
                    value="INVOKE",
                    target="Lcom/simibubi/create/foundation/utility/BlockHelper;destroyBlock(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;FLjava/util/function/Consumer;)V"
            )
    )
    private void kjscauto$wrapDestroyBlock(Level world, BlockPos pos, float chance, Consumer<ItemStack> cb,
                                           Operation<Void> original,
                                           @Local(argsOnly = true) MovementContext context,
                                           @Local(argsOnly = true) BlockPos breakingPos) {
        AbstractContraptionEntity contraptionEntity = context.contraption.entity;

        CompoundTag data = context.data;
        if (data == null || !data.contains("BreakingPos")) return;
        BlockState stateToBreak = world.getBlockState(breakingPos);
        float hardness = stateToBreak.getDestroySpeed(world, breakingPos);

        float animationSpeed = Math.abs(context.getAnimationSpeed());
        float lowerLimit = 1f / 128f;
        float breakSpeed = net.minecraft.util.Mth.clamp(animationSpeed / 500f, lowerLimit, 16f);

        BlockPos actorLocalPos = context.localPos;
        var event = new ContraptionAfterBlockDestroyEventJS(
                world,
                contraptionEntity,
                actorLocalPos,
                context.state,
                breakingPos,
                context.getAnimationSpeed(),
                breakSpeed,
                hardness
        );

        ContraptionBlockDestroyTLS.push(event);
        try {
            original.call(world, pos, chance, cb);
        } finally {
            ContraptionBlockDestroyTLS.pop();
        }
    }
}