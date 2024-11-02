package net.ltxprogrammer.changed.VariantCheck;

import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

public class PatienceCompatibility {
    private final ServerPlayer player;  // 将类型更改为 ServerPlayer
    private int variantType = -1;
    private boolean conditionValue = false;

    // 修改构造函数以接受 ServerPlayer 和 MinecraftServer
    public PatienceCompatibility(ServerPlayer player) {
        this.player = player;
    }

    // 检查 Origin 条件并更新变身条件
    public void checkOriginCondition(MinecraftServer server) {
        OriginLayer layer = OriginsAPI.getLayersRegistry(server).get(new ResourceLocation("origins:origin"));
        Origin origin = OriginsAPI.getOriginsRegistry(server).get(new ResourceLocation("origins:human"));

        boolean nowHuman = hasOrigin(player, layer, origin);
        variantType = nowHuman ? 1 : 0;
        applyVariantLogic();
    }

    // 检查玩家是否满足条件
    public boolean isConditionMet() {
        return conditionValue;
    }

    private void applyVariantLogic() {
        switch (variantType) {
            case 0 -> conditionValue = true; // 耐心行为
            case 1 -> conditionValue = false; // 兼容性行为
            default -> conditionValue = false; // 默认行为
        }
    }

    private boolean hasOrigin(ServerPlayer player, OriginLayer layer, Origin origin) {
        return IOriginContainer.get(player).map(x -> Objects.equals(x.getOrigin(layer), origin)).orElse(false);
    }
}
