package com.lightdust.config;

import com.google.gson.*;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class LightDustColorConfig {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Path CONFIG_PATH = Path.of("config", "lightdust", "colors.json");

    public static class DoubleValue {
        private double value;
        public DoubleValue(double defaultValue) { this.value = defaultValue; }
        public Double get() { return value; }
        public void set(double v) { this.value = v; }
    }

    public static class StringListValue {
        private List<String> value;
        private final List<String> defaultValue;
        public StringListValue(List<String> defaultValue) {
            this.value = new ArrayList<>(defaultValue); this.defaultValue = defaultValue;
        }
        public List<String> get() { return value; }
    }

    public static final DoubleValue TINT_STRENGTH = new DoubleValue(0.6);

    public static final List<String> DEFAULT_CUSTOM_TINTS = Arrays.asList(
        "minecraft:torch=#FFDB8A",
        "minecraft:lantern=#FFDB8A",
        "minecraft:soul_torch=#4CBAFF",
        "minecraft:soul_lantern=#4CBAFF",
        "minecraft:soul_campfire=#4CBAFF",
        "minecraft:redstone_torch=#FF4C4C",
        "minecraft:redstone_lamp=#FF4C4C",
        "minecraft:amethyst_cluster=#CC66FF",
        "minecraft:glowstone=#FFD670",
        "minecraft:shroomlight=#FF9933"
    );

    public static final List<String> DEFAULT_BIOME_TINTS = Arrays.asList(
        "minecraft:lush_caves=#8FCE00",
        "minecraft:dripstone_caves=#8B6B4A",
        "minecraft:deep_dark=#006666",
        "minecraft:forest=#70924D",
        "minecraft:flower_forest=#9AB96D",
        "minecraft:birch_forest=#84A65D",
        "minecraft:old_growth_birch_forest=#84A65D",
        "minecraft:dark_forest=#3E5B2D",
        "minecraft:jungle=#537B2F",
        "minecraft:sparse_jungle=#628D37",
        "minecraft:bamboo_jungle=#659832",
        "minecraft:taiga=#526E54",
        "minecraft:snowy_taiga=#FFFFFF",
        "minecraft:old_growth_pine_taiga=#445C45",
        "minecraft:old_growth_spruce_taiga=#3F5640",
        "minecraft:plains=#A8D080",
        "minecraft:sunflower_plains=#BDE491",
        "minecraft:snowy_plains=#FFFFFF",
        "minecraft:desert=#E4D5A7",
        "minecraft:savanna=#B3A25E",
        "minecraft:savanna_plateau=#A99958",
        "minecraft:windswept_savanna=#9E9156",
        "minecraft:badlands=#B26344",
        "minecraft:eroded_badlands=#C26845",
        "minecraft:wooded_badlands=#A35A3D",
        "minecraft:swamp=#4C5E35",
        "minecraft:mangrove_swamp=#54683C",
        "minecraft:river=#3F76E4",
        "minecraft:frozen_river=#CCFFFF",
        "minecraft:warm_ocean=#00AAAA",
        "minecraft:ocean=#00008B",
        "minecraft:cold_ocean=#202070",
        "minecraft:frozen_ocean=#CCFFFF",
        "minecraft:meadow=#98C874",
        "minecraft:cherry_grove=#FFB6C1",
        "minecraft:grove=#7A9C86",
        "minecraft:snowy_slopes=#FFFFFF",
        "minecraft:jagged_peaks=#F0F5F5",
        "minecraft:frozen_peaks=#FFFFFF",
        "minecraft:stony_peaks=#9E9E9E",
        "minecraft:windswept_hills=#8B9E8B",
        "minecraft:windswept_gravelly_hills=#8A948A",
        "minecraft:windswept_forest=#6C8B6C",
        "minecraft:ice_spikes=#CCFFFF",
        "minecraft:mushroom_fields=#807080",
        "minecraft:beach=#FADE55",
        "minecraft:snowy_beach=#FFFFFF",
        "minecraft:stony_shore=#8C8C8C",
        "minecraft:nether_wastes=#702B2B",
        "minecraft:crimson_forest=#821A1A",
        "minecraft:warped_forest=#1A8275",
        "minecraft:soul_sand_valley=#453531",
        "minecraft:basalt_deltas=#4A4A52",
        "minecraft:the_end=#C0A0C0",
        "minecraft:small_end_islands=#B090B0",
        "minecraft:end_midlands=#C0A0C0",
        "minecraft:end_highlands=#C0A0C0",
        "minecraft:end_barrens=#B090B0"
    );

    public static final List<String> DEFAULT_CAVE_TRIGGERS = Arrays.asList(
        "minecraft:moss_block=#8FCE00",
        "minecraft:cave_vines=#8FCE00",
        "minecraft:cave_vines_plant=#8FCE00",
        "minecraft:spore_blossom=#8FCE00",
        "minecraft:pointed_dripstone=#8B6B4A",
        "minecraft:dripstone_block=#8B6B4A",
        "minecraft:sculk=#006666",
        "minecraft:sculk_vein=#006666",
        "minecraft:sculk_sensor=#006666",
        "minecraft:snow=#FFFFFF",
        "minecraft:snow_block=#FFFFFF",
        "minecraft:powder_snow=#FFFFFF",
        "minecraft:ice=#CCFFFF",
        "minecraft:packed_ice=#CCFFFF",
        "minecraft:blue_ice=#CCFFFF"
    );

    public static final StringListValue CUSTOM_TINTS = new StringListValue(DEFAULT_CUSTOM_TINTS);
    public static final StringListValue CUSTOM_BIOME_TINTS = new StringListValue(DEFAULT_BIOME_TINTS);
    public static final StringListValue CAVE_BIOME_TRIGGERS = new StringListValue(DEFAULT_CAVE_TRIGGERS);

    public static void load() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            if (Files.exists(CONFIG_PATH)) {
                String json = Files.readString(CONFIG_PATH);
                JsonObject root = JsonParser.parseString(json).getAsJsonObject();
                if (root.has("tintStrength")) TINT_STRENGTH.set(root.get("tintStrength").getAsDouble());
                if (root.has("customTints")) {
                    List<String> list = new ArrayList<>();
                    root.getAsJsonArray("customTints").forEach(e -> list.add(e.getAsString()));
                    CUSTOM_TINTS.value = list;
                }
                if (root.has("customBiomeTints")) {
                    List<String> list = new ArrayList<>();
                    root.getAsJsonArray("customBiomeTints").forEach(e -> list.add(e.getAsString()));
                    CUSTOM_BIOME_TINTS.value = list;
                }
                if (root.has("caveBiomeTriggers")) {
                    List<String> list = new ArrayList<>();
                    root.getAsJsonArray("caveBiomeTriggers").forEach(e -> list.add(e.getAsString()));
                    CAVE_BIOME_TRIGGERS.value = list;
                }
            } else {
                save();
            }
        } catch (Exception e) {
            LOGGER.error("[Light Dust] Failed to load color config: {}", e.getMessage());
            save();
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            JsonObject root = new JsonObject();
            root.addProperty("tintStrength", TINT_STRENGTH.get());
            JsonArray tintsArr = new JsonArray();
            CUSTOM_TINTS.get().forEach(tintsArr::add);
            root.add("customTints", tintsArr);
            JsonArray biomeArr = new JsonArray();
            CUSTOM_BIOME_TINTS.get().forEach(biomeArr::add);
            root.add("customBiomeTints", biomeArr);
            JsonArray caveArr = new JsonArray();
            CAVE_BIOME_TRIGGERS.get().forEach(caveArr::add);
            root.add("caveBiomeTriggers", caveArr);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Files.writeString(CONFIG_PATH, gson.toJson(root));
        } catch (Exception e) {
            LOGGER.error("[Light Dust] Failed to save color config: {}", e.getMessage());
        }
    }
}
