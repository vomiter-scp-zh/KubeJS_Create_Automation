package com.vomiter.kjscauto.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.StructureTransform;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractContraptionEntity.class, remap = false)
public abstract class AbstractContraptionEntity_PrecheckDisassembleMixin {

    @Shadow @Final
    protected Contraption contraption;

    @Shadow
    protected abstract StructureTransform makeStructureTransform();

    @Inject(method = "disassemble", at = @At("HEAD"), cancellable = true)
    private void yourmod$precheckDisassemble(CallbackInfo ci) {
        AbstractContraptionEntity self = (AbstractContraptionEntity) (Object) this;

        if (self.level().isClientSide()) {
            return;
        }

        if (contraption == null) {
            return;
        }

        final StructureTransform transform = makeStructureTransform();


    }
}