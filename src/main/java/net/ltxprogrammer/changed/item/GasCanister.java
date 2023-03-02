package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.entity.projectile.GasParticle;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class GasCanister extends BlockItem {
    private final List<LatexVariant<?>> variants;
    private final ChangedParticles.Color3 color;

    public GasCanister(Block block, List<LatexVariant<?>> variants, ChangedParticles.Color3 color) {
        super(block, new Item.Properties().tab(ChangedTabs.TAB_CHANGED_BLOCKS).stacksTo(1));
        this.variants = variants;
        this.color = color;
    }

    public int getUseDuration(ItemStack stack) {
        return 5 * 20;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int ticks) {
        super.onUseTick(level, entity, stack, ticks);
        if (level.isClientSide || variants.isEmpty())
            return;

        GasParticle nParticle = new GasParticle(ChangedEntities.GAS_PARTICLE.get(), level).setVariant(
                variants.get(level.random.nextInt(variants.size()))
        );

        float randX = (level.random.nextFloat(90.0f) - 45.0f) * 0.5f;
        float randY = (level.random.nextFloat(90.0f) - 45.0f) * 0.5f;

        Vec3 frontVector = entity.getViewVector(0.5f);
        Vec3 rightVector = frontVector.cross(entity.getUpVector(0.5f));
        float dir = ((entity.getUsedItemHand() == InteractionHand.MAIN_HAND) == (entity.getMainArm() == HumanoidArm.RIGHT) ? 1.0f : -1.0f) * 0.33f;

        nParticle.setPos(entity.getEyePosition()
                .add(frontVector.multiply(0.75, 0.75, 0.75))
                .add(rightVector.multiply(dir, dir, dir))
        );
        nParticle.shootFromRotation(entity, entity.getXRot() + randX, entity.getYRot() + randY, 0.0F, 0.5F, 1.0F);
        level.addFreshEntity(nParticle);

        nParticle.setColor(color);
    }
}
