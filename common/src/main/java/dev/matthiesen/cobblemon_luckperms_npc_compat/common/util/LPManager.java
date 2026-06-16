package dev.matthiesen.cobblemon_luckperms_npc_compat.common.util;

import dev.matthiesen.cobblemon_luckperms_npc_compat.common.CobblemonLuckPermsNPCCompat;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.track.Track;
import net.luckperms.api.track.TrackManager;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class LPManager {
    private static LuckPerms luckPerms;

    public LPManager getInstance() {
        return this;
    }

    public LuckPerms getLuckPerms() {
        if (luckPerms == null) {
            try {
                luckPerms = LuckPermsProvider.get();
                CobblemonLuckPermsNPCCompat.INSTANCE.createInfoLog("LuckPerms API loaded successfully");
            } catch (IllegalStateException e) {
                CobblemonLuckPermsNPCCompat.INSTANCE.createErrorLog("LuckPerms not available", e);
                return null;
            }
        }
        return luckPerms;
    }

    public User getLPUser(ServerPlayer player) {
        LuckPerms lp = getLuckPerms();
        if (lp == null) return null;
        UserManager userManager = lp.getUserManager();
        CompletableFuture<User> asyncUser = userManager.loadUser(player.getUUID());
        return asyncUser.join();
    }

    public TrackManager getLPTrackManager() {
        LuckPerms lp = getLuckPerms();
        if (lp == null) return null;
        return lp.getTrackManager();
    }

    public void saveUser(User user) {
        LuckPerms lp = getLuckPerms();
        if (lp == null) return;
        UserManager userManager = lp.getUserManager();
        userManager.saveUser(user);
    }

    public void saveTrack(Track track) {
        LuckPerms lp = getLuckPerms();
        if (lp == null) return;
        TrackManager trackManager = lp.getTrackManager();
        trackManager.saveTrack(track);
    }

    public void addUserParentGroup(ServerPlayer player, String group) {
        User user = getLPUser(player);
        if (user == null) return;
        user.data().add(InheritanceNode.builder(group).build());
        saveUser(user);
    }

    public void removeUserParentGroup(User user, String group) {
        user.data().remove(InheritanceNode.builder(group).build());
    }

    public boolean hasPermissionNode(ServerPlayer player, String node) {
        User user = getLPUser(player);
        if (user == null) return false;
        return user.getCachedData().getPermissionData().checkPermission(node).asBoolean();
    }

    public List<String> getUserGroups(ServerPlayer player) {
        User user = getLPUser(player);
        if (user == null) return List.of();
        return user.getNodes(NodeType.INHERITANCE).stream()
                .map(InheritanceNode::getGroupName)
                .collect(Collectors.toList());
    }

    public boolean isUserOnTrack(ServerPlayer player, String track) {
        TrackManager trackManager = getLPTrackManager();
        if (trackManager == null) return false;
        Track trackObj = trackManager.getTrack(track);
        if (trackObj == null) return false;

        List<String> trackGroups = trackObj.getGroups();
        List<String> userGroups = getUserGroups(player);

        for (String userGroup : userGroups) {
            if (trackGroups.contains(userGroup)) return true;
        }
        return false;
    }

    public void clearMetaKey(User user, String metaKey) {
        user.data().clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals(metaKey)));
    }
}
