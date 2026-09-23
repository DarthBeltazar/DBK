package com.darthbeltazar.dbk.modules;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.process.ICustomGoalProcess;
import com.darthbeltazar.dbk.Addon;
import com.darthbeltazar.dbk.utils.BoxHighlightSettings;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PointsAutoWalk extends BoxHighlightSettings {
    private static final int MAX_ATTEMPTS = 3;

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
    private final Setting<String> pointsString = sgGeneral.add(new StringSetting.Builder()
        .name("points")
        .description("Coordinates in format x y z (separator) x y z")
        .onChanged(value -> restart())
        .build()
    );
    private final Setting<String> separator = sgGeneral.add(new StringSetting.Builder()
        .name("separator")
        .description("Symbol which separates coordinates")
        .defaultValue(";")
        .onChanged(value -> restart())
        .build()
    );
    private final Setting<Boolean> highlight = sgRender.add(new BoolSetting.Builder()
        .name("highlight")
        .description("Highlights selected points")
        .build()
    );
    private final List<BlockPos> points = new ArrayList<>();
    private int index;
    private int attempts;
    private IBaritone baritone;


    public PointsAutoWalk() {
        super(Addon.DBK, "points-auto-walk", "Points auto walk");
    }

    @EventHandler
    private void onRender3d(Render3DEvent event) {
        if (!highlight.get()) return;
        for (BlockPos pos : points) {
            event.renderer.box(pos, fColor.get(), eColor.get(), shapeMode.get(), 0);
        }
    }

    @Override
    public void onActivate() {
        baritone = BaritoneAPI.getProvider().getPrimaryBaritone();
        if (baritone == null) {
            error("Baritone is not discovered. Please install baritone");
            toggle();
            return;
        }
        parsePoints();
        if (points.isEmpty()) {
            error("No valid points set");
            toggle();
            return;
        }
        index = 0;
        attempts = 0;
    }

    @Override
    public void onDeactivate() {
        if (baritone != null) {
            baritone.getPathingBehavior().cancelEverything();
        }
        baritone = null;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null || baritone == null) return;

        if (index >= points.size()) {
            info("Completed!");
            toggle();
            return;
        }

        ICustomGoalProcess process = baritone.getCustomGoalProcess();
        if (process.isActive()) return;

        // Baritone is idle: either the point is reached or pathing to it failed
        BlockPos target = points.get(index);
        if (mc.player.blockPosition().equals(target)) {
            index++;
            attempts = 0;
            return;
        }
        if (attempts >= MAX_ATTEMPTS) {
            warning("Can't reach %d %d %d, skipping", target.getX(), target.getY(), target.getZ());
            index++;
            attempts = 0;
            return;
        }
        attempts++;
        process.setGoalAndPath(new GoalBlock(target));
    }

    private void restart() {
        if (!isActive() || baritone == null) return;
        baritone.getPathingBehavior().cancelEverything();
        parsePoints();
        index = 0;
        attempts = 0;
    }

    private void parsePoints() {
        points.clear();
        String sep = separator.get().isEmpty() ? ";" : separator.get();
        for (String point : pointsString.get().split(Pattern.quote(sep))) {
            if (point.isBlank()) continue;
            String[] split = point.trim().split("\\s+");
            try {
                if (split.length != 3) throw new NumberFormatException();
                points.add(new BlockPos(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])));
            } catch (NumberFormatException e) {
                warning("Invalid coordinates: " + point.trim());
            }
        }
    }
}
