package net.ltxprogrammer.changed.datagen.ability;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.tree.PartialNode.TreeReference;
import net.ltxprogrammer.changed.ability.tree.events.OnTransfurOther;
import net.ltxprogrammer.changed.data.RegistryElementPredicate;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class AbilityTreeProvider extends AbilityTreeDataProvider {

    public static final TreeReference LATEX = new TreeReference(Changed.modResource("latex"));

    public AbilityTreeProvider(PackOutput output) {
        super(output, Changed.MODID);
    }

    @Override
    protected void addTrees() {
    }
}