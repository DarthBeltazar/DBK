package com.darthbeltazar.dbk.modules;

import com.darthbeltazar.dbk.Addon;
import com.darthbeltazar.dbk.assets.BoxHighlightSettings;
import meteordevelopment.meteorclient.events.meteor.MouseScrollEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.BlockItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class DBKAirPlace extends BoxHighlightSettings {
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
        .name("range")
        .min(0)
        .sliderMax(5.5)
        .defaultValue(5)
        .build()
    );

    private final Setting<Double> placeDelay = sgGeneral.add(new DoubleSetting.Builder()
        .name("delay")
        .min(0)
        .sliderMax(20)
        .defaultValue(3)
        .build()
    );

    private final Setting<Double> scrollSensitivity = sgGeneral.add(new DoubleSetting.Builder()
        .name("scroll-sensitivity")
        .description("Allows you to change range.")
        .defaultValue(1)
        .min(0)
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
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        delay++;
        if (mc.player == null || mc.getCameraEntity() == null) return;
        hitResult = mc.getCameraEntity().raycast(range.get(), 0, false);
        if (!(hitResult instanceof BlockHitResult blockHitResult) || !(mc.player.getMainHandStack().getItem() instanceof BlockItem))
            return;

        if (delay < placeDelay.get()) return;

        if (mc.options.useKey.isPressed()) {
            mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, new BlockPos(0, 0, 0), Direction.DOWN));
            mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.OFF_HAND, blockHitResult, mc.player.currentScreenHandler.getRevision() + 2));
            mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, new BlockPos(0, 0, 0), Direction.DOWN));

            mc.player.swingHand(Hand.MAIN_HAND);
            delay = 0;
        }
    }

    @EventHandler
    private void onRender3d(Render3DEvent event) {
        if (mc.world == null) return;
        if (!render.get() || !(hitResult instanceof BlockHitResult blockHitResult) || !mc.world.getBlockState(blockHitResult.getBlockPos()).isReplaceable())
            return;
        event.renderer.box(blockHitResult.getBlockPos(), fColor.get(), eColor.get(), shapeMode.get(), 0);
    }

    @EventHandler
    private void onMouseScroll(MouseScrollEvent event) {
        if (scrollSensitivity.get() > 0 && isActive()) {
            range.set(range.get() + event.value * 0.25 * (scrollSensitivity.get()));
            if (range.get() > 5.5) range.set(5.5);

            event.cancel();
        }
    }
}
