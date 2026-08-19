package com.vomiter.kjscauto.custom_process;


import dev.latvian.mods.kubejs.typings.Info;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("unused")
@Info("Helpers for creating custom fan particle effects and custom fan recipe JSON.")
public interface FanUtils {

    @Info("(primary: ParticleOptions, secondary: ParticleOptions) => FanSpawnProcessingParticles. " +
            "Creates a handler that randomly spawns the primary particle and, sometimes, the secondary particle near the fan air current.")
    static FanType.CustomFanProcessingType.FanSpawnProcessingParticles
    spawnProcessingParticles(ParticleOptions particle1, ParticleOptions particle2) {
        return (level, pos) -> {
            if (level.random.nextInt(8) != 0)
                return;
            pos = pos.add(VecHelper.offsetRandomly(Vec3.ZERO, level.random, 1)
                    .multiply(1, 0.05f, 1)
                    .normalize()
                    .scale(0.15f));
            level.addParticle(
                    particle1,
                    pos.x, pos.y + .45f, pos.z,
                    0, 0, 0
            );
            if (level.random.nextInt(2) == 0)
                level.addParticle(
                        particle2,
                        pos.x, pos.y + .25f, pos.z,
                        0, 0, 0
                );
        };
    }

    @Info("(particle: ParticleOptions) => FanSpawnProcessingParticles. " +
            "Creates a handler that randomly spawns this particle near the fan air current.")
    static FanType.CustomFanProcessingType.FanSpawnProcessingParticles
    spawnProcessingParticles(ParticleOptions particle1) {
        return (level, pos) -> {
            if (level.random.nextInt(8) != 0)
                return;
            pos = pos.add(VecHelper.offsetRandomly(Vec3.ZERO, level.random, 1)
                    .multiply(1, 0.05f, 1)
                    .normalize()
                    .scale(0.15f));
            level.addParticle(
                    particle1,
                    pos.x, pos.y + .45f, pos.z,
                    0, 0, 0
            );
        };
    }

    @Info("(color1: number, color2: number, alpha: number, particle1: ParticleOptions, chance1: number, particle2: ParticleOptions, chance2: number) => FanMorphAirFlow. " +
            "Blends randomly between two 0xRRGGBB colors, sets the air current alpha, and randomly spawns each extra particle. " +
            "Particle chances range from 0.0 to 1.0.")
    static FanType.CustomFanProcessingType.FanMorphAirFlow
        morphAirFlow(int color1, int color2, float alpha, ParticleOptions particle1, float chance1, ParticleOptions particle2, float chance2){
        return (particleAccess, random) -> {
            particleAccess.setColor(Color.mixColors(color1, color2, random.nextFloat()));
            particleAccess.setAlpha(alpha);
            if (random.nextFloat() < chance1)
                particleAccess.spawnExtraParticle(particle1, .125f);
            if (random.nextFloat() < chance2)
                particleAccess.spawnExtraParticle(particle2, .125f);
        };
    }

    @Info("(color1: number, color2: number, alpha: number, particle: ParticleOptions, chance: number) => FanMorphAirFlow. " +
            "Blends randomly between two 0xRRGGBB colors, sets the air current alpha, and randomly spawns an extra particle. " +
            "Chance ranges from 0.0 to 1.0.")
    static FanType.CustomFanProcessingType.FanMorphAirFlow
    morphAirFlow(int color1, int color2, float alpha, ParticleOptions particle1, float chance1){
        return (particleAccess, random) -> {
            particleAccess.setColor(Color.mixColors(color1, color2, random.nextFloat()));
            particleAccess.setAlpha(alpha);
            if (random.nextFloat() < chance1)
                particleAccess.spawnExtraParticle(particle1, .125f);
        };
    }

    @Info("(color1: number, color2: number, alpha: number) => FanMorphAirFlow. " +
            "Blends randomly between two 0xRRGGBB colors and sets the air current alpha.")
    static FanType.CustomFanProcessingType.FanMorphAirFlow
    morphAirFlow(int color1, int color2, float alpha){
        return (particleAccess, random) -> {
            particleAccess.setColor(Color.mixColors(color1, color2, random.nextFloat()));
            particleAccess.setAlpha(alpha);
        };
    }


    @Info("(color: number, scale: number) => DustParticleOptions. " +
            "Creates a dust particle option from a 0xRRGGBB color and particle scale.")
    static DustParticleOptions dustParticle(int color, float scale) {
        return new DustParticleOptions(new Color(color).asVectorF(), scale);
    }

    @Info("Creates a builder for a custom fan processing recipe JSON object. " +
            "Use it with ServerEvents.recipes(event => event.custom(...)).")
    static FanRecipeJsonObjectBuilder buildRecipe() {
        return new FanRecipeJsonObjectBuilder();
    }

}
