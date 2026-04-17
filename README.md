# Cobblemon LuckPerms NPC Compat

This mod adds compatibility for Cobblemon NPCs to interact with the LuckPerms Permissions API.

## Requirements
- [Cobblemon 1.7.3+](https://modrinth.com/mod/cobblemon)
- [Luckperms 5.4](https://modrinth.com/mod/luckperms)

## Cobblemon NPC Extensions

### Player functions

#### `lp_promote`

Promotes the player along the specified track.

- `q.player.lp_promote(<track>, <dont-add-to-first>)`

**Parameters:**
- `track` (string): The name of the track to promote along.
- `dont-add-to-first` (integer): If set to `1`, the player will not be added to the first group in the track if they are not already in a group in that track. Use `0` for default behavior.

#### `lp_demote`

Demotes the player along the specified track.

- `q.player.lp_demote(<track>, <dont-remove-from-first>)`

**Parameters:**
- `track` (string): The name of the track to demote along.
- `dont-remove-from-first` (integer): If set to `1`, the player will not be removed from the first group in the track if they are demoted from it. Use `0` for default behavior.

#### `lp_permission_set`

Sets a specific permission for the player.

- `q.player.lp_permission_set(<permission>, <value>)`

**Parameters:**
- `permission` (string): The permission node to set.
- `value` (integer): The value to set for the permission. Use `1` to grant the permission and `0` to revoke it.

#### `lp_permission_unset`

Unsets a specific permission for the player, reverting it to its default state.

- `q.player.lp_permission_unset(<permission>)`

**Parameters:**
- `permission` (string): The permission node to unset.

#### `lp_permission_settemp`

Sets a specific permission for the player temporarily.

- `q.player.lp_permission_settemp(<permission>, <value>, <duration>)`

**Parameters:**
- `permission` (string): The permission node to set temporarily.
- `value` (integer): The value to set for the permission. Use `1` to grant the permission and `0` to revoke it.
- `duration` (string): The duration for which the permission should be set, in a format like `1d 2h 30m` (1 day, 2 hours, and 30 minutes).

#### `lp_permission_unsettemp`

Unsets a specific temporary permission for the player, reverting it to its default state.

- `q.player.lp_permission_unsettemp(<permission>)`

**Parameters:**
- `permission` (string): The permission node to unset temporarily.

#### `lp_permission_check`

Checks if the player has a specific permission.

- `q.player.lp_permission_check(<permission>)`

**Parameters:**
- `permission` (string): The permission node to check.

**Returns:**
- `1` if the player has the permission, `0` otherwise. This value is represented as a Double, so you may need to convert it to an integer in your scripts.

#### `lp_parent_set`

Sets a specific parent group for the player.

- `q.player.lp_parent_set(<group>)`

**Parameters:**
- `group` (string): The name of the group to set as a parent for the player.

#### `lp_parent_add`

Adds a specific parent group to the player.

- `q.player.lp_parent_add(<group>)`

**Parameters:**
- `group` (string): The name of the group to add as a parent for the player.

#### `lp_parent_remove`

Removes a specific parent group from the player.

- `q.player.lp_parent_remove(<group>)`

**Parameters:**
- `group` (string): The name of the group to remove as a parent from the player.

#### `lp_parent_settrack`

Sets the player's parent group based on their current group in a specified track.

- `q.player.lp_parent_settrack(<track>, [group])`

**Parameters:**
- `track` (string): The name of the track to check for the player's current group.
- `group` (string, optional): If provided, the player will only be set to the group if their current group in the track matches this value. If not provided, the player will be set to the group corresponding to their current group in the track regardless of what it is.

#### `lp_parent_addtemp`

Adds a specific parent group to the player temporarily.

- `q.player.lp_parent_addtemp(<group>, <duration>)`

**Parameters:**
- `group` (string): The name of the group to add as a parent for the player temporarily.
- `duration` (string): The duration for which the parent group should be added, in a format like `1d 2h 30m` (1 day, 2 hours, and 30 minutes).

#### `lp_parent_removetemp`

Removes a specific temporary parent group from the player.

- `q.player.lp_parent_removetemp(<group>)`

**Parameters:**
- `group` (string): The name of the group to remove as a parent from the player.

#### `lp_meta_set`

Sets a specific meta key-value pair for the player.

- `q.player.lp_meta_set(<key>, <value>)`

**Parameters:**
- `key` (string): The meta key to set.
- `value` (string): The value to set for the meta key.

#### `lp_meta_unset`

Unsets a specific meta key for the player, reverting it to its default state.

- `q.player.lp_meta_unset(<key>)`

**Parameters:**
- `key` (string): The meta key to unset.

#### `lp_meta_settemp`

Sets a specific meta key-value pair for the player temporarily.

- `q.player.lp_meta_settemp(<key>, <value>, <duration>)`

**Parameters:**
- `key` (string): The meta key to set temporarily.
- `value` (string): The value to set for the meta key.
- `duration` (string): The duration for which the meta key-value pair should be set, in a format like `1d 2h 30m` (1 day, 2 hours, and 30 minutes).

#### `lp_meta_unsettemp`

Unsets a specific temporary meta key for the player, reverting it to its default state.

- `q.player.lp_meta_unsettemp(<key>)`

**Parameters:**
- `key` (string): The meta key to unset temporarily.

#### `lp_meta_get`

Gets the value of a specific meta key for the player.

- `q.player.lp_meta_get(<key>)`

**Parameters:**
- `key` (string): The meta key to get the value of.

**Returns:**
- The string value of the specified meta key for the player, or `null` if the key does not exist.