package com.lightdust.init;

import com.lightdust.LightDust;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class ParticleInit {

    public static final SimpleParticleType DUST_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType ACTION_DUST_PARTICLE = FabricParticleTypes.simple();

    public static void register() {
        Registry.register(BuiltInRegistries.PARTICLE_TYPE,
            ResourceLocation.fromNamespaceAndPath(LightDust.MODID, "dust_particle"), DUST_PARTICLE);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE,
            ResourceLocation.fromNamespaceAndPath(LightDust.MODID, "action_dust_particle"), ACTION_DUST_PARTICLE);
    }
}
