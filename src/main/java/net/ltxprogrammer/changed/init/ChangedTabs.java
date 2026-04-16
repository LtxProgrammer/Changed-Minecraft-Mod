package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.decoration.WallSignVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.item.WallSignItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class ChangedTabs {
    private static CreativeModeTab.Builder makeTab(String id, CreativeModeTab.DisplayItemsGenerator generator) {
        return CreativeModeTab.builder()
                .title(Component.translatable("itemGroup." + id))
                .displayItems(generator);
    }

    public static DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Changed.MODID);

    private static final Predicate<TransfurVariant<?>> CHANGED_ONLY_TRANSFURS = variant -> variant.getFormId().getNamespace().equals(Changed.MODID);
    private static final Predicate<WallSignVariant> CHANGED_ONLY_WALL_SIGNS = variant -> ChangedRegistry.WALL_SIGN_VARIANT.getKey(variant).getNamespace().equals(Changed.MODID);

    private static RegistryObject<CreativeModeTab> register(String id, Function<CreativeModeTab.Builder, CreativeModeTab> finalizer) {
        return REGISTRY.register(id, () -> finalizer.apply(
                CreativeModeTab.builder().title(Component.translatable("itemGroup.tab_changed_" + id))
        ));
    }

    public static RegistryObject<CreativeModeTab> TAB_CHANGED_BLOCKS = register("blocks", builder ->
            builder.icon(() -> new ItemStack(ChangedBlocks.WALL_LIGHTRED_STRIPED.get()))
                    .displayItems((params, output) -> {
                        output.accept(ChangedBlocks.INFUSER.get());
                        output.accept(ChangedBlocks.PURIFIER.get());
                        output.accept(ChangedBlocks.STASIS_CHAMBER.get());

                        output.accept(ChangedBlocks.AIR_CONDITIONER.get());
                        output.accept(ChangedBlocks.BAR_STOOL.get());
                        output.accept(ChangedBlocks.BAR_TOP.get());
                        output.accept(ChangedBlocks.BEAKER.get());
                        output.accept(ChangedBlocks.BEDSIDE_IV_RACK.get());
                        output.accept(ChangedBlocks.BLACK_RAILING.get());
                        output.accept(ChangedBlocks.CLIPBOARD.get());
                        output.accept(ChangedBlocks.NOTE.get());
                        output.accept(ChangedBlocks.COMPUTER.get());
                        output.accept(ChangedBlocks.CANNED_PEACHES.get());
                        output.accept(ChangedBlocks.CANNED_SOUP.get());
                        output.accept(ChangedBlocks.CARDBOARD_BOX.get());
                        output.accept(ChangedBlocks.CARDBOARD_BOX_SMALL.get());
                        output.accept(ChangedBlocks.CARDBOARD_BOX_TALL.get());
                        output.accept(ChangedBlocks.BOX_PILE.get());
                        output.accept(ChangedBlocks.PEN_BOX.get());
                        output.accept(ChangedBlocks.DUCT.get());
                        output.accept(ChangedBlocks.ERLENMEYER_FLASK.get());
                        output.accept(ChangedBlocks.EXOSKELETON_CHARGER.get());
                        output.accept(ChangedBlocks.FLOOR_SIGN_WET.get());
                        output.accept(ChangedBlocks.FLOOR_SIGN_EXIT.get());
                        output.accept(ChangedBlocks.FLOOR_SIGN_ELECTRICAL.get());
                        output.accept(ChangedBlocks.GENERATOR.get());
                        output.accept(ChangedBlocks.IRON_CRATE.get());
                        output.accept(ChangedBlocks.KEYPAD.get());
                        output.accept(ChangedBlocks.LAB_LIGHT.get());
                        output.accept(ChangedBlocks.LAB_LIGHT_SMALL.get());
                        output.accept(ChangedBlocks.LAB_TABLE.get());
                        output.accept(ChangedBlocks.LASER_EMITTER.get());
                        output.accept(ChangedBlocks.LATEX_CRYSTAL.get());
                        output.accept(ChangedBlocks.LATEX_PUP_CRYSTAL.get());
                        output.accept(ChangedBlocks.LATEX_CONTAINER.get());
                        output.accept(ChangedBlocks.LATEX_TRAFFIC_CONE.get());
                        output.accept(ChangedBlocks.BEIFENG_CRYSTAL.get());
                        output.accept(ChangedBlocks.BEIFENG_CRYSTAL_SMALL.get());
                        output.accept(ChangedBlocks.DARK_DRAGON_CRYSTAL.get());
                        output.accept(ChangedBlocks.WOLF_CRYSTAL.get());
                        output.accept(ChangedBlocks.WOLF_CRYSTAL_SMALL.get());
                        output.accept(ChangedBlocks.DARK_LATEX_CRYSTAL_LARGE.get());
                        output.accept(ChangedBlocks.DARK_LATEX_PUDDLE.get());
                        output.accept(ChangedBlocks.WHITE_LATEX_PUDDLE_MALE.get());
                        output.accept(ChangedBlocks.WHITE_LATEX_PUDDLE_FEMALE.get());
                        output.accept(ChangedBlocks.PIPE.get());
                        output.accept(ChangedBlocks.PETRI_DISH.get());
                        output.accept(ChangedBlocks.RETINAL_SCANNER.get());
                        output.accept(ChangedBlocks.ROOMBA_CHARGER.get());
                        output.accept(ChangedBlocks.ROUTER.get());
                        output.accept(ChangedBlocks.SHIPPING_CONTAINER_BLUE.get());
                        output.accept(ChangedBlocks.SHIPPING_CONTAINER_ORANGE.get());
                        output.accept(ChangedBlocks.SPEAKER.get());
                        output.accept(ChangedBlocks.MICROPHONE.get());
                        output.accept(ChangedBlocks.MICROSCOPE.get());
                        output.accept(ChangedBlocks.OFFICE_CHAIR.get());
                        output.accept(ChangedBlocks.ORANGE_TREE_LEAVES.get());
                        output.accept(ChangedBlocks.ORANGE_TREE_SAPLING.get());
                        output.accept(ChangedBlocks.TAPE_RECORDER.get());
                        output.accept(ChangedBlocks.TILES_BLUE.get());
                        output.accept(ChangedBlocks.TILES_BLUE_SMALL.get());
                        output.accept(ChangedBlocks.TILES_CAUTION.get());
                        output.accept(ChangedBlocks.TILES_CAUTION_SLAB.get());
                        output.accept(ChangedBlocks.TILES_CAUTION_STAIRS.get());
                        output.accept(ChangedBlocks.TILES_GREENHOUSE.get());
                        output.accept(ChangedBlocks.WALL_GREENHOUSE.get());
                        output.accept(ChangedBlocks.TILES_GRAYBLUE.get());
                        output.accept(ChangedBlocks.TILES_GRAYBLUE_SLAB.get());
                        output.accept(ChangedBlocks.TILES_GRAYBLUE_STAIRS.get());
                        output.accept(ChangedBlocks.TILES_GRAY.get());
                        output.accept(ChangedBlocks.TILES_GRAY_SLAB.get());
                        output.accept(ChangedBlocks.TILES_GRAY_STAIRS.get());
                        output.accept(ChangedBlocks.TILES_GRAYBLUE_BOLTED.get());
                        output.accept(ChangedBlocks.TILES_GRAYBLUE_BOLTED_CONNECTED.get());
                        output.accept(ChangedBlocks.TILES_GRAYBLUE_BOLTED_SLAB.get());
                        output.accept(ChangedBlocks.TILES_GRAYBLUE_BOLTED_STAIRS.get());
                        output.accept(ChangedBlocks.TILES_LIBRARY_BROWN.get());
                        output.accept(ChangedBlocks.TILES_LIBRARY_BROWN_SLAB.get());
                        output.accept(ChangedBlocks.TILES_LIBRARY_BROWN_STAIRS.get());
                        output.accept(ChangedBlocks.TILES_LIBRARY_TAN.get());
                        output.accept(ChangedBlocks.TILES_LIBRARY_TAN_SLAB.get());
                        output.accept(ChangedBlocks.TILES_LIBRARY_TAN_STAIRS.get());
                        output.accept(ChangedBlocks.TILES_TEAL.get());
                        output.accept(ChangedBlocks.TILES_WHITE.get());
                        output.accept(ChangedBlocks.TILES_WHITE_SLAB.get());
                        output.accept(ChangedBlocks.TILES_WHITE_STAIRS.get());
                        output.accept(ChangedBlocks.ORANGE_LAB_CARPETING.get());
                        output.accept(ChangedBlocks.VENT_FAN.get());
                        output.accept(ChangedBlocks.VENT_HATCH.get());
                        output.accept(ChangedBlocks.WALL_BLUE_STRIPED.get());
                        output.accept(ChangedBlocks.WALL_BLUE_TILED.get());
                        output.accept(ChangedBlocks.WALL_CAUTION.get());
                        output.accept(ChangedBlocks.WALL_LIBRARY_UPPER.get());
                        output.accept(ChangedBlocks.WALL_LIBRARY_LOWER.get());
                        output.accept(ChangedBlocks.WALL_LIGHTRED.get());
                        output.accept(ChangedBlocks.WALL_LIGHTRED_CRACKED.get());
                        output.accept(ChangedBlocks.WALL_LIGHTRED_SLAB.get());
                        output.accept(ChangedBlocks.WALL_LIGHTRED_STAIRS.get());
                        output.accept(ChangedBlocks.WALL_LIGHTRED_STRIPED.get());
                        output.accept(ChangedBlocks.WALL_GRAY.get());
                        output.accept(ChangedBlocks.WALL_GRAY_CRACKED.get());
                        output.accept(ChangedBlocks.WALL_GRAY_SLAB.get());
                        output.accept(ChangedBlocks.WALL_GRAY_STAIRS.get());
                        output.accept(ChangedBlocks.WALL_GRAY_STRIPED.get());
                        output.accept(ChangedBlocks.WALL_GREEN.get());
                        output.accept(ChangedBlocks.WALL_GREEN_CRACKED.get());
                        output.accept(ChangedBlocks.WALL_GREEN_SLAB.get());
                        output.accept(ChangedBlocks.WALL_GREEN_STAIRS.get());
                        output.accept(ChangedBlocks.WALL_GREEN_STRIPED.get());
                        output.accept(ChangedBlocks.WALL_VENT.get());
                        output.accept(ChangedBlocks.WALL_WHITE.get());
                        output.accept(ChangedBlocks.WALL_WHITE_CRACKED.get());
                        output.accept(ChangedBlocks.WALL_WHITE_GREEN_STRIPED.get());
                        output.accept(ChangedBlocks.WALL_WHITE_GREEN_TILED.get());
                        output.accept(ChangedBlocks.WALL_WHITE_SLAB.get());
                        output.accept(ChangedBlocks.WALL_WHITE_STAIRS.get());
                        output.accept(ChangedBlocks.WHITE_LAB_TABLE.get());

                        output.accept(ChangedBlocks.BEEHIVE_BED.get());
                        output.accept(ChangedBlocks.BEEHIVE_WALL.get());

                        output.accept(ChangedBlocks.LARGE_LAB_DOOR.get());
                        output.accept(ChangedBlocks.LARGE_LIBRARY_DOOR.get());
                        output.accept(ChangedBlocks.LARGE_MAINTENANCE_DOOR.get());
                        output.accept(ChangedBlocks.LARGE_BLUE_LAB_DOOR.get());
                        output.accept(ChangedBlocks.LAB_DOOR.get());
                        output.accept(ChangedBlocks.LIBRARY_DOOR.get());
                        output.accept(ChangedBlocks.MAINTENANCE_DOOR.get());
                        output.accept(ChangedBlocks.BLUE_LAB_DOOR.get());

                        output.accept(ChangedBlocks.EMPTY_CANISTER.get());
                        output.accept(ChangedBlocks.OXYGENATED_WATER_CANISTER.get());
                        output.accept(ChangedBlocks.WOLF_GAS_CANISTER.get());
                        output.accept(ChangedBlocks.TIGER_GAS_CANISTER.get());
                        output.accept(ChangedBlocks.SKUNK_GAS_CANISTER.get());

                        output.accept(ChangedBlocks.DARK_LATEX_BLOCK.get());
                        output.accept(ChangedBlocks.DARK_LATEX_ICE.get());
                        output.accept(ChangedBlocks.WOLF_CRYSTAL_BLOCK.get());
                        output.accept(ChangedBlocks.WHITE_LATEX_BLOCK.get());
                        output.accept(ChangedBlocks.WHITE_LATEX_PILLAR.get());

                        output.accept(ChangedItems.BIPED_ARMOR_STAND.get());
                        output.accept(ChangedItems.CENTAUR_ARMOR_STAND.get());
                        output.accept(ChangedItems.LEGLESS_ARMOR_STAND.get());

                        WallSignItem.fillItemList(CHANGED_ONLY_WALL_SIGNS, params, output);

                        ChangedBlocks.PILLOWS.values().stream().map(RegistryObject::get).forEach(output::accept);

                        ForgeRegistries.PAINTING_VARIANTS.getKeys().stream()
                                .filter(name -> name.getNamespace().equals(Changed.MODID))
                                .map(ForgeRegistries.PAINTING_VARIANTS::getHolder).filter(Optional::isPresent).map(Optional::get)
                                .forEach(variant -> {
                                    ItemStack stack = new ItemStack(Items.PAINTING);
                                    CompoundTag compoundtag = stack.getOrCreateTagElement("EntityTag");
                                    Painting.storeVariant(compoundtag, variant);
                                    output.accept(stack);
                                });
                    }).build());

    public static RegistryObject<CreativeModeTab> TAB_CHANGED_ITEMS = register("items", builder ->
            builder.icon(() -> new ItemStack(ChangedItems.LATEX_BASE.get()))
                    .displayItems((params, output) -> {
                        output.accept(ChangedItems.GAS_MASK.get());
                        output.accept(ChangedItems.COMPACT_DISC.get());
                        output.accept(ChangedItems.LAB_BOOK.get());
                        output.accept(ChangedItems.LATEX_BASE.get());
                        output.accept(ChangedBlocks.MUG.get());
                        output.accept(ChangedItems.MUG_WITH_WATER.get());
                        output.accept(ChangedItems.MUG_WITH_MILK.get());
                        output.accept(ChangedItems.MUG_WITH_COFFEE.get());
                        output.accept(ChangedItems.MUG_WITH_DARK_LATEX.get());
                        output.accept(ChangedItems.MUG_WITH_WHITE_LATEX.get());
                        output.accept(ChangedItems.ORANGE.get());
                        output.accept(ChangedItems.SYRINGE.get());
                        output.accept(ChangedItems.BLOOD_SYRINGE.get());
                        output.accept(ChangedItems.DARK_LATEX_GOO.get());
                        output.accept(ChangedItems.DARK_LATEX_BUCKET.get());
                        output.accept(ChangedItems.WHITE_LATEX_GOO.get());
                        output.accept(ChangedItems.WHITE_LATEX_BUCKET.get());
                        output.accept(ChangedItems.DARK_LATEX_CRYSTAL_FRAGMENT.get());
                        output.accept(ChangedItems.BEIFENG_CRYSTAL_FRAGMENT.get());
                        output.accept(ChangedItems.WOLF_CRYSTAL_FRAGMENT.get());
                        output.accept(ChangedItems.DARK_DRAGON_CRYSTAL_FRAGMENT.get());
                        output.accept(ChangedItems.LATEX_INKBALL.get());
                        output.accept(ChangedItems.ROOMBA.get());
                        output.accept(ChangedItems.EXOSKELETON.get());

                        ChangedItems.DARK_LATEX_MASK.get().fillItemList(CHANGED_ONLY_TRANSFURS, params, output);
                        ChangedItems.LATEX_SYRINGE.get().fillItemList(CHANGED_ONLY_TRANSFURS, params, output);
                        ChangedItems.LATEX_FLASK.get().fillItemList(CHANGED_ONLY_TRANSFURS, params, output);
                        ChangedItems.LATEX_TIPPED_ARROW.get().fillItemList(CHANGED_ONLY_TRANSFURS, params, output);
                    })
                    .build());

    public static RegistryObject<CreativeModeTab> TAB_CHANGED_ENTITIES = register("entities", builder ->
            builder.icon(() -> new ItemStack(ChangedItems.DARK_LATEX_MASK.get()))
                    .displayItems((params, output) -> {
                        ChangedEntities.SPAWN_EGGS.values().stream()
                                .map(RegistryObject::get)
                                .forEach(output::accept);
                    })
                    .build());

    public static RegistryObject<CreativeModeTab> TAB_CHANGED_COMBAT = register("combat", builder ->
            builder.icon(() -> new ItemStack(ChangedItems.TSC_BATON.get()))
                    .displayItems((params, output) -> {
                        output.accept(ChangedItems.TSC_BATON.get());
                        output.accept(ChangedItems.TSC_STAFF.get());
                        output.accept(ChangedItems.TSC_SHIELD.get());

                        output.accept(ChangedItems.LEATHER_UPPER_ABDOMEN_ARMOR.get());
                        output.accept(ChangedItems.LEATHER_LOWER_ABDOMEN_ARMOR.get());
                        output.accept(ChangedItems.LEATHER_QUADRUPEDAL_LEGGINGS.get());
                        output.accept(ChangedItems.LEATHER_QUADRUPEDAL_BOOTS.get());

                        output.accept(ChangedItems.CHAINMAIL_UPPER_ABDOMEN_ARMOR.get());
                        output.accept(ChangedItems.CHAINMAIL_LOWER_ABDOMEN_ARMOR.get());
                        output.accept(ChangedItems.CHAINMAIL_QUADRUPEDAL_LEGGINGS.get());
                        output.accept(ChangedItems.CHAINMAIL_QUADRUPEDAL_BOOTS.get());

                        output.accept(ChangedItems.IRON_UPPER_ABDOMEN_ARMOR.get());
                        output.accept(ChangedItems.IRON_LOWER_ABDOMEN_ARMOR.get());
                        output.accept(ChangedItems.IRON_QUADRUPEDAL_LEGGINGS.get());
                        output.accept(ChangedItems.IRON_QUADRUPEDAL_BOOTS.get());

                        output.accept(ChangedItems.GOLDEN_UPPER_ABDOMEN_ARMOR.get());
                        output.accept(ChangedItems.GOLDEN_LOWER_ABDOMEN_ARMOR.get());
                        output.accept(ChangedItems.GOLDEN_QUADRUPEDAL_LEGGINGS.get());
                        output.accept(ChangedItems.GOLDEN_QUADRUPEDAL_BOOTS.get());

                        output.accept(ChangedItems.DIAMOND_UPPER_ABDOMEN_ARMOR.get());
                        output.accept(ChangedItems.DIAMOND_LOWER_ABDOMEN_ARMOR.get());
                        output.accept(ChangedItems.DIAMOND_QUADRUPEDAL_LEGGINGS.get());
                        output.accept(ChangedItems.DIAMOND_QUADRUPEDAL_BOOTS.get());

                        output.accept(ChangedItems.NETHERITE_UPPER_ABDOMEN_ARMOR.get());
                        output.accept(ChangedItems.NETHERITE_LOWER_ABDOMEN_ARMOR.get());
                        output.accept(ChangedItems.NETHERITE_QUADRUPEDAL_LEGGINGS.get());
                        output.accept(ChangedItems.NETHERITE_QUADRUPEDAL_BOOTS.get());

                        output.accept(ChangedItems.ABDOMEN_ARMOR_CONVERSION.get());
                        output.accept(ChangedItems.QUADRUPEDAL_ARMOR_CONVERSION.get());
                    })
                    .build());

    public static RegistryObject<CreativeModeTab> TAB_CHANGED_CLOTHING = register("clothing", builder ->
            builder.icon(() -> new ItemStack(ChangedItems.BENIGN_SHORTS.get()))
                    .displayItems((params, output) -> {
                        output.accept(ChangedItems.BENIGN_SHORTS.get());
                        output.accept(ChangedItems.PINK_SHORTS.get());
                        output.accept(ChangedItems.BLACK_PANTS.get());
                        output.accept(ChangedItems.NAVY_PANTS.get());
                        output.accept(ChangedItems.BLACK_TSHIRT.get());
                        output.accept(ChangedItems.WHITE_TSHIRT.get());
                        output.accept(ChangedItems.TSC_VEST.get());
                        output.accept(ChangedItems.SPORTS_BRA.get());
                        output.accept(ChangedItems.LAB_COAT.get());
                        output.accept(ChangedItems.WETSUIT.get());
                        output.accept(ChangedItems.NITRILE_GLOVES.get());
                        output.accept(ChangedItems.FACE_MASK.get());
                        output.accept(ChangedItems.ORANGE_NECK_TIE.get());
                        output.accept(ChangedItems.RED_NECK_TIE.get());
                        output.accept(ChangedItems.BLUE_NECK_TIE.get());
                        output.accept(ChangedItems.DOG_COLLAR.get());
                    })
                    .build());

    public static RegistryObject<CreativeModeTab> TAB_CHANGED_MUSIC = register("music", builder ->
            builder.icon(() -> new ItemStack(ChangedItems.PURO_THE_BLACK_GOO_RECORD.get()))
                    .displayItems((params, output) -> {
                        output.accept(ChangedItems.BLACK_GOO_ZONE_RECORD.get());
                        output.accept(ChangedItems.CRYSTAL_ZONE_RECORD.get());
                        output.accept(ChangedItems.GAS_ROOM_RECORD.get());
                        output.accept(ChangedItems.LABORATORY_RECORD.get());
                        output.accept(ChangedItems.OUTSIDE_THE_TOWER_RECORD.get());
                        output.accept(ChangedItems.PURO_THE_BLACK_GOO_RECORD.get());
                        output.accept(ChangedItems.PUROS_HOME_RECORD.get());
                        output.accept(ChangedItems.THE_LIBRARY_RECORD.get());
                        output.accept(ChangedItems.THE_LION_CHASE_RECORD.get());
                        output.accept(ChangedItems.THE_SCARLET_CRYSTAL_MINE_RECORD.get());
                        output.accept(ChangedItems.THE_SHARK_RECORD.get());
                        output.accept(ChangedItems.THE_SQUID_DOG_RECORD.get());
                        output.accept(ChangedItems.THE_WHITE_GOO_JUNGLE_RECORD.get());
                        output.accept(ChangedItems.THE_WHITE_TAIL_CHASE_PART_1.get());
                        output.accept(ChangedItems.THE_WHITE_TAIL_CHASE_PART_2.get());
                        output.accept(ChangedItems.VENT_PIPE_RECORD.get());
                    })
                    .build());
}
