package dev.matthiesen.cobblemon_luckperms_npc_compat.common.molang;

import com.bedrockk.molang.runtime.MoParams;
import com.bedrockk.molang.runtime.value.DoubleValue;
import com.cobblemon.mod.common.api.molang.MoLangFunctions;
import dev.matthiesen.cobblemon_luckperms_npc_compat.common.CobblemonLuckPermsNPCCompat;
import dev.matthiesen.cobblemon_luckperms_npc_compat.common.util.StringUtils;
import dev.matthiesen.matthiesen_core.common.core.permissions.LuckPermsHelper;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.track.Track;
import net.luckperms.api.track.TrackManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public final class PlayerFunctionsExtension {
    private static int sharedRemovePermissionNode(Player player, MoParams params) {
        String node = params.getString(0);
        ServerPlayer serverPlayer = (ServerPlayer) player;
        User user = LuckPermsHelper.INSTANCE.getUser(serverPlayer);
        if (user == null) return 1;
        user.data().remove(Node.builder(node).build());
        LuckPermsHelper.INSTANCE.saveUser(user);
        return 0;
    }

    private static int sharedRemoveParentGroup(Player player, MoParams params) {
        String group = params.getString(0);
        ServerPlayer serverPlayer = (ServerPlayer) player;
        User user = LuckPermsHelper.INSTANCE.getUser(serverPlayer);
        if (user == null) return 1;
        LuckPermsHelper.INSTANCE.removeUserParentGroup(user, group);
        return 0;
    }

    private static int sharedRemoveUserMetaData(Player player, MoParams params) {
        String key = params.getString(0);
        ServerPlayer serverPlayer = (ServerPlayer) player;
        User user = LuckPermsHelper.INSTANCE.getUser(serverPlayer);
        if (user == null) return 1;
        LuckPermsHelper.INSTANCE.clearMetaKey(user, key);
        return 0;
    }

    public static void register() {
        CobblemonLuckPermsNPCCompat.INSTANCE.createInfoLog("Registering Cobblemon Molang Player function extensions");

        MoLangFunctions.INSTANCE.getPlayerFunctions().add(player -> {
            HashMap<String, Function<MoParams, Object>> map = new HashMap<>();

            // q.player.lp_promote(<track string>, <dont-add-to-first int-as-boolean>) 0
            map.put("lp_promote", params -> {
                String track = params.getString(0);
                boolean dontAddToFirst = params.getInt(1) != 0;
                ServerPlayer serverPlayer = (ServerPlayer) player;
                User user = LuckPermsHelper.INSTANCE.getUser(serverPlayer);
                if (user == null) return 1;
                var trackManager = LuckPermsHelper.INSTANCE.getLuckPerms().getTrackManager();
                Track trackEntry = trackManager.getTrack(track);
                if (trackEntry == null) return 1;
                if (dontAddToFirst && !LuckPermsHelper.INSTANCE.isUserOnTrack(serverPlayer, track)) {
                    return 1;
                }
                trackEntry.promote(user, ImmutableContextSet.empty());
                LuckPermsHelper.INSTANCE.saveUser(user);
                LuckPermsHelper.INSTANCE.saveTrack(trackEntry);
                return 0;
            });

            // q.player.lp_demote(<track string>, <dont-remove-from-first int-as-boolean>) 0
            map.put("lp_demote", params -> {
                String track = params.getString(0);
                boolean dontRemoveFromFirst = params.getInt(1) != 0;
                ServerPlayer serverPlayer = (ServerPlayer) player;
                User user = LuckPermsHelper.INSTANCE.getUser(serverPlayer);
                if (user == null) return 1;
                var trackManager = LuckPermsHelper.INSTANCE.getLuckPerms().getTrackManager();
                Track trackEntry = trackManager.getTrack(track);
                if (trackEntry == null) return 1;

                List<String> currentUserGroups = LuckPermsHelper.INSTANCE.getUserGroups(serverPlayer);
                List<String> trackGroups = trackEntry.getGroups();
                String firstTrackGroup = trackEntry.getGroups().getFirst();

                boolean isOnTrack = currentUserGroups.stream().anyMatch(trackGroups::contains);
                boolean isInFirstGroup = currentUserGroups.stream().anyMatch(group -> group.equals(firstTrackGroup));

                if (!isOnTrack) {
                    return 1;
                }

                if (dontRemoveFromFirst && isInFirstGroup) {
                    return 1;
                }

                trackEntry.demote(user, ImmutableContextSet.empty());
                LuckPermsHelper.INSTANCE.saveUser(user);
                LuckPermsHelper.INSTANCE.saveTrack(trackEntry);
                return 0;
            });

            // q.player.lp_permission_set(<node string>, <value int-as-boolean>) 0
            map.put("lp_permission_set", params -> {
                String node = params.getString(0);
                boolean value = params.getInt(1) != 0;
                ServerPlayer serverPlayer = (ServerPlayer) player;
                if (LuckPermsHelper.INSTANCE.hasPermissionNode(serverPlayer, node)) {
                    return 0;
                }
                User user = LuckPermsHelper.INSTANCE.getUser(serverPlayer);
                if (user == null) return 1;
                user.data().add(Node.builder(node).value(value).build());
                LuckPermsHelper.INSTANCE.saveUser(user);
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
                User user = LuckPermsHelper.INSTANCE.getUser(serverPlayer);
                long exp = StringUtils.convertToSecondsFromNow(duration);
                if (user == null) return 1;
                user.data().add(Node.builder(node).value(value).expiry(exp).build());
                LuckPermsHelper.INSTANCE.saveUser(user);
                return 0;
            });

            // q.player.lp_permission_unsettemp(<node string>) 0
            map.put("lp_permission_unsettemp", params -> sharedRemovePermissionNode(player, params));

            // q.player.lp_permission_check(<node string>) Boolean-As-Double
            map.put("lp_permission_check", params -> {
                String node = params.getString(0);
                ServerPlayer serverPlayer = (ServerPlayer) player;
                if (LuckPermsHelper.INSTANCE.hasPermissionNode(serverPlayer, node)) {
                    return new DoubleValue(1);
                }
                return new DoubleValue(0);
            });

            // q.player.lp_parent_set(<group string>) 0
            map.put("lp_parent_set", params -> {
                String group = params.getString(0);
                ServerPlayer serverPlayer = (ServerPlayer) player;
                User user = LuckPermsHelper.INSTANCE.getUser(serverPlayer);
                if (user == null) return 1;

                List<String> userGroups = LuckPermsHelper.INSTANCE.getUserGroups(serverPlayer);

                // first remove from existing groups
                for (String userGroup : userGroups) {
                    LuckPermsHelper.INSTANCE.removeUserParentGroup(user, userGroup);
                }

                // Then add the user to the new parent
                LuckPermsHelper.INSTANCE.addUserParentGroup(serverPlayer, group);
                return 0;
            });

            // q.player.lp_parent_add(<group string>) 0
            map.put("lp_parent_add", params -> {
                String group = params.getString(0);
                ServerPlayer serverPlayer = (ServerPlayer) player;
                LuckPermsHelper.INSTANCE.addUserParentGroup(serverPlayer, group);
                return 0;
            });

            // q.player.lp_parent_remove(<group string>) 0
            map.put("lp_parent_remove", params -> sharedRemoveParentGroup(player, params));

            // q.player.lp_parent_settrack(<track string>, <group string>) 0
            map.put("lp_parent_settrack", params -> {
                String track = params.getString(0);
                String group = params.contains(1) ? params.getString(1) : null;
                ServerPlayer serverPlayer = (ServerPlayer) player;
                TrackManager trackManager = LuckPermsHelper.INSTANCE.getLuckPerms().getTrackManager();
                Track trackObj = trackManager.getTrack(track);
                if (trackObj == null) return 1;

                List<String> trackGroups = trackObj.getGroups();
                List<String> userGroups = LuckPermsHelper.INSTANCE.getUserGroups(serverPlayer);

                for (String userGroup : userGroups) {
                    if (trackGroups.contains(userGroup)) return 1;
                }

                if (group != null) {
                    if (!trackGroups.contains(group)) {
                        return 1;
                    }

                    LuckPermsHelper.INSTANCE.addUserParentGroup(serverPlayer, group);
                }

                String trackFirstGroup = trackGroups.getFirst();
                LuckPermsHelper.INSTANCE.addUserParentGroup(serverPlayer, trackFirstGroup);
                return 0;
            });

            // q.player.lp_parent_addtemp(<group string>, <duration string>) 0
            map.put("lp_parent_addtemp", params -> {
                String group = params.getString(0);
                String duration = params.getString(1);
                ServerPlayer serverPlayer = (ServerPlayer) player;
                User user = LuckPermsHelper.INSTANCE.getUser(serverPlayer);
                if (user == null) return 1;
                long exp = StringUtils.convertToSecondsFromNow(duration);
                user.data().add(InheritanceNode.builder(group).expiry(exp).build());
                LuckPermsHelper.INSTANCE.saveUser(user);
                return 0;
            });

            // q.player.lp_parent_removetemp(<group string>) 0
            map.put("lp_parent_removetemp", params -> sharedRemoveParentGroup(player, params));

            // q.player.lp_meta_set(<key string>, <value string>) 0
            map.put("lp_meta_set", params -> {
                String key = params.getString(0);
                String value = params.getString(1);
                ServerPlayer serverPlayer = (ServerPlayer) player;
                User user = LuckPermsHelper.INSTANCE.getUser(serverPlayer);
                if (user == null) return 0;
                MetaNode node = MetaNode.builder(key, value).build();
                LuckPermsHelper.INSTANCE.clearMetaKey(user, key);
                user.data().add(node);
                LuckPermsHelper.INSTANCE.saveUser(user);
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
                User user = LuckPermsHelper.INSTANCE.getUser(serverPlayer);
                if (user == null) return 0;
                long exp = StringUtils.convertToSecondsFromNow(duration);
                MetaNode node = MetaNode.builder(key, value).expiry(exp).build();
                LuckPermsHelper.INSTANCE.clearMetaKey(user, key);
                user.data().add(node);
                LuckPermsHelper.INSTANCE.saveUser(user);
                return 0;
            });

            // q.player.lp_meta_unsettemp(<key string>) 0
            map.put("lp_meta_unsettemp", params -> sharedRemoveUserMetaData(player, params));

            // q.player.lp_meta_get(<key string>) string
            map.put("lp_meta_get", params -> {
                String key = params.getString(0);
                ServerPlayer serverPlayer = (ServerPlayer) player;
                User user = LuckPermsHelper.INSTANCE.getUser(serverPlayer);
                if (user == null) return null;
                return user.getCachedData().getMetaData().getMetaValue(key);
            });

            return map;
        });
    }
}
