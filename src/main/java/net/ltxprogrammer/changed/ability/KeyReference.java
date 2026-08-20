package net.ltxprogrammer.changed.ability;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedKeyMappings;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

/// Utility class to reference a key without requiring them to be loaded.
public class KeyReference {
    private static final Map<ResourceLocation, KeyReference> LOOKUP = new Object2ObjectArrayMap<>();

    public static final KeyReference SELECT_ABILITY = registerKeyReference(new KeyReference(Changed.modResource("select_ability"),
            () -> ChangedKeyMappings.SELECT_ABILITY.getTranslatedKeyMessage(),
            () -> ChangedKeyMappings.SELECT_ABILITY.isDown(),
            () -> ChangedKeyMappings.SELECT_ABILITY.getKey().getValue()));
    public static final KeyReference ABILITY = registerKeyReference(new KeyReference(Changed.modResource("ability"),
            () -> ChangedKeyMappings.USE_ABILITY.getTranslatedKeyMessage(),
            () -> ChangedKeyMappings.USE_ABILITY.isDown(),
            () -> ChangedKeyMappings.USE_ABILITY.getKey().getValue()));
    public static final KeyReference ABILITY_ALT = registerKeyReference(new KeyReference(Changed.modResource("ability_alt"),
            () -> ChangedKeyMappings.USE_ABILITY_ALT.getTranslatedKeyMessage(),
            () -> ChangedKeyMappings.USE_ABILITY_ALT.isDown(),
            () -> ChangedKeyMappings.USE_ABILITY_ALT.getKey().getValue()));
    public static final KeyReference ATTACK = registerKeyReference(new KeyReference(Changed.modResource("attack"),
            () -> Minecraft.getInstance().options.keyAttack.getTranslatedKeyMessage(),
            () -> Minecraft.getInstance().options.keyAttack.isDown(),
            () -> Minecraft.getInstance().options.keyAttack.getKey().getValue()));
    public static final KeyReference USE = registerKeyReference(new KeyReference(Changed.modResource("use"),
            () -> Minecraft.getInstance().options.keyUse.getTranslatedKeyMessage(),
            () -> Minecraft.getInstance().options.keyUse.isDown(),
            () -> Minecraft.getInstance().options.keyUse.getKey().getValue()));

    public static final KeyReference MOVE_FORWARD = registerKeyReference(new KeyReference(Changed.modResource("move_forward"),
            () -> Minecraft.getInstance().options.keyUp.getTranslatedKeyMessage(),
            () -> Minecraft.getInstance().options.keyUp.isDown(),
            () -> Minecraft.getInstance().options.keyUp.getKey().getValue()));
    public static final KeyReference MOVE_BACKWARD = registerKeyReference(new KeyReference(Changed.modResource("move_backward"),
            () -> Minecraft.getInstance().options.keyDown.getTranslatedKeyMessage(),
            () -> Minecraft.getInstance().options.keyDown.isDown(),
            () -> Minecraft.getInstance().options.keyDown.getKey().getValue()));
    public static final KeyReference MOVE_LEFT = registerKeyReference(new KeyReference(Changed.modResource("move_left"),
            () -> Minecraft.getInstance().options.keyLeft.getTranslatedKeyMessage(),
            () -> Minecraft.getInstance().options.keyLeft.isDown(),
            () -> Minecraft.getInstance().options.keyLeft.getKey().getValue()));
    public static final KeyReference MOVE_RIGHT = registerKeyReference(new KeyReference(Changed.modResource("move_right"),
            () -> Minecraft.getInstance().options.keyRight.getTranslatedKeyMessage(),
            () -> Minecraft.getInstance().options.keyRight.isDown(),
            () -> Minecraft.getInstance().options.keyRight.getKey().getValue()));

    private final ResourceLocation id;
    private final Supplier<Component> getName;
    private final Supplier<Boolean> isDown;
    private final Supplier<Integer> getKeycode;

    public static KeyReference registerKeyReference(KeyReference reference) {
        LOOKUP.put(reference.id, reference);
        return reference;
    }

    public static @Nullable KeyReference getNamedKey(ResourceLocation id) {
        return LOOKUP.get(id);
    }

    public KeyReference(ResourceLocation id, Supplier<Component> getName, Supplier<Boolean> isDown, Supplier<Integer> getKeycode) {
        this.id = id;
        this.getName = getName;
        this.isDown = isDown;
        this.getKeycode = getKeycode;
    }

    public Component getName(Level level) {
        if (level != null && level.isClientSide)
            return getName.get();
        else
            return Component.empty();
    }

    public boolean isDown(Level level) {
        if (level != null && level.isClientSide)
            return isDown.get();
        else
            return false;
    }

    public int getKeycode(Level level) {
        if (level != null && level.isClientSide)
            return getKeycode.get();
        else
            return 0;
    }

    public ResourceLocation getId() {
        return id;
    }
}
