package net.ltxprogrammer.changed.item;


import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.ai.LatexAssimilationDecision;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.Util;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Supplier;

public class TransfurCrystalItem extends Item {
    private final List<Supplier<? extends TransfurVariant<?>>> variants;

    public TransfurCrystalItem(List<Supplier<? extends TransfurVariant<?>>> variants) {
        super(new Properties());
        this.variants = variants;
    }

    public TransfurCrystalItem(Supplier<? extends TransfurVariant<?>> variant) {
        this(List.of(variant));
    }

    protected LatexAssimilationDecision<?> makeAssimilationDecision(LivingEntity target) {
        return LatexAssimilationDecision.fromBlockOrItem(Util.getRandom(variants, target.getRandom()).get(), TransfurContext.hazard(TransfurCause.CRYSTAL), 5.0f);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity entity, LivingEntity source) {
        ProcessTransfur.progressTransfur(entity, this.makeAssimilationDecision(entity));
        return super.hurtEnemy(stack, entity, source);
    }
}
