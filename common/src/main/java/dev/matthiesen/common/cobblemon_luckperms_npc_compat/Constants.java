package dev.matthiesen.common.cobblemon_luckperms_npc_compat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Constants {
    public static final String MOD_ID = "cobblemon_luckperms_npc_compat";
    public static final String ModName = "Cobblemon LuckPerms NPC Compat";

    public static Logger LOGGER = LogManager.getLogger(ModName);

    public static void createInfoLog(String message) {
        LOGGER.info(message);
    }

    public static void createErrorLog(String message) {
        LOGGER.error(message);
    }
}
