package com.darthbeltazar.dbk.modules;

import baritone.api.BaritoneAPI;
import baritone.api.process.IMineProcess;
import com.darthbeltazar.dbk.Addon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.world.level.block.Blocks;


public class WoodMine extends Module {
    private IMineProcess mineProcess;
    private boolean isMining = false;

    public WoodMine() {
        super(Addon.DBK, "wood-mine", "Extracts wood");
    }

    private void startMine() {
        isMining = true;
        mineProcess.mine(Blocks.OAK_LOG, Blocks.SPRUCE_LOG, Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG, Blocks.ACACIA_LOG, Blocks.CHERRY_LOG, Blocks.DARK_OAK_LOG, Blocks.PALE_OAK_LOG, Blocks.MANGROVE_LOG);
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.level == null) return;
        if (mineProcess == null || BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing()) {
            return;
        }
        if (!isMining) {
            startMine();
        }
        isMining = BaritoneAPI.getProvider().getPrimaryBaritone().getMineProcess().isActive();
    }

    @Override
    public void onActivate() {
        if (BaritoneAPI.getProvider().getPrimaryBaritone() == null) {
            error("Baritone is not discovered. Please install baritone");
            toggle();
            return;
        }
        mineProcess = BaritoneAPI.getProvider().getPrimaryBaritone().getMineProcess();
        isMining = false;
    }

    @Override
    public void onDeactivate() {
        if (mineProcess != null) {
            mineProcess.cancel();
        }
        isMining = false;
    }
}
