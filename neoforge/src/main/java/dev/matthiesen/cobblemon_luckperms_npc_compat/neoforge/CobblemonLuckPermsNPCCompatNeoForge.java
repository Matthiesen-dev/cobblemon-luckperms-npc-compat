package dev.matthiesen.cobblemon_luckperms_npc_compat.neoforge;

import dev.matthiesen.cobblemon_luckperms_npc_compat.common.CobblemonLuckPermsNPCCompat;
import net.neoforged.fml.common.Mod;

@Mod(CobblemonLuckPermsNPCCompat.MOD_ID)
public final class CobblemonLuckPermsNPCCompatNeoForge {
    public CobblemonLuckPermsNPCCompatNeoForge() {
        var INSTANCE = CobblemonLuckPermsNPCCompat.INSTANCE;
        INSTANCE.createInfoLog("Loading for NeoForge Mod Loader");
        INSTANCE.initialize();
    }
}
