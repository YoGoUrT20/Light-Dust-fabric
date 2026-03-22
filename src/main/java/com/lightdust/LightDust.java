package com.lightdust;

import com.lightdust.client.particle.DustParticle;
import com.lightdust.client.particle.ActionDustParticle;
import com.lightdust.config.LightDustConfig;
import com.lightdust.config.LightDustColorConfig;
import com.lightdust.event.AmbientDustHandler;
import com.lightdust.init.ParticleInit;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class LightDust implements ClientModInitializer {
    public static final String MODID = "lightdust";

    @Override
    public void onInitializeClient() {
        LightDustConfig.load();
        LightDustColorConfig.load();

        ParticleInit.register();

        ParticleFactoryRegistry.getInstance().register(ParticleInit.DUST_PARTICLE, DustParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(ParticleInit.ACTION_DUST_PARTICLE, ActionDustParticle.Provider::new);

        ClientTickEvents.END_CLIENT_TICK.register(AmbientDustHandler::onClientTick);

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> AmbientDustHandler.clearMaps());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> AmbientDustHandler.clearMaps());
    }
}
