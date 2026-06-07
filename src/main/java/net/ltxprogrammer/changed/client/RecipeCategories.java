package net.ltxprogrammer.changed.client;

import com.google.common.collect.ImmutableList;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedRecipeTypes;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.ltxprogrammer.changed.item.AbdomenArmor;
import net.ltxprogrammer.changed.item.Syringe;
import net.ltxprogrammer.changed.item.TscWeapon;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.compress.utils.Lists;

import java.util.*;
import java.util.function.Function;

import static net.minecraft.client.RecipeBookCategories.CRAFTING_EQUIPMENT;

public class RecipeCategories {
    public static final List<RecipeBookCategories> INJECTED_CATEGORIES = new ArrayList<>();
    private static RecipeBookCategories registerBookCategory(String name, ItemStack... icons) {
        var category = RecipeBookCategories.create(name, icons);
        INJECTED_CATEGORIES.add(category);
        return category;
    }

    public static <T extends Recipe<?>> void registerCategoriesFinder(RegisterRecipeBookCategoriesEvent event, RecipeType<T> recipeType,
                                                                    RecipeBookCategories coreCategory, Function<T, List<RecipeBookCategories>> finder) {
        event.registerRecipeCategoryFinder(recipeType, recipe -> coreCategory);
        MULTICATEGORY_FINDER.add(recipe -> {
            try {
                return finder.apply((T)recipe);
            } catch (Exception ignored) {
                return Lists.newArrayList();
            }
        });
    }

    public static <T extends Recipe<?>> void registerTypeCategories(RegisterRecipeBookCategoriesEvent event, RecipeBookType bookType, RecipeType<T> recipeType,
                                                                    RecipeBookCategories coreCategory, List<RecipeBookCategories> categories, Function<T, List<RecipeBookCategories>> finder) {

        event.registerBookCategories(bookType, categories);
        registerCategoriesFinder(event, recipeType, coreCategory, finder);
    }

    public static final List<Function<Recipe<?>, List<RecipeBookCategories>>> MULTICATEGORY_FINDER = new ArrayList<>();

    public static final RecipeBookCategories INFUSER_SEARCH = registerBookCategory("CHANGED_INFUSER_SEARCH", new ItemStack(Items.COMPASS));
    public static final RecipeBookCategories INFUSER_DARK_LATEX = registerBookCategory("CHANGED_INFUSER_DARK_LATEX", new ItemStack(ChangedItems.DARK_LATEX_GOO.get()));
    public static final RecipeBookCategories INFUSER_WHITE_LATEX = registerBookCategory("CHANGED_INFUSER_WHITE_LATEX", new ItemStack(ChangedItems.WHITE_LATEX_GOO.get()));
    public static final RecipeBookCategories INFUSER_AQUATIC = registerBookCategory("CHANGED_INFUSER_AQUATIC", new ItemStack(Items.TROPICAL_FISH_BUCKET));
    public static final RecipeBookCategories INFUSER_AERIAL = registerBookCategory("CHANGED_INFUSER_AERIAL", new ItemStack(Items.ELYTRA));
    public static final RecipeBookCategories INFUSER_GENDERED = registerBookCategory("CHANGED_INFUSER_GENDERED",
            Syringe.setVariant(
                    new ItemStack(ChangedItems.LATEX_SYRINGE.get()), ChangedTransfurVariants.LATEX_SHARK.getId()),
            Syringe.setVariant(
                    new ItemStack(ChangedItems.LATEX_SYRINGE.get()), ChangedTransfurVariants.DARK_LATEX_WOLF_FEMALE.getId()));

    public static class GatherVariantCategoriesEvent extends Event implements IModBusEvent {
        private final Map<RecipeBookCategories, Set<ResourceLocation>> variantCategories;

        public GatherVariantCategoriesEvent(Map<RecipeBookCategories, Set<ResourceLocation>> variantCategories) {
            this.variantCategories = variantCategories;
        }

        public void add(RecipeBookCategories category, RegistryObject<? extends TransfurVariant<?>> delegate) {
            variantCategories.computeIfAbsent(category, local -> new HashSet<>()).add(delegate.getId());
        }

        public void add(RecipeBookCategories category, ResourceLocation name) {
            variantCategories.computeIfAbsent(category, local -> new HashSet<>()).add(name);
        }

        public void addDarkLatex(RegistryObject<? extends TransfurVariant<?>> delegate) {
            add(INFUSER_DARK_LATEX, delegate);
        }

        public void addDarkLatex(ResourceLocation name) {
            add(INFUSER_DARK_LATEX, name);
        }

        public void addWhiteLatex(RegistryObject<? extends TransfurVariant<?>> delegate) {
            add(INFUSER_WHITE_LATEX, delegate);
        }

        public void addWhiteLatex(ResourceLocation name) {
            add(INFUSER_WHITE_LATEX, name);
        }

        public void addAquatic(RegistryObject<? extends TransfurVariant<?>> delegate) {
            add(INFUSER_AQUATIC, delegate);
        }

        public void addAquatic(ResourceLocation name) {
            add(INFUSER_AQUATIC, name);
        }

        public void addAerial(RegistryObject<? extends TransfurVariant<?>> delegate) {
            add(INFUSER_AERIAL, delegate);
        }

        public void addAerial(ResourceLocation name) {
            add(INFUSER_AERIAL, name);
        }

        public void addGendered(RegistryObject<? extends TransfurVariant<?>> delegate) {
            add(INFUSER_GENDERED, delegate);
        }

        public void addGendered(ResourceLocation name) {
            add(INFUSER_GENDERED, name);
        }

        @Override
        public boolean isCancelable() {
            return false;
        }
    }
    
    private static void addVariantsToCategories(GatherVariantCategoriesEvent event) {
        event.addDarkLatex(ChangedTransfurVariants.DARK_LATEX_WOLF_MALE);
        event.addDarkLatex(ChangedTransfurVariants.DARK_LATEX_WOLF_FEMALE);
        event.addDarkLatex(ChangedTransfurVariants.DARK_LATEX_WOLF_PARTIAL);
        event.addDarkLatex(ChangedTransfurVariants.DARK_LATEX_WOLF_PUP);
        event.addDarkLatex(ChangedTransfurVariants.PHAGE_LATEX_WOLF_MALE);
        event.addDarkLatex(ChangedTransfurVariants.PHAGE_LATEX_WOLF_FEMALE);
        event.addDarkLatex(ChangedTransfurVariants.DARK_LATEX_YUFENG);
        event.addDarkLatex(ChangedTransfurVariants.DARK_LATEX_DOUBLE_YUFENG);
        event.addDarkLatex(ChangedTransfurVariants.DARK_DRAGON);

        event.addWhiteLatex(ChangedTransfurVariants.PURE_WHITE_LATEX_WOLF);
        event.addWhiteLatex(ChangedTransfurVariants.LATEX_MUTANT_BLOODCELL_WOLF);
        event.addWhiteLatex(ChangedTransfurVariants.PURE_WHITE_LATEX_WOLF_PUP);

        event.addAquatic(ChangedTransfurVariants.LATEX_ORCA);
        event.addAquatic(ChangedTransfurVariants.LATEX_SHARK);
        event.addAquatic(ChangedTransfurVariants.LATEX_SHARK_FUSION_MALE);
        event.addAquatic(ChangedTransfurVariants.LATEX_SHARK_FUSION_FEMALE);
        event.addAquatic(ChangedTransfurVariants.LATEX_MERMAID_SHARK);
        event.addAquatic(ChangedTransfurVariants.LATEX_SIREN);
        event.addAquatic(ChangedTransfurVariants.LATEX_MANTA_RAY_MALE);
        event.addAquatic(ChangedTransfurVariants.LATEX_MANTA_RAY_FEMALE);

        event.addAerial(ChangedTransfurVariants.DARK_DRAGON);
        event.addAerial(ChangedTransfurVariants.DARK_LATEX_YUFENG);
        event.addAerial(ChangedTransfurVariants.DARK_LATEX_DOUBLE_YUFENG);
        event.addAerial(ChangedTransfurVariants.LATEX_GOLDEN_DRAGON);
        event.addAerial(ChangedTransfurVariants.LATEX_PINK_YUIN_DRAGON);
        event.addAerial(ChangedTransfurVariants.LATEX_RED_DRAGON);

        event.addGendered(ChangedTransfurVariants.DARK_LATEX_WOLF_MALE);
        event.addGendered(ChangedTransfurVariants.DARK_LATEX_WOLF_FEMALE);
        event.addGendered(ChangedTransfurVariants.GAS_WOLF_MALE);
        event.addGendered(ChangedTransfurVariants.GAS_WOLF_FEMALE);
        event.addGendered(ChangedTransfurVariants.PHAGE_LATEX_WOLF_MALE);
        event.addGendered(ChangedTransfurVariants.PHAGE_LATEX_WOLF_FEMALE);
        event.addGendered(ChangedTransfurVariants.WHITE_LATEX_WOLF_MALE);
        event.addGendered(ChangedTransfurVariants.WHITE_LATEX_WOLF_FEMALE);
        event.addGendered(ChangedTransfurVariants.WHITE_WOLF_MALE);
        event.addGendered(ChangedTransfurVariants.WHITE_WOLF_FEMALE);
        event.addGendered(ChangedTransfurVariants.LATEX_RABBIT_MALE);
        event.addGendered(ChangedTransfurVariants.LATEX_RABBIT_FEMALE);
        event.addGendered(ChangedTransfurVariants.LATEX_SQUID_DOG_MALE);
        event.addGendered(ChangedTransfurVariants.LATEX_SQUID_DOG_FEMALE);
        event.addGendered(ChangedTransfurVariants.LATEX_SNOW_LEOPARD_MALE);
        event.addGendered(ChangedTransfurVariants.LATEX_SNOW_LEOPARD_FEMALE);
        event.addGendered(ChangedTransfurVariants.LATEX_SHARK_FUSION_MALE);
        event.addGendered(ChangedTransfurVariants.LATEX_SHARK_FUSION_FEMALE);
        event.addGendered(ChangedTransfurVariants.LATEX_MERMAID_SHARK);
        event.addGendered(ChangedTransfurVariants.LATEX_SIREN);
        event.addGendered(ChangedTransfurVariants.LATEX_MANTA_RAY_MALE);
        event.addGendered(ChangedTransfurVariants.LATEX_MANTA_RAY_FEMALE);
    }

    public static void registerCategories(RegisterRecipeBookCategoriesEvent event) {
        final Map<RecipeBookCategories, Set<ResourceLocation>> sortedVariants = Util.make(new HashMap<>(), map -> {
            map.put(INFUSER_DARK_LATEX, new HashSet<>());
            map.put(INFUSER_WHITE_LATEX, new HashSet<>());
            map.put(INFUSER_AQUATIC, new HashSet<>());
            map.put(INFUSER_AERIAL, new HashSet<>());
            map.put(INFUSER_GENDERED, new HashSet<>());
        });

        final var gatherEvent = new GatherVariantCategoriesEvent(sortedVariants);
        addVariantsToCategories(gatherEvent);
        Changed.postModLoadingEvent(gatherEvent);

        registerTypeCategories(event, ChangedRecipeTypes.INFUSER_BOOK, ChangedRecipeTypes.INFUSER_RECIPE.get(), INFUSER_SEARCH, ImmutableList.of(
                INFUSER_SEARCH, INFUSER_DARK_LATEX, INFUSER_WHITE_LATEX, INFUSER_AQUATIC, INFUSER_AERIAL, INFUSER_GENDERED
        ), recipe -> {
            ResourceLocation form = recipe.gendered ? ResourceLocation.parse(recipe.form + "/male") : recipe.form; // Default male for preview
            TransfurVariant<?> variant = ChangedRegistry.TRANSFUR_VARIANT.get().getValue(form);
            List<RecipeBookCategories> categories = new ArrayList<>();
            if (variant == null)
                return categories;

            sortedVariants.entrySet().stream()
                    .filter(entry -> entry.getValue().contains(form))
                    .map(Map.Entry::getKey)
                    .forEach(categories::add);

            return categories;
        });

        registerCategoriesFinder(event, RecipeType.CRAFTING, RecipeBookCategories.CRAFTING_SEARCH, recipe -> {
            final var registryAccess = Minecraft.getInstance().level.registryAccess();
            List<RecipeBookCategories> categories = new ArrayList<>();
            if (recipe.getResultItem(registryAccess).getItem() instanceof AbdomenArmor)
                categories.add(CRAFTING_EQUIPMENT);
            else if (recipe.getResultItem(registryAccess).getItem() instanceof TscWeapon)
                categories.add(CRAFTING_EQUIPMENT);
            return categories;
        });
    }
}
