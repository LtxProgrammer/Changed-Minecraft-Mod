package net.ltxprogrammer.changed.ability.active;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.tree.AbilityTreeInstance;
import net.ltxprogrammer.changed.ability.tree.events.OnTransfurOther;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.ai.ImmediateTransfurDecision;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedAbilityPointEvents;
import net.ltxprogrammer.changed.network.ChangedClickEvent;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class FriendlyTransfurAbilityInstance extends AbstractAbilityInstance {
    protected static final int DEBUFF_LENGTH = 20 * 60 * 5;

    private Player lastAskedEntity = null;
    private boolean consented = false;

    public FriendlyTransfurAbilityInstance(AbstractAbility<?> ability, IAbstractChangedEntity entity) {
        super(ability, entity);
    }

    @Override
    public AbstractAbility.UseType getUseType() {
        return consented ? AbstractAbility.UseType.CHARGE_TIME : AbstractAbility.UseType.INSTANT;
    }

    @Override
    public boolean canUse() {
        var grabAbility = entity.getAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get());
        return grabAbility != null && grabAbility.grabbedEntity instanceof Player && grabAbility.suited &&
                (lastAskedEntity != grabAbility.grabbedEntity || consented);
    }

    @Override
    public boolean canKeepUsing() {
        return true;
    }

    protected void applyDebuffs(IAbstractChangedEntity entity) {
        entity.getEntity().addEffect(new MobEffectInstance(MobEffects.WEAKNESS, DEBUFF_LENGTH));
        entity.getEntity().addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, DEBUFF_LENGTH, 1));
        entity.getEntity().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, DEBUFF_LENGTH, 1));
    }

    @Override
    public void startUsing() {
        var grabAbility = entity.getAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get());
        if (grabAbility != null && grabAbility.grabbedEntity instanceof Player grabbedPlayer) {
            if (consented) {
                grabAbility.releaseEntity(false);
                ProcessTransfur.transfur(grabbedPlayer, ImmediateTransfurDecision.safe(
                        entity.getSelfVariant(),
                        TransfurCause.GRAB_REPLICATE,
                        entity,
                        this::applyDebuffs
                ));

                AbilityTreeInstance.offerPointEvent(entity, ChangedAbilityPointEvents.ON_TRANSFUR_OTHER.get(), new OnTransfurOther.Criteria(grabbedPlayer));

                applyDebuffs(entity);
                this.getController().forceCooldown(20 * 60 * 5);

                consented = false;
                lastAskedEntity = null;
            } else {
                lastAskedEntity = grabbedPlayer;
                if (this.entity.getLevel().isClientSide())
                    return;

                entity.displayClientMessage(Component.translatable("ability.changed.friendly_transfur.requested", grabbedPlayer.getName())
                        .withStyle(Style.EMPTY.withItalic(true)), false);
                grabbedPlayer.displayClientMessage(
                        Component.translatable(
                                "ability.changed.friendly_transfur.request",
                                entity.getEntity().getName(),
                                Component.translatable("ability.changed.friendly_transfur.consent").withStyle(
                                        Style.EMPTY.withUnderlined(true).withColor(ChatFormatting.GREEN).withHoverEvent(
                                                new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("ability.changed.friendly_transfur.consent.tip"))
                                        ).withClickEvent(
                                                new ChangedClickEvent(ChangedClickEvent.ChangedAction.FRIENDLY_TF_CONSENT, "")
                                        )
                                )), false);
            }
        }
    }

    @Override
    public void acceptPayloadFromNonHost(CompoundTag tag, Player sender) {
        super.acceptPayloadFromNonHost(tag, sender);

        if (sender != lastAskedEntity || consented)
            return;

        consented = true;
        sender.displayClientMessage(Component.translatable("ability.changed.friendly_transfur.accept", entity.getEntity().getName()), false);
        entity.displayClientMessage(Component.translatable("ability.changed.friendly_transfur.accepted", sender.getName())
                .withStyle(Style.EMPTY.withItalic(true)), false);
    }

    public void provideConsent(Player grabbedEntity) {
        if (grabbedEntity != lastAskedEntity)
            return;

        this.sendPayload(new CompoundTag());
    }

    @Override
    public void tick() {

    }

    @Override
    public void tickIdle() {
        super.tickIdle();
        var grabAbility = entity.getAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get());
        if (grabAbility == null || !(grabAbility.grabbedEntity instanceof Player)) {
            lastAskedEntity = null;
            consented = false;
        }
    }

    @Override
    public void stopUsing() {

    }
}
