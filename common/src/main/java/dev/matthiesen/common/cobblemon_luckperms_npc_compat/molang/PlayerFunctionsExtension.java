package dev.matthiesen.common.cobblemon_luckperms_npc_compat.molang;

import com.bedrockk.molang.runtime.MoParams;
import com.bedrockk.molang.runtime.value.DoubleValue;
import com.cobblemon.mod.common.api.molang.MoLangFunctions;
import dev.matthiesen.common.cobblemon_luckperms_npc_compat.CobblemonLuckPermsNPCCompat;
import dev.matthiesen.common.cobblemon_luckperms_npc_compat.Constants;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.function.Function;

public class PlayerFunctionsExtension {
    private static int sharedRemovePermissionNode(Player player, MoParams params) {
        String node = params.getString(0);
        ServerPlayer serverPlayer = (ServerPlayer) player;
        CobblemonLuckPermsNPCCompat.lpManager.removePermission(serverPlayer, node);
        return 0;
    }

    private static int sharedRemoveParentGroup(Player player, MoParams params) {
        String group = params.getString(0);
        ServerPlayer serverPlayer = (ServerPlayer) player;
        CobblemonLuckPermsNPCCompat.lpManager.removeUserParentGroup(serverPlayer, group);
        return 0;
    }

    private static int sharedRemoveUserMetaData(Player player, MoParams params) {
        String key = params.getString(0);
        ServerPlayer serverPlayer = (ServerPlayer) player;
        CobblemonLuckPermsNPCCompat.lpManager.removeUserMetaData(serverPlayer, key);
        return 0;
    }

    public static void register() {
        Constants.createInfoLog("Registering Cobblemon Molang Player function extensions");

        MoLangFunctions.INSTANCE.getPlayerFunctions().add(player -> {
            HashMap<String, Function<MoParams, Object>> map = new HashMap<>();

            // q.player.lp_promote(<track string>, <dont-add-to-first int-as-boolean>) 0
            map.put("lp_promote", params -> {
                String track = params.getString(0);
                boolean dontAddToFirst = params.getInt(1) != 0;
                ServerPlayer serverPlayer = (ServerPlayer) player;
                CobblemonLuckPermsNPCCompat.lpManager.promoteUserOnTrack(serverPlayer, track, dontAddToFirst);
                return 0;
            });

            // q.player.lp_demote(<track string>, <dont-remove-from-first int-as-boolean>) 0
            map.put("lp_demote", params -> {
                String track = params.getString(0);
                boolean dontRemoveFromFirst = params.getInt(1) != 0;
                ServerPlayer serverPlayer = (ServerPlayer) player;
                CobblemonLuckPermsNPCCompat.lpManager.demoteUserOnTrack(serverPlayer, track, dontRemoveFromFirst);
                return 0;
            });

            // q.player.lp_permission_set(<node string>, <value int-as-boolean>) 0
            map.put("lp_permission_set", params -> {
                String node = params.getString(0);
                boolean value = params.getInt(1) != 0;
                ServerPlayer serverPlayer = (ServerPlayer) player;
                if (CobblemonLuckPermsNPCCompat.lpManager.hasPermissionNode(serverPlayer, node)) {
                    return 0;
                }
                CobblemonLuckPermsNPCCompat.lpManager.addPermission(serverPlayer, node, value);
                return 0;
            });

            // q.player.lp_permission_unset(<node string>) 0
            map.put("lp_permission_unset", params -> sharedRemovePermissionNode(player, params));

            // q.player.lp_permission_settemp(<node string>, <value boolean>, <duration string>) 0
            map.put("lp_permission_settemp", params -> {
                String node = params.getString(0);
                boolean value = params.getInt(1) != 0;
                String duration = params.getString(2);
                ServerPlayer serverPlayer = (ServerPlayer) player;
                CobblemonLuckPermsNPCCompat.lpManager.addTempPermission(serverPlayer, node, value, duration);
                return 0;
            });

            // q.player.lp_permission_unsettemp(<node string>) 0
            map.put("lp_permission_unsettemp", params -> sharedRemovePermissionNode(player, params));

            // q.player.lp_permission_check(<node string>) Boolean-As-Double
            map.put("lp_permission_check", params -> {
                String node = params.getString(0);
                ServerPlayer serverPlayer = (ServerPlayer) player;
                if (CobblemonLuckPermsNPCCompat.lpManager.hasPermissionNode(serverPlayer, node)) {
                    return new DoubleValue(1);
                }
                return new DoubleValue(0);
            });

            // q.player.lp_parent_set(<group string>) 0
            map.put("lp_parent_set", params -> {
                String group = params.getString(0);
                ServerPlayer serverPlayer = (ServerPlayer) player;
                CobblemonLuckPermsNPCCompat.lpManager.setUserParentGroup(serverPlayer, group);
                return 0;
            });

            // q.player.lp_parent_add(<group string>) 0
            map.put("lp_parent_add", params -> {
                String group = params.getString(0);
                ServerPlayer serverPlayer = (ServerPlayer) player;
                CobblemonLuckPermsNPCCompat.lpManager.addUserParentGroup(serverPlayer, group);
                return 0;
            });

            // q.player.lp_parent_remove(<group string>) 0
            map.put("lp_parent_remove", params -> sharedRemoveParentGroup(player, params));

            // q.player.lp_parent_settrack(<track string>, <group string>) 0
            map.put("lp_parent_settrack", params -> {
                String track = params.getString(0);
                String group = params.contains(1) ? params.getString(1) : null;
                ServerPlayer serverPlayer = (ServerPlayer) player;
                CobblemonLuckPermsNPCCompat.lpManager.addUserToTrack(serverPlayer, track, group);
                return 0;
            });

            // q.player.lp_parent_addtemp(<group string>, <duration string>) 0
            map.put("lp_parent_addtemp", params -> {
                String group = params.getString(0);
                String duration = params.getString(1);
                ServerPlayer serverPlayer = (ServerPlayer) player;
                CobblemonLuckPermsNPCCompat.lpManager.addUserParentTempGroup(serverPlayer, group, duration);
                return 0;
            });

            // q.player.lp_parent_removetemp(<group string>) 0
            map.put("lp_parent_removetemp", params -> sharedRemoveParentGroup(player, params));

            // q.player.lp_meta_set(<key string>, <value string>) 0
            map.put("lp_meta_set", params -> {;
                String key = params.getString(0);
                String value = params.getString(1);
                ServerPlayer serverPlayer = (ServerPlayer) player;
                CobblemonLuckPermsNPCCompat.lpManager.setUserMetaData(serverPlayer, key, value);
                return 0;
            });

            // q.player.lp_meta_unset(<key string>) 0
            map.put("lp_meta_unset", params -> sharedRemoveUserMetaData(player, params));

            // q.player.lp_meta_settemp(<key string>, <value string>, <duration string>) 0
            map.put("lp_meta_settemp", params -> {
                String key = params.getString(0);
                String value = params.getString(1);
                String duration = params.getString(2);
                ServerPlayer serverPlayer = (ServerPlayer) player;
                CobblemonLuckPermsNPCCompat.lpManager.setTempUserMetaData(serverPlayer, key, value, duration);
                return 0;
            });

            // q.player.lp_meta_unsettemp(<key string>) 0
            map.put("lp_meta_unsettemp", params -> sharedRemoveUserMetaData(player, params));

            // q.player.lp_meta_get(<key string>) string
            map.put("lp_meta_get", params -> {
                String key = params.getString(0);
                ServerPlayer serverPlayer = (ServerPlayer) player;
                return CobblemonLuckPermsNPCCompat.lpManager.getUserMetaData(serverPlayer, key);
            });

            return map;
        });
    }
}
