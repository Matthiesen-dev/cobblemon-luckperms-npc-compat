package dev.matthiesen.cobblemon_luckperms_npc_compat.fabric;

import dev.matthiesen.cobblemon_luckperms_npc_compat.common.CobblemonLuckPermsNPCCompat;
import net.fabricmc.api.ModInitializer;

public final class CobblemonLuckPermsNPCCompatFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        var INSTANCE = CobblemonLuckPermsNPCCompat.INSTANCE;
        INSTANCE.createInfoLog("Loading for Fabric Mod Loader");
        INSTANCE.initialize();
    }
}
