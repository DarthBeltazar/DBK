package com.darthbeltazar.dbk.modules;

import com.darthbeltazar.dbk.Addon;
import com.darthbeltazar.dbk.utils.RaidHelper;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;


public class AutoBottle extends Module {
    private boolean isDrinking;
    private boolean noBottleReported;

    public AutoBottle() {
        super(Addon.DBK, "auto-bottle", "Automatically drinks ominous bottles for raid farming");
    }

    @Override
    public void onActivate() {
        isDrinking = false;
        noBottleReported = false;
    }

    @Override
    public void onDeactivate() {
        if (isDrinking) stopDrinking();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.level == null) return;

        if (shouldDrink()) {
            drink();
        } else if (isDrinking) {
            stopDrinking();
        }
    }

    private boolean shouldDrink() {
        if (RaidHelper.isRaidActive()) return false;
        return !mc.player.hasEffect(MobEffects.BAD_OMEN) && !mc.player.hasEffect(MobEffects.RAID_OMEN);
    }

    private void drink() {
        FindItemResult bottle = InvUtils.findInHotbar(Items.OMINOUS_BOTTLE);
        if (!bottle.found()) {
            if (!noBottleReported) {
                info("No bottle found in hotbar");
                noBottleReported = true;
            }
            if (isDrinking) stopDrinking();
            return;
        }
        noBottleReported = false;

        InvUtils.swap(bottle.slot(), true);
        mc.options.keyUse.setDown(true);
        isDrinking = true;
    }

    // Called only on the drinking -> not drinking transition, so the player can use items otherwise
    private void stopDrinking() {
        mc.options.keyUse.setDown(false);
        InvUtils.swapBack();
        isDrinking = false;
    }
}
