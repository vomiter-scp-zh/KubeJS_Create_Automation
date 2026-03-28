package com.vomiter.kjscauto.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.vomiter.kjscauto.bindings.event.ContraptionEvents;
import com.vomiter.kjscauto.contraption.ContraptionBeforeDisassembleEventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@RemapPrefixForJS("kjs$")
@Mixin(value = AbstractContraptionEntity.class, remap = false)
public abstract class AbstractContraptionEntityMixin {

    @Shadow @Final
    protected Contraption contraption;

    @Shadow
    protected abstract StructureTransform makeStructureTransform();

    @Inject(
        method = "disassemble()V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/contraptions/Contraption;stop(Lnet/minecraft/world/level/Level;)V"
        ),
        cancellable = true
    )
    private void kjscauto$beforeDisassemble(CallbackInfo ci) {
        AbstractContraptionEntity self = (AbstractContraptionEntity) (Object) this;
        Level level = self.level();

        if (level == null || level.isClientSide) return;
        if (contraption == null) return;

        StructureTransform transform = makeStructureTransform();
        AABB bounds = self.getBoundingBox();

        var event = new ContraptionBeforeDisassembleEventJS(
            level,
            self,
            transform,
            bounds
        );

        if (ContraptionEvents.BEFORE_DISASSEMBLE.hasListeners()) {
            ContraptionEvents.BEFORE_DISASSEMBLE.post(ScriptType.SERVER, event);
        }

        if (event.kjs$isCancelled()) {
            ci.cancel();
        }
    }
}