package net.ltxprogrammer.changed.latexvariant;

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
    public int varianttypenumber = 0;

    public CheckCondition(ServerPlayer player) {
        this.player = player;
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


    public boolean isConditionMet() {
        return conditionValue;
    }

    public int VariantTypeNumber() {
        return varianttypenumber;
    }

    public void checkOriginCondition(MinecraftServer server) {
        if ((ModList.get().isLoaded("origins"))) {
           OriginLayer layer = OriginsAPI.getLayersRegistry(server).get(new ResourceLocation("origins:origin"));
           Origin origin = OriginsAPI.getOriginsRegistry(server).get(new ResourceLocation("origins:human"));
            boolean nowHuman = IOriginContainer.get(player)
                        .map(container -> Objects.equals(container.getOrigin(layer), origin))
                        .orElse(false);
                if(nowHuman){
                    varianttypenumber = 0;
                }else{
                    varianttypenumber = 1;
                }
            if (!(Changed.config.server.enableTransfurringOrigins.get())) {
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