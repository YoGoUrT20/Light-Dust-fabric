package com.lightdust.config;

import com.google.gson.*;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class LightDustConfig {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Path CONFIG_PATH = Path.of("config", "lightdust", "main.json");
    private static boolean loaded = false;

    public static class ConfigSpec {
        public boolean isLoaded() { return loaded; }
    }
    public static final ConfigSpec SPEC = new ConfigSpec();

    // Value wrappers
    public static class IntValue {
        private int value;
        private final int defaultValue, min, max;
        public IntValue(int defaultValue, int min, int max) {
            this.value = defaultValue; this.defaultValue = defaultValue; this.min = min; this.max = max;
        }
        public int get() { return value; }
        public void set(int v) { this.value = Math.max(min, Math.min(max, v)); }
    }

    public static class DoubleValue {
        private double value;
        private final double defaultValue, min, max;
        public DoubleValue(double defaultValue, double min, double max) {
            this.value = defaultValue; this.defaultValue = defaultValue; this.min = min; this.max = max;
        }
        public Double get() { return value; }
        public void set(double v) { this.value = Math.max(min, Math.min(max, v)); }
    }

    public static class BooleanValue {
        private boolean value;
        private final boolean defaultValue;
        public BooleanValue(boolean defaultValue) {
            this.value = defaultValue; this.defaultValue = defaultValue;
        }
        public boolean get() { return value; }
        public void set(boolean v) { this.value = v; }
    }

    public static class StringListValue {
        private List<String> value;
        private final List<String> defaultValue;
        public StringListValue(List<String> defaultValue) {
            this.value = new ArrayList<>(defaultValue); this.defaultValue = defaultValue;
        }
        public List<String> get() { return value; }
    }

    // Spawning & Performance
    public static final IntValue AMBIENT_RADIUS = new IntValue(10, 1, 32);
    public static final IntValue AMBIENT_HARD_CAP = new IntValue(12, 1, 48);
    public static final IntValue AMBIENT_BLOCK_CAP = new IntValue(14, 1, 20);
    public static final IntValue MIN_BLOCK_LIGHT = new IntValue(6, 0, 15);
    public static final IntValue DAYTIME_LIGHT_DIFF = new IntValue(5, 0, 15);
    public static final IntValue FALLOFF_DISTANCE = new IntValue(6, 1, 32);
    public static final DoubleValue FALLOFF_MULTIPLIER = new DoubleValue(0.3, 0.0, 1.0);
    public static final BooleanValue ENABLE_OCCLUSION_CULLING = new BooleanValue(true);

    // Visuals & Environment
    public static final DoubleValue AMBIENT_DUST_OPACITY = new DoubleValue(0.22, 0.0, 1.0);
    public static final DoubleValue PARTICLE_SIZE = new DoubleValue(0.022, 0.001, 0.1);
    public static final IntValue PARTICLE_LIFETIME = new IntValue(200, 20, 1000);
    public static final DoubleValue WIND_SPEED_CLEAR = new DoubleValue(0.15, 0.0, 1.0);
    public static final DoubleValue WIND_SPEED_RAIN = new DoubleValue(0.25, 0.0, 1.0);
    public static final DoubleValue WIND_SPEED_THUNDER = new DoubleValue(0.4, 0.0, 1.0);
    public static final BooleanValue DISABLE_DURING_RAIN = new BooleanValue(false);
    public static final BooleanValue DISABLE_DURING_THUNDER = new BooleanValue(false);

    // Player Interactions & World Actions
    public static final DoubleValue PLAYER_INTERACT_RADIUS = new DoubleValue(4.0, 0.0, 16.0);
    public static final IntValue BREAK_PARTICLE_COUNT = new IntValue(12, 0, 50);
    public static final DoubleValue BREAK_PARTICLE_SPEED = new DoubleValue(0.1, 0.0, 1.0);
    public static final DoubleValue ACTION_DUST_GRAVITY = new DoubleValue(0.002, 0.0, 0.05);
    public static final DoubleValue ACTION_DUST_BOUNCE = new DoubleValue(0.2, 0.0, 1.0);

    public static final IntValue HEAVY_LANDING_MAX_PARTICLES = new IntValue(96, 0, 300);
    public static final IntValue HEAVY_LANDING_PARTICLE_MULTIPLIER = new IntValue(12, 0, 50);
    public static final DoubleValue HEAVY_LANDING_UPWARD_SPEED = new DoubleValue(0.2, 0.0, 2.0);
    public static final DoubleValue HEAVY_LANDING_OUTWARD_SPEED = new DoubleValue(0.12, 0.0, 2.0);
    public static final DoubleValue HEAVY_LANDING_AMBIENT_PUSH = new DoubleValue(0.001, 0.0, 0.1);
    public static final DoubleValue HEAVY_LANDING_AMBIENT_RADIUS = new DoubleValue(4.0, 1.0, 16.0);

    public static final StringListValue HEAT_BLOCKS = new StringListValue(Arrays.asList(
        "minecraft:torch=0.015,2,0.4",
        "minecraft:wall_torch=0.015,2,0.4",
        "minecraft:soul_torch=0.015,2,0.4",
        "minecraft:soul_wall_torch=0.015,2,0.4",
        "minecraft:magma_block=0.02,3,0.7",
        "minecraft:campfire=0.035,5,0.5",
        "minecraft:soul_campfire=0.035,5,0.5",
        "minecraft:lava=0.045,5,0.7"
    ));

    // Experimental Features
    public static final BooleanValue ENABLE_DUST_SETTLING = new BooleanValue(true);
    public static final BooleanValue ENABLE_ENTITY_DISTURBANCE = new BooleanValue(false);
    public static final DoubleValue ENTITY_PUSH_STRENGTH = new DoubleValue(0.05, 0.0, 2.0);

    public static void load() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            if (Files.exists(CONFIG_PATH)) {
                String json = Files.readString(CONFIG_PATH);
                JsonObject root = JsonParser.parseString(json).getAsJsonObject();
                loadFromJson(root);
            } else {
                save();
            }
        } catch (Exception e) {
            LOGGER.error("[Light Dust] Failed to load config: {}", e.getMessage());
            save();
        }
        loaded = true;
    }

    private static void loadFromJson(JsonObject root) {
        if (root.has("spawning")) {
            JsonObject s = root.getAsJsonObject("spawning");
            if (s.has("ambientRadius")) AMBIENT_RADIUS.set(s.get("ambientRadius").getAsInt());
            if (s.has("ambientHardCapRadius")) AMBIENT_HARD_CAP.set(s.get("ambientHardCapRadius").getAsInt());
            if (s.has("ambientBlockCap")) AMBIENT_BLOCK_CAP.set(s.get("ambientBlockCap").getAsInt());
            if (s.has("minBlockLight")) MIN_BLOCK_LIGHT.set(s.get("minBlockLight").getAsInt());
            if (s.has("daytimeLightDiff")) DAYTIME_LIGHT_DIFF.set(s.get("daytimeLightDiff").getAsInt());
            if (s.has("falloffDistance")) FALLOFF_DISTANCE.set(s.get("falloffDistance").getAsInt());
            if (s.has("falloffMultiplier")) FALLOFF_MULTIPLIER.set(s.get("falloffMultiplier").getAsDouble());
            if (s.has("enableOcclusionCulling")) ENABLE_OCCLUSION_CULLING.set(s.get("enableOcclusionCulling").getAsBoolean());
        }
        if (root.has("visuals")) {
            JsonObject v = root.getAsJsonObject("visuals");
            if (v.has("ambientDustOpacity")) AMBIENT_DUST_OPACITY.set(v.get("ambientDustOpacity").getAsDouble());
            if (v.has("particleSize")) PARTICLE_SIZE.set(v.get("particleSize").getAsDouble());
            if (v.has("particleLifetime")) PARTICLE_LIFETIME.set(v.get("particleLifetime").getAsInt());
            if (v.has("windSpeedClear")) WIND_SPEED_CLEAR.set(v.get("windSpeedClear").getAsDouble());
            if (v.has("windSpeedRain")) WIND_SPEED_RAIN.set(v.get("windSpeedRain").getAsDouble());
            if (v.has("windSpeedThunder")) WIND_SPEED_THUNDER.set(v.get("windSpeedThunder").getAsDouble());
            if (v.has("disableDuringRain")) DISABLE_DURING_RAIN.set(v.get("disableDuringRain").getAsBoolean());
            if (v.has("disableDuringThunder")) DISABLE_DURING_THUNDER.set(v.get("disableDuringThunder").getAsBoolean());
        }
        if (root.has("interactions")) {
            JsonObject i = root.getAsJsonObject("interactions");
            if (i.has("playerInteractRadius")) PLAYER_INTERACT_RADIUS.set(i.get("playerInteractRadius").getAsDouble());
            if (i.has("breakParticleCount")) BREAK_PARTICLE_COUNT.set(i.get("breakParticleCount").getAsInt());
            if (i.has("breakParticleSpeed")) BREAK_PARTICLE_SPEED.set(i.get("breakParticleSpeed").getAsDouble());
            if (i.has("actionDustGravity")) ACTION_DUST_GRAVITY.set(i.get("actionDustGravity").getAsDouble());
            if (i.has("actionDustBounce")) ACTION_DUST_BOUNCE.set(i.get("actionDustBounce").getAsDouble());
            if (i.has("heavyLandingMaxParticles")) HEAVY_LANDING_MAX_PARTICLES.set(i.get("heavyLandingMaxParticles").getAsInt());
            if (i.has("heavyLandingParticleMultiplier")) HEAVY_LANDING_PARTICLE_MULTIPLIER.set(i.get("heavyLandingParticleMultiplier").getAsInt());
            if (i.has("heavyLandingUpwardSpeed")) HEAVY_LANDING_UPWARD_SPEED.set(i.get("heavyLandingUpwardSpeed").getAsDouble());
            if (i.has("heavyLandingOutwardSpeed")) HEAVY_LANDING_OUTWARD_SPEED.set(i.get("heavyLandingOutwardSpeed").getAsDouble());
            if (i.has("heavyLandingAmbientPush")) HEAVY_LANDING_AMBIENT_PUSH.set(i.get("heavyLandingAmbientPush").getAsDouble());
            if (i.has("heavyLandingAmbientRadius")) HEAVY_LANDING_AMBIENT_RADIUS.set(i.get("heavyLandingAmbientRadius").getAsDouble());
            if (i.has("heatBlocks")) {
                JsonArray arr = i.getAsJsonArray("heatBlocks");
                List<String> list = new ArrayList<>();
                arr.forEach(e -> list.add(e.getAsString()));
                HEAT_BLOCKS.value = list;
            }
        }
        if (root.has("experimental")) {
            JsonObject e = root.getAsJsonObject("experimental");
            if (e.has("enableDustSettling")) ENABLE_DUST_SETTLING.set(e.get("enableDustSettling").getAsBoolean());
            if (e.has("enableEntityDisturbance")) ENABLE_ENTITY_DISTURBANCE.set(e.get("enableEntityDisturbance").getAsBoolean());
            if (e.has("entityPushStrength")) ENTITY_PUSH_STRENGTH.set(e.get("entityPushStrength").getAsDouble());
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            JsonObject root = new JsonObject();

            JsonObject spawning = new JsonObject();
            spawning.addProperty("ambientRadius", AMBIENT_RADIUS.get());
            spawning.addProperty("ambientHardCapRadius", AMBIENT_HARD_CAP.get());
            spawning.addProperty("ambientBlockCap", AMBIENT_BLOCK_CAP.get());
            spawning.addProperty("minBlockLight", MIN_BLOCK_LIGHT.get());
            spawning.addProperty("daytimeLightDiff", DAYTIME_LIGHT_DIFF.get());
            spawning.addProperty("falloffDistance", FALLOFF_DISTANCE.get());
            spawning.addProperty("falloffMultiplier", FALLOFF_MULTIPLIER.get());
            spawning.addProperty("enableOcclusionCulling", ENABLE_OCCLUSION_CULLING.get());
            root.add("spawning", spawning);

            JsonObject visuals = new JsonObject();
            visuals.addProperty("ambientDustOpacity", AMBIENT_DUST_OPACITY.get());
            visuals.addProperty("particleSize", PARTICLE_SIZE.get());
            visuals.addProperty("particleLifetime", PARTICLE_LIFETIME.get());
            visuals.addProperty("windSpeedClear", WIND_SPEED_CLEAR.get());
            visuals.addProperty("windSpeedRain", WIND_SPEED_RAIN.get());
            visuals.addProperty("windSpeedThunder", WIND_SPEED_THUNDER.get());
            visuals.addProperty("disableDuringRain", DISABLE_DURING_RAIN.get());
            visuals.addProperty("disableDuringThunder", DISABLE_DURING_THUNDER.get());
            root.add("visuals", visuals);

            JsonObject interactions = new JsonObject();
            interactions.addProperty("playerInteractRadius", PLAYER_INTERACT_RADIUS.get());
            interactions.addProperty("breakParticleCount", BREAK_PARTICLE_COUNT.get());
            interactions.addProperty("breakParticleSpeed", BREAK_PARTICLE_SPEED.get());
            interactions.addProperty("actionDustGravity", ACTION_DUST_GRAVITY.get());
            interactions.addProperty("actionDustBounce", ACTION_DUST_BOUNCE.get());
            interactions.addProperty("heavyLandingMaxParticles", HEAVY_LANDING_MAX_PARTICLES.get());
            interactions.addProperty("heavyLandingParticleMultiplier", HEAVY_LANDING_PARTICLE_MULTIPLIER.get());
            interactions.addProperty("heavyLandingUpwardSpeed", HEAVY_LANDING_UPWARD_SPEED.get());
            interactions.addProperty("heavyLandingOutwardSpeed", HEAVY_LANDING_OUTWARD_SPEED.get());
            interactions.addProperty("heavyLandingAmbientPush", HEAVY_LANDING_AMBIENT_PUSH.get());
            interactions.addProperty("heavyLandingAmbientRadius", HEAVY_LANDING_AMBIENT_RADIUS.get());
            JsonArray heatArr = new JsonArray();
            HEAT_BLOCKS.get().forEach(heatArr::add);
            interactions.add("heatBlocks", heatArr);
            root.add("interactions", interactions);

            JsonObject experimental = new JsonObject();
            experimental.addProperty("enableDustSettling", ENABLE_DUST_SETTLING.get());
            experimental.addProperty("enableEntityDisturbance", ENABLE_ENTITY_DISTURBANCE.get());
            experimental.addProperty("entityPushStrength", ENTITY_PUSH_STRENGTH.get());
            root.add("experimental", experimental);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Files.writeString(CONFIG_PATH, gson.toJson(root));
        } catch (Exception e) {
            LOGGER.error("[Light Dust] Failed to save config: {}", e.getMessage());
        }
    }
}
