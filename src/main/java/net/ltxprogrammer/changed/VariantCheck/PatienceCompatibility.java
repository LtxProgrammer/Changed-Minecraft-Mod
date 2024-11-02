package net.ltxprogrammer.changed.VariantCheck;

import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public class PatienceCompatibility {
    private static int variantType = -1;
    private static boolean conditionValue = false;
    private static Player player;

    public PatienceCompatibility(Player player) {
        this.player = player;
        applyVariantLogic();
    }

    private static void applyVariantLogic() {
        // 根据 variantType 选择行为
        switch (variantType) {
            case 0 -> conditionValue = true; // 耐心行为
            case 1 -> conditionValue = false; // 兼容性行为
            default -> conditionValue = false; // 默认行为，如果 variantType 未定义 (-1)
        }
    }

    public static void OriginCondition(MinecraftServer server) {
        // 确保 player 不为空
        if (player == null) {
            return;
        }

        // 使用 OriginsAPI 获取 OriginLayer 和 Origin 实例
        OriginLayer layer = OriginsAPI.getLayersRegistry(server).get(new ResourceLocation("origins:origin"));
        Origin origin = OriginsAPI.getOriginsRegistry(server).get(new ResourceLocation("origins:human"));

            boolean nowHuman = hasOrigin(player, layer, origin);
            if (nowHuman) {
                variantType = 1;
            } else {
                variantType = 0;
            }
            applyVariantLogic(); // 确保更新 conditionValue

    }

    public static boolean isConditionMet() {
        return conditionValue;
    }

    private static boolean hasOrigin(Player player, OriginLayer layer, Origin origin) {
        return IOriginContainer.get(player).map((x) -> Objects.equals(x.getOrigin(layer), origin)).orElse(false);
    }
}
