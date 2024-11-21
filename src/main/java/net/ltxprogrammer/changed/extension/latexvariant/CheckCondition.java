package net.ltxprogrammer.changed.extension.latexvariant;

import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.ltxprogrammer.changed.Changed;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class CheckCondition {
    private final ServerPlayer player;
    private boolean conditionValue = false;
    public VariantType variantType = VariantType.DEFAULT;
    public static final Logger LOGGER = LogManager.getLogger(Changed.class);
    public CheckCondition(ServerPlayer player) {
        this.player = player;
        checkOriginCondition();
    }
    private void applyVariantLogic() {
        switch (variantType) {
            case ORIGINS -> {
                conditionValue = true;
            }
            default -> {
                conditionValue = false;
            }
        }
    }
    public boolean getConditionMet() {
        return conditionValue;
    }
    public void checkOriginCondition() {
        if ((ModList.get().isLoaded("origins"))) {
            MinecraftServer server=this.player.getServer();
            OriginLayer layer = OriginsAPI.getLayersRegistry(server).get(new ResourceLocation("origins:origin"));
            Origin origin = OriginsAPI.getOriginsRegistry(server).get(new ResourceLocation("origins:human"));
            boolean nowHuman = IOriginContainer.get(player)
                        .map(container -> Objects.equals(container.getOrigin(layer), origin))
                        .orElse(false);
            if(!(Changed.config.server.enableTransfurringOrigins.get())) {
               if (nowHuman) {
                   variantType = VariantType.DEFAULT;
               } else {
                   variantType = VariantType.ORIGINS;
               }
               applyVariantLogic();
           }
        }
    }
}
enum VariantType {
    DEFAULT(0),
    ORIGINS(1);
    private final int varianttypenumber;
    VariantType(int varianttypenumber) {
        this.varianttypenumber = varianttypenumber;
    }
    public int getVariantTypeNumber() {
        return this.varianttypenumber;
    }
}