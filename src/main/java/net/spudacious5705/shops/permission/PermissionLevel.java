package net.spudacious5705.shops.permission;

import org.intellij.lang.annotations.MagicConstant;

/// Why is this here?
/// todo move this to its own package along with the delegates class in AbstractShopEntity
///
public enum PermissionLevel {
    SERVER_ADMIN(true, false, false, false, false, false, -1),
    OWNER(true, true, true, true, true, true, 4),
    MANAGER(false, true, true, true, false, true, 3),
    SUPERVISOR(false, false, true, true, false, true, 2),
    CLERK(false, false, true, false, false, true, 1),
    CUSTOMER(false, false, false, false, false, false, 0);

    @MagicConstant
    static final PermissionLevel[] CONTRACT_PERMS = {
            OWNER,
            MANAGER,
            SUPERVISOR,
            CLERK
    };

    final boolean breakBlock;
    final boolean editPermissions;
    final boolean importStock;
    final boolean takeItems;
    final boolean editTrades;
    final boolean viewShopScreen;
    final int level;

    PermissionLevel(
            boolean breakBlock,
            boolean editPermissions,
            boolean importStock,
            boolean takeItems,
            boolean editTrades,
            boolean viewShopScreen,
            int level
    ) {
        this.breakBlock = breakBlock;
        this.editPermissions = editPermissions;
        this.importStock = importStock;
        this.takeItems = takeItems;
        this.editTrades = editTrades;
        this.viewShopScreen = viewShopScreen;
        this.level = level;
    }

    public static PermissionLevel fromInt(int queryInt) {
        for (PermissionLevel lvl : values()) {
            if (lvl.asInt() == queryInt) {
                return lvl;
            }
        }
        return PermissionLevel.CUSTOMER;
    }

    public boolean canBreakBlock() {
        return breakBlock;
    }

    public boolean canImportStock() {
        return importStock;
    }

    public boolean canTakeItems() {
        return takeItems;
    }

    public boolean canEditTrades() {
        return editTrades;
    }

    public boolean canViewShopScreen() {
        return viewShopScreen;
    }

    public boolean canEditPermissions() {
        return editPermissions;
    }

    public int asInt() {
        return level;
    }
}
