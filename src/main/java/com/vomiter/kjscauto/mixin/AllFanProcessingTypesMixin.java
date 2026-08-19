package com.vomiter.kjscauto.mixin;

import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.vomiter.kjscauto.KJSCAuto;
import com.vomiter.kjscauto.bindings.event.KJSCAutoEvents;
import com.vomiter.kjscauto.custom_process.AllCustomRecipes;
import com.vomiter.kjscauto.custom_process.CustomFanRegisterEventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AllFanProcessingTypes.class, remap = false)
public class AllFanProcessingTypesMixin {
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void kjscauto$addFan(CallbackInfo ci){
        KJSCAutoEvents.CUSTOM_FAN_REGISTRY.post(ScriptType.STARTUP, new CustomFanRegisterEventJS());
        AllCustomRecipes.register(KJSCAuto.modBus);
    }
}
