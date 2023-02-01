package net.ltxprogrammer.changed.process;

import net.ltxprogrammer.changed.init.ChangedDamageSources;
import net.ltxprogrammer.changed.init.ChangedGameRules;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

public class Pale {
    private static final Field paleExposureField;

    static {
        Field paleExposureField1;
        try {
            paleExposureField1 = Player.class.getField("paleExposure");
        } catch (NoSuchFieldException e) {
            paleExposureField1 = null;
            e.printStackTrace();
        }
        paleExposureField = paleExposureField1;
    }

    public static void setPaleExposure(Player player, int level) {
        try {
            paleExposureField.set(player, level);
        } catch (IllegalAccessException unused) {}
    }

    public static int getPaleExposure(Player player) {
        try {
            return (Integer)paleExposureField.get(player);
        } catch (IllegalAccessException unused) {
            return 0;
        }
    }

    public static boolean isCured(Player player) {
        return ProcessTransfur.isPlayerLatex(player) && getPaleExposure(player) < 0;
    }

    public static boolean tryCure(Player player) {
        if (ProcessTransfur.isPlayerLatex(player)) {
            setPaleExposure(player, -2000);
            return true;
        }

        return false;
    }

    public static int THRESHOLD_IMMUNE_MAX = 1200; // How much your immune system can take until Pale begins to take over
    public static int THRESHOLD_MINIMAL_DAMAGE = 24000; // 1 minecraft day
    public static int THRESHOLD_SMALL_DAMAGE = 48000; // 2 minecraft days
    public static int THRESHOLD_LARGE_DAMAGE = 60000; // 2.5 minecraft days
    public static int THRESHOLD_DEATH = 72000; // 3 minecraft days

    public static void tickPaleExposure(Player player) {
        if (isCured(player))
            return;
        if (!player.level.getGameRules().getBoolean(ChangedGameRules.RULE_DO_PALE))
            return;
        if (player.isCreative() || player.isSpectator())
            return;

        int exposure = getPaleExposure(player);

        AtomicInteger localExposure = new AtomicInteger(0);
        player.level.getEntitiesOfClass(LivingEntity.class, new AABB(player.blockPosition()).inflate(1.5)).forEach(livingEntity -> {
            if (livingEntity.getType().is(ChangedTags.EntityTypes.PALE_SMALL_EXPOSURE))
                localExposure.addAndGet(1);
            else if (livingEntity.getType().is(ChangedTags.EntityTypes.PALE_LARGE_EXPOSURE))
                localExposure.addAndGet(2); // Standing next to a zombie will tick you over immune exposure
            else if (livingEntity instanceof Player otherPlayer) {
                int otherExposure = getPaleExposure(otherPlayer);
                if (otherExposure < THRESHOLD_IMMUNE_MAX)
                    return;
                localExposure.addAndGet(1);
                if (otherExposure >= THRESHOLD_SMALL_DAMAGE)
                    localExposure.addAndGet(1);
                if (otherExposure >= THRESHOLD_LARGE_DAMAGE)
                    localExposure.addAndGet(1);
            }
        });

        exposure += localExposure.getAcquire();
        if (exposure >= 0 && exposure < THRESHOLD_IMMUNE_MAX)
            exposure--;
        if (exposure >= THRESHOLD_IMMUNE_MAX)
            exposure++;

        // VVV effects VVV
        if (exposure >= THRESHOLD_MINIMAL_DAMAGE && exposure < THRESHOLD_SMALL_DAMAGE) {
            if (exposure % 1200 == 0) { // 1/2 Heart per minute
                player.hurt(ChangedDamageSources.PALE, 1f);
            }
        } else if (exposure >= THRESHOLD_SMALL_DAMAGE && exposure < THRESHOLD_LARGE_DAMAGE) {
            if (exposure % 600 == 0) { // 1/2 Heart per 30 seconds
                player.hurt(ChangedDamageSources.PALE, 1f);
            }
        } else if (exposure >= THRESHOLD_LARGE_DAMAGE && exposure < THRESHOLD_DEATH) {
            if (exposure % 300 == 0) { // 1/2 Heart per 15 seconds
                player.hurt(ChangedDamageSources.PALE, 1f);
            }
        } else if (exposure >= THRESHOLD_DEATH) {
            player.hurt(ChangedDamageSources.PALE, 2f); // 1 Heart (like poison II)
        }

        setPaleExposure(player, exposure);
    }
}
