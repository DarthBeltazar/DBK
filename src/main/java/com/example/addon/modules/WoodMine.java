package com.example.addon.modules;
import baritone.api.BaritoneAPI;
import baritone.api.process.IMineProcess;
import com.example.addon.Addon;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import net.minecraft.block.Blocks;


public class WoodMine extends Module {
    public WoodMine() {
        super(Addon.DBK, "wood-mine", "Extracts wood");
    }

    private IMineProcess mineProcess;
    private boolean isMining = false;


    private void startMine(){
        isMining = true;
        mineProcess.mine(Blocks.OAK_LOG, Blocks.SPRUCE_LOG, Blocks.BIRCH_LOG,  Blocks.JUNGLE_LOG, Blocks.ACACIA_LOG, Blocks.CHERRY_LOG, Blocks.DARK_OAK_LOG,  Blocks.PALE_OAK_LOG, Blocks.MANGROVE_LOG);
    }

    @EventHandler
    private void onFinishMining(){
        if (mineProcess != null && !mineProcess.isActive() && isMining) {
            isMining = false;
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event){
        if (mc.player == null || mc.world == null) return;
        if (mineProcess == null || BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing()) {
            return;
        }
        if (!isMining) {
            startMine();
        }
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
