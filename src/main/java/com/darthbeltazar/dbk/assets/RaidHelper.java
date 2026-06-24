package com.darthbeltazar.dbk.assets;

import com.darthbeltazar.dbk.interfaces.IRaidCheck;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class RaidHelper {
    public static boolean isRaidActive() {
        if (mc.gui != null && mc.gui.getBossOverlay() != null) {
            IRaidCheck raidCheck = (IRaidCheck) mc.gui.getBossOverlay();

            return raidCheck.dbk$isRaidActive();
        }
        return false;
    }
}
