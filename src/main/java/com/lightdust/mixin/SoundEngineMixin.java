package com.lightdust.mixin;

import com.lightdust.client.particle.DustParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {

    @Inject(method = "play", at = @At("HEAD"))
    private void lightdust$onPlay(SoundInstance sound, CallbackInfoReturnable<?> cir) {
        if (sound != null && sound.getLocation() != null) {
            String soundPath = sound.getLocation().getPath();
            if (soundPath.contains("explode") || soundPath.contains("warden.roar") || soundPath.contains("sonic_boom")) {
                DustParticle.LOUD_NOISE_POS = new Vec3(sound.getX(), sound.getY(), sound.getZ());
                if (Minecraft.getInstance().level != null) {
                    DustParticle.LOUD_NOISE_TICK = Minecraft.getInstance().level.getGameTime();
                }
            }
        }
    }
}
