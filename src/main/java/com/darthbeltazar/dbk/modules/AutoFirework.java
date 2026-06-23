package com.darthbeltazar.dbk.modules;

import com.darthbeltazar.dbk.Addon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class AutoFirework extends Module {
    private static final int TICKRATE = 20;
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("Delay in ticks between firework uses.")
        .defaultValue(20)
        .min(1)
        .sliderRange(1, 100)
        .build()
    );

    private final Setting<Boolean> stopWhenFastEnough = sgGeneral.add(new BoolSetting.Builder()
        .name("stop-when-fast-enough")
        .description("Stop using fireworks when speed is high enough.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Double> minSpeedThreshold = sgGeneral.add(new DoubleSetting.Builder()
        .name("min-speed-threshold")
        .description("Minimum speed to stop using fireworks.")
        .defaultValue(20)
        .min(0.0)
        .sliderRange(0.0, 35)
        .visible(stopWhenFastEnough::get)
        .build()
    );

    private int tickCounter = 0;

    public AutoFirework() {
        super(Addon.DBK, "auto-firework", "Automatically uses fireworks to boost elytra flight.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;
        if (!mc.player.isGliding()) return;

        tickCounter++;
        if (tickCounter < delay.get()) return;
        tickCounter = 0;

        if (stopWhenFastEnough.get()) {
            double speed = mc.player.getVelocity().length() * TICKRATE; //because velocity is in blocks per tick
            if (speed >= minSpeedThreshold.get()) return;
        }

        useFirework();
    }

    private void useFirework() {
        FindItemResult firework = InvUtils.find(itemStack -> itemStack.getItem() == Items.FIREWORK_ROCKET);
        if (!firework.found()) {
            info("No firework found");
            return;
        }
        InvUtils.swap(firework.slot(), true);
        if (mc.interactionManager != null) {
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        }
        InvUtils.swapBack();
    }
}
