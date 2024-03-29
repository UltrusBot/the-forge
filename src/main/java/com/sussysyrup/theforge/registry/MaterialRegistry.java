package com.sussysyrup.theforge.registry;

import com.sussysyrup.theforge.Main;
import com.sussysyrup.theforge.api.trait.ForgeTraitRegistry;
import com.sussysyrup.theforge.api.trait.TraitContainer;
import com.sussysyrup.theforge.api.material.ForgeMaterialRegistry;
import com.sussysyrup.theforge.api.material.Material;
import com.sussysyrup.theforge.api.material.MaterialResource;
import com.sussysyrup.theforge.trait.active.MagneticTrait;
import com.sussysyrup.theforge.trait.active.RegrowthTrait;
import com.sussysyrup.theforge.trait.repair.CorrodingTrait;
import com.sussysyrup.theforge.trait.repair.GrowthTrait;
import com.sussysyrup.theforge.trait.stat.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class MaterialRegistry {

    private static final MaterialResource WOOD = new MaterialResource("wood", 1);
    private static final TagKey<Item> WOOD_TAG = TagKey.of(Registry.ITEM_KEY, new Identifier(Main.MODID, "wood_resource_material"));

    private static final MaterialResource STONE = new MaterialResource("stone", 1);
    private static final TagKey<Item> STONE_TAG = TagKey.of(Registry.ITEM_KEY, new Identifier(Main.MODID, "stone_resource_material"));

    private static final MaterialResource FLINT = new MaterialResource("flint", 1);
    private static final TagKey<Item> FLINT_TAG = TagKey.of(Registry.ITEM_KEY, new Identifier(Main.MODID, "flint_resource_material"));

    private static final MaterialResource IRONNUG = new MaterialResource("iron", 1F/9F);
    private static final TagKey<Item> IRONNUG_TAG = TagKey.of(Registry.ITEM_KEY, new Identifier(Main.MODID, "ironnug_resource_material"));
    private static final MaterialResource IRONING = new MaterialResource("iron", 1);
    private static final TagKey<Item> IRONING_TAG = TagKey.of(Registry.ITEM_KEY, new Identifier(Main.MODID, "ironing_resource_material"));
    private static final MaterialResource IRONBLOCK = new MaterialResource("iron", 9);
    private static final TagKey<Item> IRONBLOCK_TAG = TagKey.of(Registry.ITEM_KEY, new Identifier(Main.MODID, "ironblock_resource_material"));

    //copper nuggets don't exist, yet
    private static final MaterialResource COPPERING = new MaterialResource("copper", 1);
    private static final TagKey<Item> COPPERING_TAG = TagKey.of(Registry.ITEM_KEY, new Identifier(Main.MODID, "coppering_resource_material"));
    private static final MaterialResource COPPERBLOCK = new MaterialResource("copper", 9);
    private static final TagKey<Item> COPPERBLOCK_TAG = TagKey.of(Registry.ITEM_KEY, new Identifier(Main.MODID, "copperblock_resource_material"));

    private static final MaterialResource DIAMOND = new MaterialResource("diamond", 1);
    private static final TagKey<Item> DIAMOND_TAG = TagKey.of(Registry.ITEM_KEY, new Identifier(Main.MODID, "diamond_resource_material"));

    //nether nugs don't exist either, yet
    private static final MaterialResource NETHERITEING = new MaterialResource("netherite", 1);
    private static final TagKey<Item> NETHERITEING_TAG = TagKey.of(Registry.ITEM_KEY, new Identifier(Main.MODID, "netheriteing_resource_material"));
    private static final MaterialResource NETHERITEBLOCK = new MaterialResource("netherite", 9);
    private static final TagKey<Item> NETHERITEBLOCK_TAG = TagKey.of(Registry.ITEM_KEY, new Identifier(Main.MODID, "netheriteblock_resource_material"));

    private static TraitContainer REGROWTH_TRAIT = new RegrowthTrait("regrowth",Formatting.DARK_GREEN);
    private static TraitContainer MAGNETIC1_TRAIT = new MagneticTrait("magnetic1", Formatting.GRAY, 4);
    private static TraitContainer MAGNETIC2_TRAIT = new MagneticTrait("magnetic2", Formatting.GRAY, 4.5F);

    private static TraitContainer BRITTLE_TRAIT = new BrittleTrait("brittle", Formatting.DARK_GRAY);
    private static TraitContainer CRUDE_TRAIT = new CrudeTrait("crude", Formatting.BLACK);
    private static TraitContainer PRIMAL_TRAIT = new PrimalTrait("primal", Formatting.DARK_GRAY);
    private static TraitContainer MUNDANE_TRAIT = new MundaneTrait("mundane", Formatting.DARK_GRAY);
    private static TraitContainer ANCIENT_TRAIT = new AncientTrait("ancient", Formatting.GOLD);

    private static TraitContainer GROWTH_TRAIT = new GrowthTrait("growth", Formatting.DARK_GREEN);
    private static TraitContainer CORRODING_TRAIT = new CorrodingTrait("corroding",Formatting.DARK_RED);

    public static void init()
    {
        registerTraits();

        ForgeMaterialRegistry.registerMaterial("wood", new Material("wood", false, "empty", MiningLevels.WOOD, 59, 1F, 2, 0.0F,
                createTraitsList(GROWTH_TRAIT), createTraitsList(REGROWTH_TRAIT), createTraitsList(REGROWTH_TRAIT), createTraitsList(GROWTH_TRAIT)));

        ForgeMaterialRegistry.registerMaterial("stone", new Material("stone", false, "empty", MiningLevels.STONE, 131, 0.2F, 4, 1.0F,
                createTraitsList(BRITTLE_TRAIT, CRUDE_TRAIT), createTraitsList(BRITTLE_TRAIT), createTraitsList(BRITTLE_TRAIT), createTraitsList(CRUDE_TRAIT)));

        ForgeMaterialRegistry.registerMaterial("flint", new Material("flint", false, "empty", MiningLevels.STONE, 112, 1.2F, 3.5F, 0.8F,
                createTraitsList(CRUDE_TRAIT, PRIMAL_TRAIT), createTraitsList(BRITTLE_TRAIT), createTraitsList(BRITTLE_TRAIT), createTraitsList(CRUDE_TRAIT)));

        ForgeMaterialRegistry.registerMaterial("iron", new Material("iron", true, "molten_iron", MiningLevels.IRON, 250, 1.05F, 6F, 2.0F,
                createTraitsList(MAGNETIC1_TRAIT), createTraitsList(), createTraitsList(), createTraitsList(MAGNETIC1_TRAIT)));

        ForgeMaterialRegistry.registerMaterial("copper", new Material("copper", true, "molten_copper", MiningLevels.IRON, 125, 0.8F, 4.2F, 1.5F,
                createTraitsList(MAGNETIC2_TRAIT, CORRODING_TRAIT), createTraitsList(CORRODING_TRAIT), createTraitsList(CORRODING_TRAIT), createTraitsList(MAGNETIC2_TRAIT)));

        //Use of diamond should be allowed but always discouraged
        ForgeMaterialRegistry.registerMaterial("diamond", new Material("diamond", false, "empty", MiningLevels.DIAMOND, 1561, 1.3F, 8F, 3.0F,
                createTraitsList(MUNDANE_TRAIT), createTraitsList(BRITTLE_TRAIT, MUNDANE_TRAIT), createTraitsList(MUNDANE_TRAIT), createTraitsList(MUNDANE_TRAIT)));

        ForgeMaterialRegistry.registerMaterial("netherite", new Material("netherite", true, "molten_netherite", MiningLevels.NETHERITE, 2031, 0.9F, 9F, 4.0F,
                createTraitsList(), createTraitsList(ANCIENT_TRAIT), createTraitsList(), createTraitsList(ANCIENT_TRAIT)));

        ForgeMaterialRegistry.registerPreMaterialResource(WOOD_TAG, WOOD);
        ForgeMaterialRegistry.registerPreMaterialResource(STONE_TAG, STONE);
        ForgeMaterialRegistry.registerPreMaterialResource(FLINT_TAG, FLINT);
        ForgeMaterialRegistry.registerPreMaterialResource(IRONNUG_TAG, IRONNUG);
        ForgeMaterialRegistry.registerPreMaterialResource(IRONING_TAG, IRONING);
        ForgeMaterialRegistry.registerPreMaterialResource(IRONBLOCK_TAG, IRONBLOCK);
        ForgeMaterialRegistry.registerPreMaterialResource(COPPERING_TAG, COPPERING);
        ForgeMaterialRegistry.registerPreMaterialResource(COPPERBLOCK_TAG, COPPERBLOCK);
        ForgeMaterialRegistry.registerPreMaterialResource(DIAMOND_TAG, DIAMOND);
        ForgeMaterialRegistry.registerPreMaterialResource(NETHERITEING_TAG, NETHERITEING);
        ForgeMaterialRegistry.registerPreMaterialResource(NETHERITEBLOCK_TAG, NETHERITEBLOCK);
    }

    private static void registerTraits()
    {
        ForgeTraitRegistry.registerTrait("regrowth", REGROWTH_TRAIT);
        ForgeTraitRegistry.registerTrait("magnetic1", MAGNETIC1_TRAIT);
        ForgeTraitRegistry.registerTrait("magnetic2", MAGNETIC2_TRAIT);

        ForgeTraitRegistry.registerTrait("brittle", BRITTLE_TRAIT);
        ForgeTraitRegistry.registerTrait("crude", CRUDE_TRAIT);
        ForgeTraitRegistry.registerTrait("primal", PRIMAL_TRAIT);
        ForgeTraitRegistry.registerTrait("mundane", MUNDANE_TRAIT);
        ForgeTraitRegistry.registerTrait("ancient", ANCIENT_TRAIT);

        ForgeTraitRegistry.registerTrait("growth", GROWTH_TRAIT);
        ForgeTraitRegistry.registerTrait("corroding", CORRODING_TRAIT);
    }

    @Environment(EnvType.CLIENT)
    public static void clientInit()
    {
        ForgeMaterialRegistry.registerColours("wood",
                new Color(40, 30 ,11),
                new Color(73, 54, 21),
                new Color(89, 67, 25),
                new Color(107, 81, 31),
                new Color(117, 88, 33),
                new Color(134, 101, 38),
                new Color(137, 103, 39));

        ForgeMaterialRegistry.registerColours("stone",
                new Color(24, 24 ,24),
                new Color(73, 73, 73),
                new Color(108, 108, 108),
                new Color(127, 127, 127),
                new Color(137, 137, 137),
                new Color(154, 154, 154),
                new Color(173, 173, 173));

        ForgeMaterialRegistry.registerColours("flint",
                new Color(14, 14 ,14),
                new Color(34, 32, 32),
                new Color(46, 45, 45),
                new Color(61, 60, 60),
                new Color(86, 86, 86),
                new Color(120, 120, 120),
                new Color(140, 140, 140));

        ForgeMaterialRegistry.registerColours("iron",
                new Color(24, 24 ,24),
                new Color(68, 68, 68),
                new Color(150, 150, 150),
                new Color(193, 193, 193),
                new Color(216, 216, 216),
                new Color(236, 236, 236),
                new Color(255, 255, 255));

        ForgeMaterialRegistry.registerColours("copper",
                new Color(109, 52 ,53),
                new Color(156, 69, 61),
                new Color(138, 65, 61),
                new Color(156, 78, 49),
                new Color(193, 90, 54),
                new Color(231, 124, 86),
                new Color(252, 153, 130));

        ForgeMaterialRegistry.registerColours("diamond",
                new Color(8, 37 ,32),
                new Color(14, 63, 54),
                new Color(38, 138, 119),
                new Color(39, 178, 154),
                new Color(43, 199, 172),
                new Color(51, 235, 203),
                new Color(58, 254, 220));

        ForgeMaterialRegistry.registerColours("netherite",
                new Color(17, 17 ,17),
                new Color(39, 28, 29),
                new Color(49, 41, 42),
                new Color(60, 50, 50),
                new Color(59, 57, 59),
                new Color(90, 87, 90),
                new Color(115, 113, 115));
    }

    private static List<TraitContainer> createTraitsList(TraitContainer... containers)
    {
        return Arrays.stream(containers).toList();
    }
}
