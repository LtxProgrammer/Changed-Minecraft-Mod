package net.ltxprogrammer.changed.item;


import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class AbstractLatexCrystalItem extends Item {
    private final List<TransfurVariant<?>> variants;

    public AbstractLatexCrystalItem(List<TransfurVariant<?>> variants) {
        super(new Properties().tab(ChangedTabs.TAB_CHANGED_ITEMS));
        this.variants = variants;
    }

    public AbstractLatexCrystalItem(TransfurVariant<?> variant) {
        this(List.of(variant));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity entity, LivingEntity source) {
        ProcessTransfur.progressTransfur(entity, 5.0f, variants.get(source.getRandom().nextInt(variants.size())), TransfurContext.hazard(TransfurCause.DARK_LATEX_CRYSTAL));
        return super.hurtEnemy(stack, entity, source);
    }
}
