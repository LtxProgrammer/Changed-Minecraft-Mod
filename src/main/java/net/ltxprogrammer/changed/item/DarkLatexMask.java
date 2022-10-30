package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.fluid.AbstractLatexFluid;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class DarkLatexMask extends ArmorItem {
    public DarkLatexMask() {
        super(new ArmorMaterial() {
            @Override
            public int getDurabilityForSlot(EquipmentSlot slot) {
                return 450;
            }

            @Override
            public int getDefenseForSlot(EquipmentSlot p_40411_) {
                return 2;
            }

            @Override
            public int getEnchantmentValue() {
                return 0;
            }

            @Override
            public SoundEvent getEquipSound() {
                return null;
            }

            @Override
            public Ingredient getRepairIngredient() {
                return Ingredient.EMPTY;
            }

            @Override
            public String getName() {
                return "dark_latex_mask";
            }

            @Override
            public float getToughness() {
                return 0f;
            }

            @Override
            public float getKnockbackResistance() {
                return 0.1f;
            }
        }, EquipmentSlot.HEAD, new Item.Properties().tab(ChangedTabs.TAB_CHANGED_ITEMS));
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "changed:textures/models/armor/darklatexmasktexture_layer_1.png";
    }

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingUpdateEvent event) {
        event.getEntityLiving().getArmorSlots().forEach(itemStack -> {
            if (itemStack.is(ChangedItems.DARK_LATEX_MASK.get())) {
                if (ProcessTransfur.progressTransfur(event.getEntityLiving(), 2500, LatexVariant.DARK_LATEX_WOLF.male().getFormId()))
                    itemStack.shrink(1);
            }
        });
    }
}
