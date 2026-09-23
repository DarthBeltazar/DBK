/**
 * Mixin which determines whether a raid is currently underway on the boss bar
 */
package com.darthbeltazar.dbk.mixin;

import com.darthbeltazar.dbk.interfaces.IRaidCheck;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;
import java.util.UUID;

@Mixin(BossHealthOverlay.class)
public class RaidBossBarMixin implements IRaidCheck {
    @Shadow
    private Map<UUID, LerpingBossEvent> events;

    @Override
    public boolean dbk$isRaidActive() {
        for (LerpingBossEvent bossBar : this.events.values()) {
            if (isRaidBar(bossBar.getName())) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private static boolean isRaidBar(Component name) {
        // Vanilla raid bars use a translatable name, which works regardless of client language
        if (name.getContents() instanceof TranslatableContents translatable
            && translatable.getKey().startsWith("event.minecraft.raid")) {
            return true;
        }
        // Fallback for servers that send the raid name as plain text
        String text = name.getString().toLowerCase();
        return text.contains("raid") || text.contains("рейд");
    }
}
