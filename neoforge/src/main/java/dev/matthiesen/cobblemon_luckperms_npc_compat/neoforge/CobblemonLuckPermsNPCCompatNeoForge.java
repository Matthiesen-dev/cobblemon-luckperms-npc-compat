package dev.matthiesen.cobblemon_luckperms_npc_compat.neoforge;

import dev.matthiesen.cobblemon_luckperms_npc_compat.common.CobblemonLuckPermsNPCCompat;
import net.neoforged.fml.common.Mod;

@Mod(CobblemonLuckPermsNPCCompat.MOD_ID)
public final class CobblemonLuckPermsNPCCompatNeoForge {
    public CobblemonLuckPermsNPCCompatNeoForge() {
        CobblemonLuckPermsNPCCompat.INSTANCE.createInfoLog("Loading for NeoForge Mod Loader");
        CobblemonLuckPermsNPCCompat.INSTANCE.initialize();
    }
}
