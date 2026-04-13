//  This file is part of Ender Source Jars.
//  Copyright (C) 2026 ZoldLeo
//
//  This library is free software: you can redistribute it and/or
//  modify it under the terms of the GNU Lesser General Public
//  License as published by the Free Software Foundation; either
//  version 3 of the License, or (at your option) any later version.
//
//  This library is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//  Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public
//  License along with this library. If not, see https://www.gnu.org/licenses/lgpl-3.0.html;.
//
//  zoldleo.dev@gmail.com

package hu.zoldleo.endersourcejars;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.lib.colour.EnumColour;
import hu.zoldleo.endersourcejars.blocks.EnderSourceJar;
import hu.zoldleo.endersourcejars.blocks.EnderSourceJarEntity;
import hu.zoldleo.endersourcejars.client.EnderSourceJarRenderer;
import hu.zoldleo.endersourcejars.compat.CreateCompat;
import hu.zoldleo.endersourcejars.network.JarNetwork;
import hu.zoldleo.endersourcejars.storage.EnderSourceStoragePlugin;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static codechicken.enderstorage.init.EnderStorageModContent.FREQUENCY_DATA_COMPONENT;

@SuppressWarnings("all")
@Mod(EnderSourceJars.MODID)
public class EnderSourceJars {
    public static final String MODID = "endersourcejars";
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);

    public static final DeferredBlock<EnderSourceJar> ENDER_SOURCE_JAR_BLOCK = BLOCKS.register("endersourcejar", () -> new EnderSourceJar(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(20, 100)));
    public static final DeferredItem<BlockItem> ENDER_SOURCE_JAR_ITEM = ITEMS.register("endersourcejar", () -> new BlockItem(ENDER_SOURCE_JAR_BLOCK.get(), new Item.Properties()) {
        @Override
        protected boolean updateCustomBlockEntityTag(@NotNull BlockPos pos, @NotNull Level world, @Nullable Player player, @NotNull ItemStack stack, @NotNull BlockState state) {
            boolean flag = super.updateCustomBlockEntityTag(pos, world, player, stack, state);
            EnderSourceJarEntity tile = (EnderSourceJarEntity) world.getBlockEntity(pos);
            if (tile == null)
                return flag;
            tile.setFreq(Frequency.readFromStack(stack));
            return true;
        }
    });
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnderSourceJarEntity>> ENDER_SOURCE_JAR_TILE = BLOCK_ENTITY_TYPES.register("endersourcejar", () ->
            BlockEntityType.Builder.of(EnderSourceJarEntity::new, ENDER_SOURCE_JAR_BLOCK.get()).build(null)
    );

    public EnderSourceJars(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);

        BLOCK_ENTITY_TYPES.register(modEventBus);

        EnderStorageManager.registerPlugin(new EnderSourceStoragePlugin());

        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::onRegisterRenderers);
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.addListener(EnderSourceJars::onBreakEvent);
        JarNetwork.init(modEventBus);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        if (ModList.get().isLoaded("ars_creo"))
            CreateCompat.setup();
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            ItemStack stack = new ItemStack(ENDER_SOURCE_JAR_ITEM.get());
            stack.set(FREQUENCY_DATA_COMPONENT, new Frequency(EnumColour.WHITE, EnumColour.WHITE, EnumColour.WHITE));
            event.accept(stack);
        }
    }

    private void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        BlockEntityRenderers.register(ENDER_SOURCE_JAR_TILE.get(), EnderSourceJarRenderer::new);
    }

    @SubscribeEvent
    public static void onBreakEvent(BlockEvent.BreakEvent event) {
        BlockEntity tile = event.getLevel().getBlockEntity(event.getPos());
        if (tile instanceof EnderSourceJarEntity jarTile)
            NeoForge.EVENT_BUS.unregister(jarTile);
    }
}