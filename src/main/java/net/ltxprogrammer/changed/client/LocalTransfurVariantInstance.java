package net.ltxprogrammer.changed.client;

import net.ltxprogrammer.changed.ability.active.GrabEntityAbility;
import net.ltxprogrammer.changed.ability.tree.effects.PostChainNodeEffect;
import net.ltxprogrammer.changed.block.WhiteLatexTransportInterface;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedAbilityTreeCodecs;
import net.ltxprogrammer.changed.init.ChangedAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@OnlyIn(Dist.CLIENT)
public class LocalTransfurVariantInstance<T extends ChangedEntity> extends ClientTransfurVariantInstance<T> {
    private final LocalPlayer host;
    private float postChainStrength = 0.0f;
    private float postChainStrengthO = 0.0f;
    private PostChainNodeEffect lastUsedPostChainNode = null;

    public PostChainNodeEffect getLastUsedPostChainNode() {
        return lastUsedPostChainNode;
    }

    public float getPostChainStrength(float partialTick) {
        return Mth.lerp(partialTick, postChainStrengthO, postChainStrength);
    }

    public LocalTransfurVariantInstance(TransfurVariant<T> parent, LocalPlayer host) {
        super(parent, host);
        this.host = host;
    }

    @Override
    protected void tickTransfurProgress() {
        super.tickTransfurProgress();

        if (transfurProgression < 1f || this.ageAsVariant < 30 || !this.getItemUseMode().holdMainHand || GrabEntityAbility.getControllingEntity(this.host) != this.host) {
            ((LocalPlayerAccessor)host).setHandsBusy(true);
        } else if (host.getVehicle() == null && host.isHandsBusy()) {
            ((LocalPlayerAccessor)host).setHandsBusy(false);
        }
    }

    public final UUID sprintSpeedModifier = Mth.createInsecureUUID(RandomSource.createNewThreadLocalInstance());
    private static final UUID ENTITY_SPEED_MODIFIER_SPRINTING_UUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");

    public void handleSprintModifier(AttributeInstance movementSpeed) {
        if (movementSpeed.getModifier(ENTITY_SPEED_MODIFIER_SPRINTING_UUID) != null) {
            // Vanilla sprint speed = MOVEMENT_SPEED + (0.3 * MOVEMENT_SPEED)
            var sprintMultiplier = host.getAttributeValue(ChangedAttributes.SPRINT_SPEED.get());
            var delta = (sprintMultiplier * 0.3) - 0.3;

            var sprintModifier = movementSpeed.getModifier(sprintSpeedModifier);
            if (sprintModifier != null && sprintModifier.getAmount() == delta)
                return;
            if (sprintModifier == null && delta == 0.0)
                return;

            movementSpeed.removeModifier(sprintSpeedModifier);
            if (delta != 0.0)
                movementSpeed.addTransientModifier(new AttributeModifier(sprintSpeedModifier, "Sprinting speed boost modifier", delta, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
    }

    public void updatePostChain() {
        postChainStrengthO = postChainStrength;

        AtomicReference<PostChainNodeEffect> highestNode = new AtomicReference<>(null);
        visitActiveNodeEffects(ChangedAbilityTreeCodecs.POST_CHAIN_NODE_EFFECT.get(), shaderNode -> {
            var prev = highestNode.get();
            if (prev == null || prev.priority < shaderNode.priority) {
                highestNode.set(shaderNode);
            }
        });

        var shaderNode = highestNode.getAcquire();
        if (shaderNode != null) {
            postChainStrength = Mth.lerp(0.05f, postChainStrength, shaderNode.strength);
            if (lastUsedPostChainNode == null || !lastUsedPostChainNode.postChain.equals(shaderNode.postChain)) {
                lastUsedPostChainNode = shaderNode;
                Minecraft.getInstance().gameRenderer.checkEntityPostEffect(this.host);
            }
        } else {
            postChainStrength = Mth.lerp(0.05f, postChainStrength, 0.0f);
            if (postChainStrength < 0.001f) {
                postChainStrength = 0.0f;
                lastUsedPostChainNode = null;
                Minecraft.getInstance().gameRenderer.checkEntityPostEffect(this.host);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (WhiteLatexTransportInterface.isEntityInWhiteLatex(host)) {
            ((LocalPlayerAccessor)host).setHandsBusy(true);
        }

        var movementSpeed = host.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed == null)
            return;

        this.handleSprintModifier(movementSpeed);
        this.updatePostChain();
    }

    @Override
    public void unhookAll(Player player) {
        super.unhookAll(player);

        if (host.getVehicle() == null && host.isHandsBusy()) {
            ((LocalPlayerAccessor)host).setHandsBusy(false);
        }
    }
}
