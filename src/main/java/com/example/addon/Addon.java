package com.example.addon;

import com.example.addon.modules.*;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class Addon extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category DBK = new Category("DBK");
    public static final HudGroup HUD_GROUP = new HudGroup("Example");

    @Override
    public void onInitialize() {
        LOG.info("Initializing DBK");

        // Modules
        Modules.get().add(new EnoughLight());
        Modules.get().add(new WoodMine());
        Modules.get().add(new DBKAirPlace());
        Modules.get().add(new PointsAutoWalk());
        Modules.get().add(new AutoFirework());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(DBK);
    }

    @Override
    public String getPackage() {
        return "com.example.addon";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("DarthBeltazar", "DBK");
    }
}
