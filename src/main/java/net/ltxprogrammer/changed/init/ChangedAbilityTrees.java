package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.tree.PartialNode;

/// Primarily for Datagen
public final class ChangedAbilityTrees {
    private static PartialNode.TreeReference namedTree(String name) {
        return new PartialNode.TreeReference(Changed.modResource(name));
    }

    /// Ability tree given to every variant. Hidden by default. Nodes specified here should be free.
    public static final PartialNode.TreeReference UNIVERSAL = namedTree("universal");

    /// Bee ability tree
    public static final PartialNode.TreeReference APIDAE = namedTree("apidae");
    /// Aquatic ability tree (sharks, eels, squogs, etc.)
    public static final PartialNode.TreeReference AQUATIC = namedTree("aquatic");
    /// Spider ability tree
    public static final PartialNode.TreeReference ARACHNIDAE = namedTree("arachnidae");
    /// Arm-winged variant ability tree (Crow, etc.)
    public static final PartialNode.TreeReference ARM_WING = namedTree("arm_wing");
    /// Back-winged variant ability tree (Winged dragons, etc.)
    public static final PartialNode.TreeReference BACK_WING = namedTree("back_wing");
    /// Plant-like ability tree
    public static final PartialNode.TreeReference CHLOROPHYLL = namedTree("chlorophyll");
    /// Squid/squid-dog ability tree
    public static final PartialNode.TreeReference COLEOIDAE = namedTree("coleoidae");
    /// Dark latex ability tree
    public static final PartialNode.TreeReference DARK_LATEX = namedTree("dark_latex");
    /// Hypnosis ability tree
    public static final PartialNode.TreeReference HYPNOSIS = namedTree("hypnosis");
    /// Shark ability tree
    public static final PartialNode.TreeReference LAMNIDAE = namedTree("lamnidae");
    /// Latex ability tree
    public static final PartialNode.TreeReference LATEX = namedTree("latex");
    /// Multi hand ability tree
    public static final PartialNode.TreeReference MULTI_ARM = namedTree("multi_arm");
    /// Non-latex (organic) ability tree
    public static final PartialNode.TreeReference NONLATEX = namedTree("nonlatex");
    /// Bipedal w/ digitigrade stance ability tree (wolf, cat, etc.)
    public static final PartialNode.TreeReference DIGITIGRADE_MOBILITY = namedTree("digitigrade_mobility");
    /// Legless aquatic ability tree (siren, mer shark, fem manta ray, etc.)
    public static final PartialNode.TreeReference MER_MOBILITY = namedTree("mer_mobility");
    /// Legless land-based ability tree (snake, etc.)
    public static final PartialNode.TreeReference NAGA_MOBILITY = namedTree("naga_mobility");
    /// On all fours ability tree (DL pup, etc.)
    public static final PartialNode.TreeReference QUADRUPEDAL_MOBILITY = namedTree("quadrupedal_mobility");
    /// On all fours + upper body ability tree (Centaur, etc.)
    public static final PartialNode.TreeReference TAUR_MOBILITY = namedTree("taur_mobility");

    /// UNSTABLE. Only used to grant the puddle ability to the DL pup
    public static final PartialNode.TreeReference DARK_LATEX_PUP = namedTree("dark_latex_pup");
    /// UNSTABLE. Only used to grant the switch gender ability to the WL wolves
    public static final PartialNode.TreeReference MALLEABLE = namedTree("malleable");
    /// UNSTABLE. Only used to grant the toggle night vision ability to cats, and others
    public static final PartialNode.TreeReference NIGHT_EYES = namedTree("night_eyes");
    /// UNSTABLE. Only used to grant the sing ability to the siren
    public static final PartialNode.TreeReference SIREN = namedTree("siren");
}
