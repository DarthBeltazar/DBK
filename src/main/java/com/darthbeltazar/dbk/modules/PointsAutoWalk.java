package com.darthbeltazar.dbk.modules;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritoneProvider;
import baritone.api.pathing.goals.GoalBlock;
import com.darthbeltazar.dbk.Addon;
import com.darthbeltazar.dbk.assets.BoxHighlightSettings;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class PointsAutoWalk extends BoxHighlightSettings {
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
    private final SettingGroup sgRender = this.settings.createGroup("Render");
    private final Setting<String> pointsString = sgGeneral.add(new StringSetting.Builder()
        .name("points")
        .description("Coordinates in format x y z (separator) x y z")
        .build()
    );
    private final Setting<String> separator = sgGeneral.add(new StringSetting.Builder()
        .name("separator")
        .description("Symbol which separates coordinates")
        .defaultValue(";")
        .build()
    );
    private final Setting<Boolean> highlight = sgRender.add(new BoolSetting.Builder()
        .name("highlight")
        .description("Highlights selected points (when active baritone is not walk)")
        .build()
    );
    private final List<BlockPos> points = new ArrayList<>();
    boolean isPatching;
    private int index;
    private IBaritoneProvider baritone;


    public PointsAutoWalk() {
        super(Addon.DBK, "points-auto-walk", "Points auto walk");
    }

    @EventHandler
    private void onRender3d(Render3DEvent event) {
        if (!highlight.get()) return;
        if (points.isEmpty()) return;
        for (BlockPos pos : points) {
            event.renderer.box(pos, fColor.get(), eColor.get(), shapeMode.get(), 0);
        }

    }

    @Override
    public void onActivate() {
        baritone = BaritoneAPI.getProvider();
        index = 0;
        isPatching = false;
    }

    @Override
    public void onDeactivate() {
        stopPatching();
        isPatching = false;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (pointsString.get().isEmpty() || !isActive()) return;
        if (!isPatching) {
            parsePoints(pointsString.get());
        }

        if (highlight.get()) return;

        if (index >= points.size()) {
            info("Completed!");
            toggle();
            return;
        }
        if (!isPatching) {
            BlockPos pos = points.get(index);
            pathTo(pos);
            isPatching = true;
        }
        if (hasReachedGoal()) {
            index++;
            isPatching = false;
        } else if (!isPatching()) {
            isPatching = false;
        }
    }

    private void parsePoints(String pointsStr) {
        points.clear();
        String[] pointsArray = pointsStr.split(separator.get());
        for (String point : pointsArray) {
            String[] split = point.trim().split(" ");
            if (split.length != 3) return;

            try {
                BlockPos pos = new BlockPos(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                points.add(pos);
            } catch (NumberFormatException e) {
                info("Invalid coordinates: " + point);
            }
        }
    }

    private void pathTo(BlockPos pos) {
        if (baritone == null) return;

        GoalBlock goal = new GoalBlock(pos);
        baritone.getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(goal);
    }

    private void stopPatching() {
        if (baritone == null) return;
        baritone.getPrimaryBaritone().getPathingBehavior().cancelEverything();
    }

    public boolean isPatching() {
        if (baritone == null) return false;
        return baritone.getPrimaryBaritone().getPathingBehavior().isPathing();
    }

    public boolean hasReachedGoal() {
        if (baritone == null) return false;
        var pathingBehavior = baritone.getPrimaryBaritone().getPathingBehavior();
        if (!pathingBehavior.isPathing()) return false;

        var goal = pathingBehavior.getGoal();
        if (goal == null) return false;

        var player = MinecraftClient.getInstance().player;
        if (player == null) return false;

        BlockPos playerPos = player.getBlockPos();
        return goal.isInGoal(playerPos);
    }
}
