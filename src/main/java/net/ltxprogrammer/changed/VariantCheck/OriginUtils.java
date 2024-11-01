package net.ltxprogrammer.changed.VariantCheck;

import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;
import java.util.Optional;

/**
 * Utility class for handling Origins-related functionality.
 * 用于处理与 Origin 相关功能的工具类。
 * Provides methods to retrieve the current origin of a player
 * and to check if a player has a specific origin.
 * 提供方法来获取玩家的当前 Origin 以及检查玩家是否具有特定的 Origin。
 */
public class OriginUtils {

    /**
     * Retrieves the current Origin of a specified player on a given OriginLayer.
     * 获取指定玩家在给定 OriginLayer 上的当前 Origin。
     *
     * @param player The player whose origin is being checked.
     *               要检查的玩家。
     * @param layer The origin layer to check for the player's current origin.
     *              要检查的 OriginLayer。
     * @return The current Origin of the player on the specified layer, or null if none is set.
     *         玩家在指定层上的当前 Origin，如果没有设置则返回 null。
     */
    public static Origin getCurrentOrigin(ServerPlayer player, OriginLayer layer) {
        Optional<IOriginContainer> originContainer = IOriginContainer.get(player).resolve();
        return originContainer.map(container -> container.getOrigin(layer)).orElse(null);
    }

    /**
     * Checks if the specified player's current origin on a given OriginLayer is "human."
     * 检查指定玩家在给定 OriginLayer 上的当前 Origin 是否为 "human"。
     *
     * @param player The player to check.
     *               要检查的玩家。
     * @param layer The origin layer to check for the "human" origin.
     *              要检查的 OriginLayer。
     * @return True if the player's origin on the specified layer is "human," false otherwise.
     *         如果玩家在指定层上的 Origin 是 "human"，则返回 true；否则返回 false。
     */
    public static boolean isHumanOrigin(ServerPlayer player, OriginLayer layer) {
        Origin origin = getCurrentOrigin(player, layer);
        return origin != null && "human".equals(Objects.requireNonNull(origin.getRegistryName()).getPath());
    }
}


