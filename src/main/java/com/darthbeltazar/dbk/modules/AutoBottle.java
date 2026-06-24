package com.darthbeltazar.dbk.modules;

import com.darthbeltazar.dbk.Addon;
import com.darthbeltazar.dbk.assets.RaidHelper;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;


public class AutoBottle extends Module {
    private boolean isDrinking;

    public AutoBottle() {
        super(Addon.DBK, "auto-bottle", "Automatically drinks ominous bottles for raid farming");
    }

    @Override
    public void onActivate() {
        isDrinking = false;
    }

    @Override
    public void onDeactivate() {
        isDrinking = false;
        useBottle();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.level == null) return;

        updateIsDrinking();
        useBottle();
    }

    private void updateIsDrinking() {
        if (mc.player == null || mc.level == null) return;
        if (RaidHelper.isRaidActive()) {
            isDrinking = false;
            return;
        }
        isDrinking = true;

        for (MobEffectInstance effect : mc.player.getActiveEffects()) {
            if (effect.getEffect().is(MobEffects.BAD_OMEN) || effect.getEffect().is(MobEffects.RAID_OMEN)) {
                isDrinking = false;
                return;
            }
        }
    }

    private void useBottle() {
        if (!isDrinking) {
            mc.options.keyUse.setDown(false);
            InvUtils.swapBack();
            return;
        }

        FindItemResult bottle = InvUtils.find(itemStack -> itemStack.getItem() == Items.OMINOUS_BOTTLE);
        if (!bottle.found()) {
            info("No bottle found");
            return;
        }
        InvUtils.swap(bottle.slot(), true);

        mc.options.keyUse.setDown(true);
    }
}
