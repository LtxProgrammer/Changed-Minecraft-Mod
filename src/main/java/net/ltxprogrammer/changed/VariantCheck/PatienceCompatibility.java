package net.ltxprogrammer.changed.VariantCheck;

import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.minecraft.server.level.ServerPlayer;

import static net.ltxprogrammer.changed.VariantCheck.OriginUtils.isHumanOrigin;

public class PatienceCompatibility {
    int VariantType = Integer.parseInt(null);

    public PatienceCompatibility() {
        if (VariantType == 0) {
        //Patience


        } else if (VariantType == 1) {
        //Compatibility


        } else {


        }
    }
    public int OriginChecker(ServerPlayer player, OriginLayer layer){
        boolean isHuman = isHumanOrigin(player, layer);
        if (isHuman) {
            VariantType = Integer.parseInt(null);
        } else {
            VariantType = 1;
        }
        return VariantType;
    }
}
