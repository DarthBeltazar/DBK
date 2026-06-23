package com.darthbeltazar.dbk.modules;

import com.darthbeltazar.dbk.Addon;
import com.darthbeltazar.dbk.assets.BoxHighlightSettings;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;

import java.util.ArrayList;
import java.util.List;

public class EnoughLight extends BoxHighlightSettings {
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();


    private final Setting<Integer> scanRadius = sgGeneral.add(new IntSetting.Builder()
        .name("scan-radius")
        .description("Scan radius (horizontally)")
        .min(1)
        .max(128)
        .sliderMax(64)
        .defaultValue(32)
        .build()
    );

    private final Setting<Integer> scanHeight = sgGeneral.add(new IntSetting.Builder()
        .name("scan-height")
        .description("Scan radius (vertically)")
        .min(1)
        .max(128)
        .sliderMax(64)
        .defaultValue(32)
        .build()
    );

    private final Setting<Integer> updRate = sgGeneral.add(new IntSetting.Builder()
        .name("update-rate")
        .description("Pause between updates")
        .min(1)
        .max(60)
        .sliderMax(60)
        .defaultValue(10)
        .build()
    );

    private final Setting<Boolean> checkAir = sgGeneral.add(new BoolSetting.Builder()
        .name("check-air")
        .defaultValue(true)
        .build()
    );

    private final List<BlockPos> spawnBlocks = new ArrayList<>();
    private int timer = 0;

    public EnoughLight() {
        super(Addon.DBK, "enough-light", "Highlights mob spawn places");
    }

    @Override
    public void onActivate() {
        spawnBlocks.clear();
        timer = 0;
    }

    @Override
    public void onDeactivate() {
        spawnBlocks.clear();
    }

    private void updateBlockPos() {
        spawnBlocks.clear();
        int r = scanRadius.get();
        int h = scanHeight.get();
        for (BlockPos pos : BlockPos.iterateOutwards(mc.player.getBlockPos(), r, h, r)) {
            if (!mc.world.isInBuildLimit(pos)) {
                continue;
            }
            if (!mc.world.isAir(pos) || !mc.world.getBlockState(pos.down()).isSolidBlock(mc.world, pos.down())) {
                continue;
            }
            if (checkAir.get()) {
                if (!mc.world.isAir(pos.up())) {
                    continue;
                }
            }
            if (mc.world.getLightLevel(LightType.BLOCK, pos) > 0) {
                continue;
            }

            spawnBlocks.add(pos.toImmutable());
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;
        if (timer > updRate.get()) {
            updateBlockPos();
            timer = 0;
        }
        timer++;
    }

    @EventHandler
    private void onRender3d(Render3DEvent event) {
        if (spawnBlocks.isEmpty()) return;
        for (BlockPos pos : spawnBlocks) {
            event.renderer.box(pos, fColor.get(), eColor.get(), shapeMode.get(), 0);
        }

    }


}
