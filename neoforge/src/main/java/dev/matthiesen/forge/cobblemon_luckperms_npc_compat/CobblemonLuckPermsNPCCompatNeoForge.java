package dev.matthiesen.forge.cobblemon_luckperms_npc_compat;

import dev.matthiesen.common.cobblemon_luckperms_npc_compat.CobblemonLuckPermsNPCCompat;
import dev.matthiesen.common.cobblemon_luckperms_npc_compat.Constants;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

@Mod(Constants.MOD_ID)
public class CobblemonLuckPermsNPCCompatNeoForge {
    public CobblemonLuckPermsNPCCompatNeoForge(IEventBus modBus) {
        Constants.createInfoLog("Loading for NeoForge Mod Loader");
        CobblemonLuckPermsNPCCompat.initialize();
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        CobblemonLuckPermsNPCCompat.onStartup();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onServerStopping(ServerStoppingEvent event) {
        CobblemonLuckPermsNPCCompat.onShutdown();
    }
}
