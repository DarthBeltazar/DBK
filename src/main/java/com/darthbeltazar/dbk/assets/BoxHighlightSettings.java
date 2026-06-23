/**
 * Template for modules with box settings
 */
package com.darthbeltazar.dbk.assets;

import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

public abstract class BoxHighlightSettings extends Module {
    protected SettingGroup sgRender = this.settings.createGroup("Render");
    protected final Setting<SettingColor> fColor = sgRender.add(new ColorSetting.Builder()
        .name("fill-color")
        .description("The color of the marker.")
        .defaultValue(new Color(255, 10, 10, 15))
        .build()
    );
    protected final Setting<SettingColor> eColor = sgRender.add(new ColorSetting.Builder()
        .name("edge-color")
        .description("The color of the marker.")
        .defaultValue(new Color(255, 10, 10, 200))
        .build()
    );
    protected final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    public BoxHighlightSettings(Category category, String name, String desc) {
        super(category, name, desc);
    }
}
