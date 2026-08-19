package com.vomiter.kjscauto.custom_process;

import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.vomiter.kjscauto.KJSCAuto;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.Registry;

@Info("Startup Only." +
        "Register a custom fan type for recipe and in-world interaction. " +
        "Please use CustomFanJEIEventJS for JEI display modification.")
public class CustomFanRegisterEventJS implements KubeEvent {
    public CustomFanRegisterEventJS(){}

    public static class CustomFanBuilder{
        @HideFromJS
        String name = null;

        @HideFromJS
        FanType.CustomFanProcessingType.FanIsValidAt isValidAt;

        @HideFromJS
        FanType.CustomFanProcessingType.FanAffectEntity affectEntity;

        @HideFromJS
        private FanType.CustomFanProcessingType.FanSpawnProcessingParticles spawnProcessingParticles;

        @HideFromJS
        private FanType.CustomFanProcessingType.FanMorphAirFlow morphAirFlow;


        public CustomFanBuilder(){}

        @Info("(level: Level, pos: BlockPos) => boolean. " +
                "Returns whether an air current at this position should switch to this fan processing type. "
                )
        public CustomFanBuilder isValidAt(FanType.CustomFanProcessingType.FanIsValidAt isValidAt) {
            this.isValidAt = isValidAt;
            return this;
        }

        @Info("(entity: Entity, level: Level) => void. " +
                "Called when an entity stands in the air current of this fan type.")
        public CustomFanBuilder affectEntity(FanType.CustomFanProcessingType.FanAffectEntity affectEntity) {
            this.affectEntity = affectEntity;
            return this;
        }

        @Info("(level: Level, pos: Vec3) => void. " +
                "Called to spawn processing particles. " +
                "Use FanUtil.spawnProcessingParticles(...) to create a standard particle handler.")
        public CustomFanBuilder spawnProcessingParticles(
                FanType.CustomFanProcessingType.FanSpawnProcessingParticles spawnProcessingParticles
        ) {
            this.spawnProcessingParticles = spawnProcessingParticles;
            return this;
        }

        @Info("(particleAccess: AirFlowParticleAccess, random: RandomSource) => void. " +
                "Called to modify the fan air-flow particles. " +
                "Use FanUtil.morphAirFlow(...) to create a standard particle morph handler.")
        public CustomFanBuilder morphAirFlow(FanType.CustomFanProcessingType.FanMorphAirFlow morphAirFlow) {
            this.morphAirFlow = morphAirFlow;
            return this;
        }

        public void build(){
            var fanType = new FanType(name);
            fanType.setFactory(params -> new KJSFanProcessingRecipe(fanType.getInfo(), params));
            fanType.setIsValidAt(isValidAt);
            fanType.setAffectEntity(affectEntity);
            fanType.setMorphAirFlow(morphAirFlow);
            fanType.setSpawnProcessingParticles(spawnProcessingParticles);
            FanType.addType(fanType);
            var processingType = new FanType.CustomFanProcessingType(fanType);
            Registry.register(CreateBuiltInRegistries.FAN_PROCESSING_TYPE, KJSCAuto.modloc(name), processingType);
        }

    }


    @Info("(name: string) => CustomFanBuilder. " +
            "Starts registration of a custom Create fan processing type." +
            "The name will be the registry path of this fan type and fan processing recipe type." +
            "The namespace is always 'kjscauto.'"
    )
    public CustomFanBuilder register(String name) {
        var builder = new CustomFanBuilder();
        builder.name = name;
        return builder;
    }

}
