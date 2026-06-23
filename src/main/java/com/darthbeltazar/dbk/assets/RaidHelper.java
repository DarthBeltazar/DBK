package com.darthbeltazar.dbk.assets;

import com.darthbeltazar.dbk.interfaces.IRaidCheck;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class RaidHelper {
    public static boolean isRaidActive(){
        if (mc.inGameHud != null && mc.inGameHud.getBossBarHud() != null) {
            IRaidCheck raidCheck = (IRaidCheck) mc.inGameHud.getBossBarHud();

            return raidCheck.dbk$isRaidActive();
        }
        return false;
    }
}
