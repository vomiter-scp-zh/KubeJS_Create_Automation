package com.vomiter.kjscauto.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.vomiter.kjscauto.bindings.event.ContraptionEvents;
import com.vomiter.kjscauto.machine.ContraptionBlockDestroyTLS;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(value = BlockHelper.class, remap = false)
public class BlockHelper_DropsMixin {

    @WrapOperation(
      method = "destroyBlockAs(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;FLjava/util/function/Consumer;)V",
      at = @At(
        value="INVOKE",
        target="Lnet/minecraft/world/level/block/Block;getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;"
      )
    )
    private static List<ItemStack> kjscauto$overrideDrops(BlockState state, ServerLevel level, BlockPos pos,
                                                          @Nullable BlockEntity be, @Nullable Entity entity, ItemStack tool,
                                                          Operation<List<ItemStack>> original) {

        List<ItemStack> drops = original.call(state, level, pos, be, entity, tool);
        var tls = ContraptionBlockDestroyTLS.get();
        if (tls == null) return drops;
        tls.event.kjs$setDrops(drops);
        if (ContraptionEvents.AFTER_BLOCK_DESTROY.hasListeners()) {
            ContraptionEvents.AFTER_BLOCK_DESTROY.post(ScriptType.SERVER, tls.event);
        }
        return tls.event.getDrops();
    }
}