package com.sussysyrup.theforge.registry;

import com.sussysyrup.theforge.Main;
import com.sussysyrup.theforge.api.itemgroup.ItemGroups;
import com.sussysyrup.theforge.blocks.ForgeBlock;
import com.sussysyrup.theforge.blocks.RepairAnvilBlock;
import com.sussysyrup.theforge.blocks.entity.ForgeBlockEntity;
import com.sussysyrup.theforge.blocks.entity.RepairAnvilBlockEntity;
import com.sussysyrup.theforge.client.render.ForgeBlockEntityRender;
import com.sussysyrup.theforge.client.render.RepairAnvilBlockEntityRender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlocksRegistry {

    public static Block FORGE_BLOCK = new ForgeBlock(FabricBlockSettings.of(Material.STONE).strength(4.0f).requiresTool());
    public static Block REPAIR_ANVIL_BLOCK = new RepairAnvilBlock(FabricBlockSettings.of(Material.METAL).requiresTool().strength(5.0f, 1200.0f).sounds(BlockSoundGroup.ANVIL));

    public static BlockEntityType<ForgeBlockEntity> FORGE_BLOCK_ENTITY;
    public static BlockEntityType<RepairAnvilBlockEntity> REPAIR_ANVIL_BLOCK_ENTITY;

    public static void init()
    {
        register(FORGE_BLOCK, "forge_block");
        register(REPAIR_ANVIL_BLOCK, "repair_anvil_block");

        FORGE_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Main.MODID, "forge_block"), FabricBlockEntityTypeBuilder.create(ForgeBlockEntity::new, FORGE_BLOCK).build());
        REPAIR_ANVIL_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Main.MODID, "repair_anvil_block"), FabricBlockEntityTypeBuilder.create(RepairAnvilBlockEntity::new, REPAIR_ANVIL_BLOCK).build());
    }

    private static void register(Block block, String name)
    {
        Registry.register(Registry.BLOCK, new Identifier(Main.MODID, name), block);
        Registry.register(Registry.ITEM, new Identifier(Main.MODID, name), new BlockItem(block, new FabricItemSettings().group(ItemGroups.BLOCK_GROUP)));
    }

    @Environment(EnvType.CLIENT)
    public static void clientInit()
    {
        BlockEntityRendererRegistry.INSTANCE.register(FORGE_BLOCK_ENTITY, ForgeBlockEntityRender::new);
        BlockEntityRendererRegistry.INSTANCE.register(REPAIR_ANVIL_BLOCK_ENTITY, RepairAnvilBlockEntityRender::new);
    }

}