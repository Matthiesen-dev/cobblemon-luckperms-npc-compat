package dev.matthiesen.common.cobblemon_luckperms_npc_compat;

import com.mojang.brigadier.CommandDispatcher;
import dev.matthiesen.common.cobblemon_luckperms_npc_compat.commands.CommandRegistry;
import dev.matthiesen.common.cobblemon_luckperms_npc_compat.config.ConfigManager;
import dev.matthiesen.common.cobblemon_luckperms_npc_compat.config.ModConfig;
import dev.matthiesen.common.cobblemon_luckperms_npc_compat.permissions.ModPermissions;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;

public class CobblemonLuckPermsNPCCompat {
    public static ModPermissions permissions;
    public static ModConfig config;
    public static MinecraftServer currentServer;

    public static void initialize() {
        Constants.createInfoLog("Initialized");
        config = new ConfigManager().loadConfig();
        permissions = new ModPermissions();
    }

    public static void onStartup(MinecraftServer server) {
        Constants.createInfoLog("Server starting, Setting up");
        currentServer = server;
    }

    public static void onShutdown() {
        Constants.createInfoLog("Server stopping, shutting down");
        new ConfigManager().saveConfig();
    }

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registry, Commands.CommandSelection context) {
        Constants.createInfoLog("Registering Commands");
        CommandRegistry.init(dispatcher, registry, context);
    }
}
