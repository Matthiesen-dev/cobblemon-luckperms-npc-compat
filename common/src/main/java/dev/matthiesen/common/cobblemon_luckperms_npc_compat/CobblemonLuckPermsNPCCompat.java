package dev.matthiesen.common.cobblemon_luckperms_npc_compat;

import dev.matthiesen.common.cobblemon_luckperms_npc_compat.lp.LPManager;
import dev.matthiesen.common.cobblemon_luckperms_npc_compat.molang.PlayerFunctionsExtension;

public class CobblemonLuckPermsNPCCompat {
    public static LPManager lpManager;

    public static void initialize() {
        Constants.createInfoLog("Initialized");

        // Setup LPManager
        lpManager = new LPManager().getInstance();

        // Extend Cobblemon's Molang functions
        PlayerFunctionsExtension.register();
    }

    public static void onStartup() {
        Constants.createInfoLog("Server starting, Setting up");
    }

    public static void onShutdown() {
        Constants.createInfoLog("Server stopping, shutting down");
    }
}
