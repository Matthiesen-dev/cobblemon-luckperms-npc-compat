package dev.matthiesen.common.cobblemon_luckperms_npc_compat.lp;

import dev.matthiesen.common.cobblemon_luckperms_npc_compat.Constants;
import dev.matthiesen.common.cobblemon_luckperms_npc_compat.util.StringUtils;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.track.Track;
import net.luckperms.api.track.TrackManager;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class LPManager {
    private static LuckPerms luckPerms;

    public LPManager getInstance() {
        return this;
    }

    public LuckPerms getLuckPerms() {
        if (luckPerms == null) {
            try {
                luckPerms = LuckPermsProvider.get();
                Constants.createInfoLog("LuckPerms API loaded successfully");
            } catch (IllegalStateException e) {
                Constants.createErrorLog("LuckPerms not available");
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

    public void addUserParentTempGroup(ServerPlayer player, String group, String duration) {
        User user = getLPUser(player);
        if (user == null) return;
        long exp = StringUtils.convertToSecondsFromNow(duration);
        user.data().add(InheritanceNode.builder(group).expiry(exp).build());
        saveUser(user);
    }

    public void removeUserParentGroup(User user, String group) {
        user.data().remove(InheritanceNode.builder(group).build());
    }

    public void removeUserParentGroup(ServerPlayer player, String group) {
        User user = getLPUser(player);
        if (user == null) return;
        removeUserParentGroup(user, group);
        saveUser(user);
    }

    public void setUserParentGroup(ServerPlayer player, String group) {
        User user = getLPUser(player);
        if (user == null) return;

        List<String> userGroups = getUserGroups(player);

        // first remove from existing groups
        for (String userGroup : userGroups) {
            removeUserParentGroup(user, userGroup);
        }

        // Then add the user to the new parent
        addUserParentGroup(player, group);
    }

    public boolean hasPermissionNode(ServerPlayer player, String node) {
        User user = getLPUser(player);
        if (user == null) return false;
        return user.getCachedData().getPermissionData().checkPermission(node).asBoolean();
    }

    public void addPermission(ServerPlayer player, String permission, Boolean value) {
        User user = getLPUser(player);
        user.data().add(Node.builder(permission).value(value).build());
        saveUser(user);
    }

    public void addTempPermission(ServerPlayer player, String permission, Boolean value, String expiry) {
        User user = getLPUser(player);
        long exp = StringUtils.convertToSecondsFromNow(expiry);
        user.data().add(Node.builder(permission).value(value).expiry(exp).build());
        saveUser(user);
    }

    public void removePermission(ServerPlayer player, String permission) {
        User user = getLPUser(player);
        user.data().remove(Node.builder(permission).build());
        saveUser(user);
    }

    public List<String> getUserGroups(ServerPlayer player) {
        User user = getLPUser(player);
        return user.getNodes(NodeType.INHERITANCE).stream()
                .map(InheritanceNode::getGroupName)
                .collect(Collectors.toList());
    }

    public boolean isUserOnTrack(ServerPlayer player, String track) {
        TrackManager trackManager = getLPTrackManager();

        Track trackObj = trackManager.getTrack(track);
        if (trackObj == null) return false;

        List<String> trackGroups = trackObj.getGroups();
        List<String> userGroups = getUserGroups(player);

        for (String userGroup : userGroups) {
            if (trackGroups.contains(userGroup)) return true;
        }
        return false;
    }

    public void addUserToTrack(ServerPlayer player, String track, @Nullable String group) {
        TrackManager trackManager = getLPTrackManager();
        Track trackObj = trackManager.getTrack(track);
        if (trackObj == null) return;

        List<String> trackGroups = trackObj.getGroups();
        List<String> userGroups = getUserGroups(player);

        for (String userGroup : userGroups) {
            if (trackGroups.contains(userGroup)) return;
        }

        if (group != null) {
            if (!trackGroups.contains(group)) {
                return;
            }

            addUserParentGroup(player, group);
        }

        String trackFirstGroup = trackGroups.getFirst();
        addUserParentGroup(player, trackFirstGroup);
    }

    public void promoteUserOnTrack(ServerPlayer player, String track, boolean dontAddToFirst) {
        User user = getLPUser(player);
        if (user == null) return;
        Track trackEntry = getLPTrackManager().getTrack(track);
        if (trackEntry == null) return;
        if (dontAddToFirst && !isUserOnTrack(player, track)) {
            return;
        }
        trackEntry.promote(user, ImmutableContextSet.empty());
        saveUser(user);
        saveTrack(trackEntry);
    }

    public void demoteUserOnTrack(ServerPlayer player, String track, boolean dontRemoveFromFirst) {
        User user = getLPUser(player);
        if (user == null) return;
        Track trackEntry = getLPTrackManager().getTrack(track);
        if (trackEntry == null) return;

        List<String> currentUserGroups = getUserGroups(player);
        List<String> trackGroups = trackEntry.getGroups();
        String firstTrackGroup = trackEntry.getGroups().getFirst();

        boolean isOnTrack = currentUserGroups.stream().anyMatch(trackGroups::contains);
        boolean isInFirstGroup = currentUserGroups.stream().anyMatch(group -> group.equals(firstTrackGroup));

        if (!isOnTrack) {
            return;
        }

        if (dontRemoveFromFirst && isInFirstGroup) {
            return;
        }

        trackEntry.demote(user, ImmutableContextSet.empty());
        saveUser(user);
        saveTrack(trackEntry);
    }

    public String getUserMetaData(ServerPlayer player, String MetaKey) {
        User user = getLPUser(player);
        if (user == null) return null;
        return user.getCachedData().getMetaData().getMetaValue(MetaKey);
    }

    public void clearMetaKey(User user, String metaKey) {
        user.data().clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals(metaKey)));
    }

    public void removeUserMetaData(ServerPlayer player, String metaKey) {
        User user = getLPUser(player);
        if (user == null) return;
        clearMetaKey(user, metaKey);
        saveUser(user);
    }

    public void setUserMetaData(ServerPlayer player, String metaKey, String value) {
        User user = getLPUser(player);
        if (user == null) return;
        MetaNode node = MetaNode.builder(metaKey, value).build();
        clearMetaKey(user, metaKey);
        user.data().add(node);
        saveUser(user);
    }

    public void setTempUserMetaData(ServerPlayer player, String MetaKey, String value, String duration) {
        User user = getLPUser(player);
        if (user == null) return;
        long exp = StringUtils.convertToSecondsFromNow(duration);
        MetaNode node = MetaNode.builder(MetaKey, value).expiry(exp).build();
        clearMetaKey(user, MetaKey);
        user.data().add(node);
        saveUser(user);
    }
}
