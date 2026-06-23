/**
 * Mixin which determines whether a raid is currently underway on the boss bar
 */
package com.darthbeltazar.dbk.mixin;

import com.darthbeltazar.dbk.interfaces.IRaidCheck;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.UUID;

@Mixin(BossBarHud.class)
public class RaidBossBarMixin implements IRaidCheck {
    @Shadow
    private Map<UUID, ClientBossBar> bossBars;

    @Override
    public boolean dbk$isRaidActive() {
        for (ClientBossBar bossBar : this.bossBars.values()) {
            String text = bossBar.getName().getString().toLowerCase();
            if (text.contains("raid") || text.contains("рейд")) {
                return true;
            }
        }
        return false;
    }
}
