package dev.matthiesen.common.cobblemon_luckperms_npc_compat;

import net.minecraft.server.MinecraftServer;

public class CobblemonLuckPermsNPCCompat {
    public static MinecraftServer currentServer;

    public static void initialize() {
        Constants.createInfoLog("Initialized");
    }

    public static void onStartup(MinecraftServer server) {
        Constants.createInfoLog("Server starting, Setting up");
        currentServer = server;
    }

    public static void onShutdown() {
        Constants.createInfoLog("Server stopping, shutting down");
    }
}
