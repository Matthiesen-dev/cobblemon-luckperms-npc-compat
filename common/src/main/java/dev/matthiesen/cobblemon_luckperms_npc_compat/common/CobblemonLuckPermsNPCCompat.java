package dev.matthiesen.cobblemon_luckperms_npc_compat.common;

import dev.matthiesen.cobblemon_luckperms_npc_compat.common.molang.PlayerFunctionsExtension;
import dev.matthiesen.libs.faststats.Token;
import dev.matthiesen.matthiesen_core.common.AbstractCommonMod;
import org.jetbrains.annotations.NotNull;

public final class CobblemonLuckPermsNPCCompat extends AbstractCommonMod {
    public static final String MOD_ID = "cobblemon_luckperms_npc_compat";
    private static final String MOD_NAME = "Cobblemon LuckPerms NPC Compat";
    private static @Token final String METRICS_TOKEN = "c44a39e50cb0c2b508f80bfca47e5346";
    public static final CobblemonLuckPermsNPCCompat INSTANCE = new CobblemonLuckPermsNPCCompat();

    public CobblemonLuckPermsNPCCompat() {
        super(MOD_ID, MOD_NAME);
    }

    @Override
    public void initialize() {
        super.initialize();
        PlayerFunctionsExtension.register();
        createInfoLog("Initialized");
    }

    @Override
    public @Token @NotNull String getMetricsToken() {
        return METRICS_TOKEN;
    }
}
