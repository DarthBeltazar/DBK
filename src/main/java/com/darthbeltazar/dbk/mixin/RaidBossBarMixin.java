/**
 * Mixin which determines whether a raid is currently underway on the boss bar
 */
package com.darthbeltazar.dbk.mixin;

import com.darthbeltazar.dbk.interfaces.IRaidCheck;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.UUID;

@Mixin(BossHealthOverlay.class)
public class RaidBossBarMixin implements IRaidCheck {
    @Shadow
    private Map<UUID, LerpingBossEvent> events;

    @Override
    public boolean dbk$isRaidActive() {
        for (LerpingBossEvent bossBar : this.events.values()) {
            String text = bossBar.getName().getString().toLowerCase();
            if (text.contains("raid") || text.contains("рейд")) {
                return true;
            }
        }
        return false;
    }
}
