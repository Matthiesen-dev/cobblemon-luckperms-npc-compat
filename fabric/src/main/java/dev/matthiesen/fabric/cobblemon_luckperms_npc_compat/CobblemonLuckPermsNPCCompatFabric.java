package dev.matthiesen.fabric.cobblemon_luckperms_npc_compat;

import dev.matthiesen.common.cobblemon_luckperms_npc_compat.CobblemonLuckPermsNPCCompat;
import dev.matthiesen.common.cobblemon_luckperms_npc_compat.Constants;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class CobblemonLuckPermsNPCCompatFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Constants.createInfoLog("Loading for Fabric Mod Loader");
        CobblemonLuckPermsNPCCompat.initialize();
        ServerLifecycleEvents.SERVER_STARTING.register(server -> CobblemonLuckPermsNPCCompat.onStartup());
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> CobblemonLuckPermsNPCCompat.onShutdown());
    }
}
