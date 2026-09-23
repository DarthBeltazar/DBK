package com.darthbeltazar.dbk.modules;

import com.darthbeltazar.dbk.Addon;
import com.darthbeltazar.dbk.utils.BaritoneAirPlace;
import com.darthbeltazar.dbk.utils.BoxHighlightSettings;
import meteordevelopment.meteorclient.events.meteor.MouseScrollEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class DBKAirPlace extends BoxHighlightSettings {
    private static final double MAX_RANGE = 5.5;

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
    private final SettingGroup sgBaritone = this.settings.createGroup("Baritone");

    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
        .name("range")
        .min(0)
        .max(MAX_RANGE)
        .sliderMax(MAX_RANGE)
        .defaultValue(5)
        .build()
    );

    private final Setting<Integer> placeDelay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("Delay in ticks between placements.")
        .min(0)
        .sliderMax(20)
        .defaultValue(3)
        .build()
    );

    private final Setting<Double> scrollSensitivity = sgGeneral.add(new DoubleSetting.Builder()
        .name("scroll-sensitivity")
        .description("Allows you to change range with the scroll wheel while holding a block.")
        .defaultValue(1)
        .min(0)
        .build()
    );

    private final Setting<Boolean> baritoneAirPlace = sgBaritone.add(new BoolSetting.Builder()
        .name("baritone-air-place")
        .description("Lets Baritone place blocks in the air while pathing and building. Needs DBK's Baritone fork.")
        .defaultValue(false)
        .onChanged(value -> {
            // Also fires while Meteor loads its config at startup; onActivate syncs on joining a world anyway
            if (Utils.canUpdate()) syncBaritone(isActive() && value);
        })
        .build()
    );

    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder()
        .name("render")
        .defaultValue(true)
        .build()
    );

    private HitResult hitResult;
    private int delay;

    public DBKAirPlace() {
        super(Addon.DBK, "DBK-air-place", "Places blocks in air");
    }

    @Override
    public void onActivate() {
        delay = 0;
        syncBaritone(baritoneAirPlace.get());
    }

    @Override
    public void onDeactivate() {
        syncBaritone(false);
    }

    // The checkbox (while the module is on) decides Baritone's airPlace; the fork re-plans the current path when it changes
    private void syncBaritone(boolean enable) {
        if (!BaritoneAirPlace.set(enable) && enable) {
            warning("This Baritone has no airPlace setting, baritone-air-place needs DBK's Baritone fork");
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        delay++;
        if (mc.player == null || mc.getCameraEntity() == null) return;
        hitResult = mc.getCameraEntity().pick(range.get(), 0, false);
        if (!(hitResult instanceof BlockHitResult blockHitResult) || !(mc.player.getMainHandItem().getItem() instanceof BlockItem))
            return;

        if (delay < placeDelay.get()) return;

        if (mc.options.keyUse.isDown()) {
            mc.player.connection.send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND, new BlockPos(0, 0, 0), Direction.DOWN));
            mc.player.connection.send(new ServerboundUseItemOnPacket(InteractionHand.OFF_HAND, blockHitResult, mc.player.containerMenu.getStateId() + 2));
            mc.player.connection.send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND, new BlockPos(0, 0, 0), Direction.DOWN));

            mc.player.swing(InteractionHand.MAIN_HAND);
            delay = 0;
        }
    }

    @EventHandler
    private void onRender3d(Render3DEvent event) {
        if (mc.level == null) return;
        if (!render.get() || !(hitResult instanceof BlockHitResult blockHitResult) || !mc.level.getBlockState(blockHitResult.getBlockPos()).canBeReplaced())
            return;
        event.renderer.box(blockHitResult.getBlockPos(), fColor.get(), eColor.get(), shapeMode.get(), 0);
    }

    @EventHandler
    private void onMouseScroll(MouseScrollEvent event) {
        if (mc.player == null || scrollSensitivity.get() <= 0) return;
        // Only hijack the wheel while holding a block, so hotbar scrolling still works otherwise
        if (!(mc.player.getMainHandItem().getItem() instanceof BlockItem)) return;

        range.set(Math.clamp(range.get() + event.value * 0.25 * scrollSensitivity.get(), 0, MAX_RANGE));
        event.cancel();
    }
}
