package com.darthbeltazar.dbk.utils;

import baritone.api.BaritoneAPI;
import baritone.api.Settings;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Toggles the {@code airPlace} setting of DBK's Baritone fork (https://github.com/DarthBeltazar/baritone, branch dbk-airplace).
 * The setting is looked up by name, so DBK still builds and runs against Meteor's Baritone, which doesn't have it.
 */
public class BaritoneAirPlace {
    private static final String SETTING = "airplace";

    /**
     * @return false if Baritone is missing or doesn't have the airPlace setting
     */
    @SuppressWarnings("unchecked")
    public static boolean set(boolean value) {
        FabricLoader loader = FabricLoader.getInstance();
        if (!loader.isModLoaded("baritone") && !loader.isModLoaded("baritone-meteor")) return false;

        Settings.Setting<?> setting = BaritoneAPI.getSettings().byLowerName.get(SETTING);
        if (setting == null || setting.getValueClass() != Boolean.class) return false;

        ((Settings.Setting<Boolean>) setting).value = value;
        return true;
    }
}
