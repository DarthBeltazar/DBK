package com.darthbeltazar.dbk.modules;

import com.darthbeltazar.dbk.Addon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;


public class AutoBottle extends Module {

    private boolean isDrinking;

    public AutoBottle() {
        super(Addon.DBK, "auto-bottle", "Automatically drinks ominous bottles for raid farming");
    }

    @Override
    public void onActivate() {
        isDrinking = false;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;

        isDrinking = true;
        for (StatusEffectInstance effect : mc.player.getStatusEffects()) {
            if (effect.getEffectType().equals(StatusEffects.BAD_OMEN) || effect.getEffectType().equals(StatusEffects.RAID_OMEN)) {
                isDrinking = false;
                break;
            }
        }

        useBottle();
    }

    private void useBottle() {
        if (!isDrinking) {
            mc.options.useKey.setPressed(false);
            return;
        }

        FindItemResult bottle = InvUtils.find(itemStack -> itemStack.getItem() == Items.OMINOUS_BOTTLE);
        if (!bottle.found()) {
            info("No bottle found");
            return;
        }
        InvUtils.swap(bottle.slot(), false);

        mc.options.useKey.setPressed(true);

    }
}
