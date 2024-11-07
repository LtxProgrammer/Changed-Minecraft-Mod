package net.ltxprogrammer.changed.latexvariant;

import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.ltxprogrammer.changed.Changed;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;


public class CheckCondition {
    private final ServerPlayer player;
    private static boolean conditionValue = false;
    private static VariantType variantType = VariantType.DEFAULT;
    public static final Logger LOGGER = LogManager.getLogger(Changed.class);

    public CheckCondition(ServerPlayer player) {
        this.player = player;
        checkOriginCondition(player.getServer());
    }

    private void applyVariantLogic() {
        switch (variantType) {
            case LATEX_RESISTANCE -> conditionValue = true;
            default -> conditionValue = false;
        }

    }

    public boolean isConditionMet() {
        return conditionValue;
    }

    public void checkOriginCondition(MinecraftServer server) {
        OriginLayer layer = OriginsAPI.getLayersRegistry(server).get(new ResourceLocation("origins:origin"));
        Origin origin = OriginsAPI.getOriginsRegistry(server).get(new ResourceLocation("origins:human"));

        if (layer == null || origin == null) {
            LOGGER.error("Failed to retrieve Origin layer or origin from registry.");
            return;
        }

        boolean nowHuman = IOriginContainer.get(player)
                .map(container -> Objects.equals(container.getOrigin(layer), origin))
                .orElse(false);
        variantType = nowHuman ? VariantType.DEFAULT : VariantType.LATEX_RESISTANCE;

        if (Changed.config.common.enableTransfurringOrigins.get()) {
            applyVariantLogic();
        }
    }
}