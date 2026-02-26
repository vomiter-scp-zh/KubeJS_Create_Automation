package com.vomiter.kjscauto.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.vomiter.kjscauto.bindings.event.KJSCAutoEvents;
import com.vomiter.kjscauto.machine.BlockDestroyEventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = BlockBreakingKineticBlockEntity.class, remap = false)
public abstract class BlockBreakingKineticBlockEntityMixin  extends KineticBlockEntity {

    @Shadow
    protected BlockPos breakingPos;
    @Shadow protected int destroyProgress;
    @Shadow protected int ticksUntilNextProgress;

    @Shadow
    protected int breakerId;

    public BlockBreakingKineticBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Inject(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/base/BlockBreakingKineticBlockEntity;onBlockBroken(Lnet/minecraft/world/level/block/state/BlockState;)V"
        ),
        cancellable = true
    )
    private void kjscauto$beforeBreak(CallbackInfo ci, @Local(name = "stateToBreak") BlockState stateToBreak, @Local(name = "breakSpeed") float breakSpeed, @Local(name = "blockHardness") float blockHardness) {

        BlockBreakingKineticBlockEntity self = (BlockBreakingKineticBlockEntity) (Object) this;
        if (self.getLevel() == null || self.getLevel().isClientSide()) return;

        // 建立並廣播 KJS event
        var event = new BlockDestroyEventJS(self.getLevel(), self, breakingPos, stateToBreak, breakSpeed, blockHardness, blockHardness);
        if(KJSCAutoEvents.BLOCK_DESTROY.hasListeners()){
            KJSCAutoEvents.BLOCK_DESTROY.post(ScriptType.SERVER, event);
            KJSCAutoEvents.BLOCK_DESTROY.post(ScriptType.CLIENT, event);
        }

        if (event.kjs$isCancelled()) {
            // 取消破壞：清掉破壞進度，避免下一 tick 又立刻撞回來造成卡動畫
            destroyProgress = 0;
            ticksUntilNextProgress = -1;
            self.getLevel().destroyBlockProgress(breakerId, breakingPos, -1); // 若你有 breakerId 就用 breakerId
            ci.cancel();
            return;
        }
    }
}