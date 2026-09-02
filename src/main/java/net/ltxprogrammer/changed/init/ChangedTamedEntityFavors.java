package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.ai.TamedEntityFavor;
import net.ltxprogrammer.changed.entity.ai.favors.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ChangedTamedEntityFavors {
    public static final DeferredRegister<TamedEntityFavor> REGISTRY = ChangedRegistry.TAMED_ENTITY_FAVORS.createDeferred(Changed.MODID);

    public static final RegistryObject<TamedEntityFavor> NONE = REGISTRY.register("none", () -> new TamedEntityFavor() {
        @Override
        public void createFavorGoals(ChangedEntity entity, LivingEntity owner, GoalConsumer goalConsumer) {}
    });

    public static final RegistryObject<FishingFavor> FISHING = REGISTRY.register("fishing", FishingFavor::new);
    public static final RegistryObject<CavingFavor> CAVING = REGISTRY.register("caving", CavingFavor::new);
    public static final RegistryObject<SuitOwnerFavor> SUIT_OWNER = REGISTRY.register("suit_owner", SuitOwnerFavor::new);
}
