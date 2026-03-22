package com.lightdust.config;

import com.lightdust.client.particle.DustParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LightDustConfigScreen extends Screen {

    private final Screen parent;
    private int scrollOffset = 0;
    private int maxScroll = 0;

    private static final int ROW_HEIGHT = 24;
    private static final int TOP_MARGIN = 56;
    private static final int BOTTOM_MARGIN = 36;

    private final Map<String, List<ConfigEntry>> tabs = new LinkedHashMap<>();
    private String activeTab;
    private final List<Button> tabButtons = new ArrayList<>();

    public LightDustConfigScreen(Screen parent) {
        super(Component.translatable("lightdust.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        tabs.clear();
        tabButtons.clear();
        buildTabs();

        if (activeTab == null || !tabs.containsKey(activeTab)) {
            activeTab = tabs.keySet().iterator().next();
        }

        int tabCount = tabs.size();
        int tabWidth = Math.min(90, (this.width - 20) / tabCount);
        int totalTabWidth = tabWidth * tabCount;
        int tabStartX = (this.width - totalTabWidth) / 2;
        int tabIdx = 0;

        for (String tabKey : tabs.keySet()) {
            String currentKey = tabKey;
            Button tabBtn = Button.builder(
                    Component.translatable(tabKey),
                    b -> switchTab(currentKey))
                    .bounds(tabStartX + tabIdx * tabWidth, 24, tabWidth - 2, 20)
                    .build();
            tabButtons.add(tabBtn);
            this.addRenderableWidget(tabBtn);
            tabIdx++;
        }

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, btn -> onClose())
                .bounds(this.width / 2 - 100, this.height - 28, 200, 20)
                .build());

        refreshEntryWidgets();
    }

    private void buildTabs() {
        List<ConfigEntry> spawning = new ArrayList<>();
        spawning.add(intEntry("lightdust.config.ambient_radius", LightDustConfig.AMBIENT_RADIUS, 1, 32));
        spawning.add(intEntry("lightdust.config.hard_cap_radius", LightDustConfig.AMBIENT_HARD_CAP, 1, 48));
        spawning.add(intEntry("lightdust.config.block_cap", LightDustConfig.AMBIENT_BLOCK_CAP, 1, 20));
        spawning.add(intEntry("lightdust.config.min_block_light", LightDustConfig.MIN_BLOCK_LIGHT, 0, 15));
        spawning.add(intEntry("lightdust.config.daytime_light_diff", LightDustConfig.DAYTIME_LIGHT_DIFF, 0, 15));
        spawning.add(intEntry("lightdust.config.falloff_distance", LightDustConfig.FALLOFF_DISTANCE, 1, 32));
        spawning.add(doubleEntry("lightdust.config.falloff_multiplier", LightDustConfig.FALLOFF_MULTIPLIER, 0.0, 1.0));
        spawning.add(boolEntry("lightdust.config.occlusion_culling", LightDustConfig.ENABLE_OCCLUSION_CULLING));
        tabs.put("lightdust.config.tab.spawning", spawning);

        List<ConfigEntry> visuals = new ArrayList<>();
        visuals.add(doubleEntry("lightdust.config.dust_opacity", LightDustConfig.AMBIENT_DUST_OPACITY, 0.0, 1.0));
        visuals.add(doubleEntry("lightdust.config.particle_size", LightDustConfig.PARTICLE_SIZE, 0.001, 0.1));
        visuals.add(intEntry("lightdust.config.particle_lifetime", LightDustConfig.PARTICLE_LIFETIME, 20, 1000));
        visuals.add(doubleEntry("lightdust.config.wind_clear", LightDustConfig.WIND_SPEED_CLEAR, 0.0, 1.0));
        visuals.add(doubleEntry("lightdust.config.wind_rain", LightDustConfig.WIND_SPEED_RAIN, 0.0, 1.0));
        visuals.add(doubleEntry("lightdust.config.wind_thunder", LightDustConfig.WIND_SPEED_THUNDER, 0.0, 1.0));
        visuals.add(boolEntry("lightdust.config.disable_rain", LightDustConfig.DISABLE_DURING_RAIN));
        visuals.add(boolEntry("lightdust.config.disable_thunder", LightDustConfig.DISABLE_DURING_THUNDER));
        visuals.add(doubleColorEntry("lightdust.config.tint_strength", LightDustColorConfig.TINT_STRENGTH, 0.0, 1.0));
        tabs.put("lightdust.config.tab.visuals", visuals);

        List<ConfigEntry> interactions = new ArrayList<>();
        interactions.add(doubleEntry("lightdust.config.interact_radius", LightDustConfig.PLAYER_INTERACT_RADIUS, 0.0, 16.0));
        interactions.add(intEntry("lightdust.config.break_count", LightDustConfig.BREAK_PARTICLE_COUNT, 0, 50));
        interactions.add(doubleEntry("lightdust.config.break_speed", LightDustConfig.BREAK_PARTICLE_SPEED, 0.0, 1.0));
        interactions.add(doubleEntry("lightdust.config.action_gravity", LightDustConfig.ACTION_DUST_GRAVITY, 0.0, 0.05));
        interactions.add(doubleEntry("lightdust.config.action_bounce", LightDustConfig.ACTION_DUST_BOUNCE, 0.0, 1.0));
        interactions.add(intEntry("lightdust.config.landing_max", LightDustConfig.HEAVY_LANDING_MAX_PARTICLES, 0, 300));
        interactions.add(intEntry("lightdust.config.landing_multiplier", LightDustConfig.HEAVY_LANDING_PARTICLE_MULTIPLIER, 0, 50));
        interactions.add(doubleEntry("lightdust.config.landing_upward", LightDustConfig.HEAVY_LANDING_UPWARD_SPEED, 0.0, 2.0));
        interactions.add(doubleEntry("lightdust.config.landing_outward", LightDustConfig.HEAVY_LANDING_OUTWARD_SPEED, 0.0, 2.0));
        interactions.add(doubleEntry("lightdust.config.landing_push", LightDustConfig.HEAVY_LANDING_AMBIENT_PUSH, 0.0, 0.1));
        interactions.add(doubleEntry("lightdust.config.landing_radius", LightDustConfig.HEAVY_LANDING_AMBIENT_RADIUS, 1.0, 16.0));
        tabs.put("lightdust.config.tab.interactions", interactions);

        List<ConfigEntry> experimental = new ArrayList<>();
        experimental.add(boolEntry("lightdust.config.dust_settling", LightDustConfig.ENABLE_DUST_SETTLING));
        experimental.add(boolEntry("lightdust.config.entity_disturbance", LightDustConfig.ENABLE_ENTITY_DISTURBANCE));
        experimental.add(doubleEntry("lightdust.config.entity_push", LightDustConfig.ENTITY_PUSH_STRENGTH, 0.0, 2.0));
        tabs.put("lightdust.config.tab.experimental", experimental);
    }

    private void switchTab(String tabKey) {
        activeTab = tabKey;
        scrollOffset = 0;
        refreshEntryWidgets();
        updateTabHighlights();
    }

    private void updateTabHighlights() {
        int idx = 0;
        for (String tabKey : tabs.keySet()) {
            if (idx < tabButtons.size()) {
                tabButtons.get(idx).active = !tabKey.equals(activeTab);
            }
            idx++;
        }
    }

    private void refreshEntryWidgets() {
        // Remove old entry widgets
        for (List<ConfigEntry> entries : tabs.values()) {
            for (ConfigEntry entry : entries) {
                if (entry.widget != null) {
                    this.removeWidget(entry.widget);
                    entry.widget = null;
                }
            }
        }

        List<ConfigEntry> entries = tabs.get(activeTab);
        if (entries == null) return;

        int contentHeight = entries.size() * ROW_HEIGHT;
        int viewHeight = this.height - TOP_MARGIN - BOTTOM_MARGIN;
        maxScroll = Math.max(0, contentHeight - viewHeight);
        scrollOffset = Mth.clamp(scrollOffset, 0, maxScroll);

        int visibleTop = TOP_MARGIN;
        int visibleBottom = this.height - BOTTOM_MARGIN;
        int widgetWidth = Math.min(310, this.width - 60);
        int widgetX = (this.width - widgetWidth) / 2;

        for (int i = 0; i < entries.size(); i++) {
            ConfigEntry entry = entries.get(i);
            int yPos = TOP_MARGIN + i * ROW_HEIGHT - scrollOffset;

            if (yPos + ROW_HEIGHT < visibleTop || yPos > visibleBottom) continue;

            String key = entry.translationKey;

            if (entry.type == EntryType.BOOL) {
                LightDustConfig.BooleanValue bv = entry.boolValue;
                Button btn = Button.builder(
                        boolLabel(key, bv.get()),
                        b -> {
                            bv.set(!bv.get());
                            b.setMessage(boolLabel(key, bv.get()));
                        })
                        .bounds(widgetX, yPos, widgetWidth, 20)
                        .tooltip(makeTooltip(key))
                        .build();
                entry.widget = btn;
                this.addRenderableWidget(btn);

            } else if (entry.type == EntryType.INT) {
                LightDustConfig.IntValue iv = entry.intValue;
                int min = entry.intMin;
                int max = entry.intMax;
                double initial = (double)(iv.get() - min) / (max - min);
                ConfigSlider slider = new ConfigSlider(widgetX, yPos, widgetWidth, 20, initial) {
                    @Override
                    protected void updateMessage() {
                        this.setMessage(Component.translatable(key).copy().append(": " + iv.get()));
                    }
                    @Override
                    protected void applyValue() {
                        iv.set(min + (int) Math.round(this.value * (max - min)));
                    }
                };
                slider.setMessage(Component.translatable(key).copy().append(": " + iv.get()));
                slider.setTooltip(makeTooltip(key));
                entry.widget = slider;
                this.addRenderableWidget(slider);

            } else if (entry.type == EntryType.DOUBLE) {
                LightDustConfig.DoubleValue dv = entry.doubleValue;
                double min = entry.doubleMin;
                double max = entry.doubleMax;
                double initial = (dv.get() - min) / (max - min);
                ConfigSlider slider = new ConfigSlider(widgetX, yPos, widgetWidth, 20, initial) {
                    @Override
                    protected void updateMessage() {
                        this.setMessage(Component.translatable(key).copy().append(": " + String.format("%.4f", dv.get())));
                    }
                    @Override
                    protected void applyValue() {
                        dv.set(min + this.value * (max - min));
                    }
                };
                slider.setMessage(Component.translatable(key).copy().append(": " + String.format("%.4f", dv.get())));
                slider.setTooltip(makeTooltip(key));
                entry.widget = slider;
                this.addRenderableWidget(slider);

            } else if (entry.type == EntryType.DOUBLE_COLOR) {
                LightDustColorConfig.DoubleValue dv = entry.colorDoubleValue;
                double min = entry.doubleMin;
                double max = entry.doubleMax;
                double initial = (dv.get() - min) / (max - min);
                ConfigSlider slider = new ConfigSlider(widgetX, yPos, widgetWidth, 20, initial) {
                    @Override
                    protected void updateMessage() {
                        this.setMessage(Component.translatable(key).copy().append(": " + String.format("%.4f", dv.get())));
                    }
                    @Override
                    protected void applyValue() {
                        dv.set(min + this.value * (max - min));
                    }
                };
                slider.setMessage(Component.translatable(key).copy().append(": " + String.format("%.4f", dv.get())));
                slider.setTooltip(makeTooltip(key));
                entry.widget = slider;
                this.addRenderableWidget(slider);
            }
        }

        updateTabHighlights();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 8, 0xFFFFFF);

        if (maxScroll > 0) {
            int barHeight = this.height - TOP_MARGIN - BOTTOM_MARGIN;
            int thumbHeight = Math.max(15, (int)((float)barHeight / (barHeight + maxScroll) * barHeight));
            int thumbY = TOP_MARGIN + (int)((float)scrollOffset / maxScroll * (barHeight - thumbHeight));
            graphics.fill(this.width - 4, TOP_MARGIN, this.width - 1, TOP_MARGIN + barHeight, 0x40FFFFFF);
            graphics.fill(this.width - 4, thumbY, this.width - 1, thumbY + thumbHeight, 0xAAFFFFFF);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        scrollOffset = Mth.clamp(scrollOffset - (int)(scrollY * ROW_HEIGHT * 2), 0, maxScroll);
        refreshEntryWidgets();
        return true;
    }

    @Override
    public void onClose() {
        LightDustConfig.save();
        LightDustColorConfig.save();
        DustParticle.colorsLoaded = false;
        Minecraft.getInstance().setScreen(parent);
    }

    // --- Helpers ---

    private Tooltip makeTooltip(String translationKey) {
        return Tooltip.create(Component.translatable(translationKey + ".tooltip"));
    }

    private Component boolLabel(String translationKey, boolean value) {
        Component state = value
                ? Component.literal("\u00a7a").append(Component.translatable("lightdust.config.bool.on"))
                : Component.literal("\u00a7c").append(Component.translatable("lightdust.config.bool.off"));
        return Component.translatable(translationKey).copy().append(": ").append(state);
    }

    // --- Entry types ---

    private enum EntryType { INT, DOUBLE, BOOL, DOUBLE_COLOR }

    private static class ConfigEntry {
        final EntryType type;
        final String translationKey;
        LightDustConfig.IntValue intValue;
        int intMin, intMax;
        LightDustConfig.DoubleValue doubleValue;
        LightDustConfig.BooleanValue boolValue;
        LightDustColorConfig.DoubleValue colorDoubleValue;
        double doubleMin, doubleMax;
        net.minecraft.client.gui.components.AbstractWidget widget;

        ConfigEntry(EntryType type, String translationKey) {
            this.type = type;
            this.translationKey = translationKey;
        }
    }

    private ConfigEntry intEntry(String key, LightDustConfig.IntValue value, int min, int max) {
        ConfigEntry e = new ConfigEntry(EntryType.INT, key);
        e.intValue = value;
        e.intMin = min;
        e.intMax = max;
        return e;
    }

    private ConfigEntry doubleEntry(String key, LightDustConfig.DoubleValue value, double min, double max) {
        ConfigEntry e = new ConfigEntry(EntryType.DOUBLE, key);
        e.doubleValue = value;
        e.doubleMin = min;
        e.doubleMax = max;
        return e;
    }

    private ConfigEntry doubleColorEntry(String key, LightDustColorConfig.DoubleValue value, double min, double max) {
        ConfigEntry e = new ConfigEntry(EntryType.DOUBLE_COLOR, key);
        e.colorDoubleValue = value;
        e.doubleMin = min;
        e.doubleMax = max;
        return e;
    }

    private ConfigEntry boolEntry(String key, LightDustConfig.BooleanValue value) {
        ConfigEntry e = new ConfigEntry(EntryType.BOOL, key);
        e.boolValue = value;
        return e;
    }

    private static abstract class ConfigSlider extends AbstractSliderButton {
        public ConfigSlider(int x, int y, int width, int height, double value) {
            super(x, y, width, height, Component.empty(), value);
        }
    }
}
