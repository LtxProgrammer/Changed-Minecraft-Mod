package net.ltxprogrammer.changed.item;


import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class AbstractLatexCrystalItem extends Item {
    private final List<LatexVariant<?>> variants;

    public AbstractLatexCrystalItem(List<LatexVariant<?>> variants) {
        super(new Properties().tab(ChangedTabs.TAB_CHANGED_ITEMS));
        this.variants = variants;
    }

    public AbstractLatexCrystalItem(LatexVariant<?> variant) {
        this(List.of(variant));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity entity, LivingEntity source) {
        ProcessTransfur.progressTransfur(entity, 5.0f, variants.get(source.getRandom().nextInt(variants.size())));
        return super.hurtEnemy(stack, entity, source);
    }
}
