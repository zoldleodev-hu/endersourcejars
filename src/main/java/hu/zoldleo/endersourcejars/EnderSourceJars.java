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
import hu.zoldleo.endersourcejars.blocks.EnderSourceJar;
import hu.zoldleo.endersourcejars.blocks.EnderSourceJarEntity;
import hu.zoldleo.endersourcejars.blocks.EnderSourceJarRenderer;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod(EnderSourceJars.MODID)
public class EnderSourceJars {public static final String MODID = "endersourcejars";
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);

    public static final RegistryObject<EnderSourceJar> ENDER_SOURCE_JAR_BLOCK = BLOCKS.register("endersourcejar", () -> new EnderSourceJar(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(20, 100)));
    public static final RegistryObject<BlockItem> ENDER_SOURCE_JAR_ITEM = ITEMS.register("endersourcejar", () -> new BlockItem(ENDER_SOURCE_JAR_BLOCK.get(), new Item.Properties()) {
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
    @SuppressWarnings("all")
    public static final RegistryObject<BlockEntityType<EnderSourceJarEntity>> ENDER_SOURCE_JAR_TILE = BLOCK_ENTITY_TYPES.register("endersourcejar", () ->
            BlockEntityType.Builder.of(EnderSourceJarEntity::new, ENDER_SOURCE_JAR_BLOCK.get()).build(null)
    );

    @SuppressWarnings("all")
    public EnderSourceJars() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);

        BLOCK_ENTITY_TYPES.register(modEventBus);

        EnderStorageManager.registerPlugin(new EnderSourceStoragePlugin());

        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::onRegisterRenderers);
        modEventBus.addListener(this::commonSetup);
        Mod.EventBusSubscriber.Bus.FORGE.bus().get().addListener(EnderSourceJars::onBreakEvent);
        JarNetwork.init();
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        if (ModList.get().isLoaded("ars_creo"))
            CreateCompat.setup();
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ENDER_SOURCE_JAR_ITEM);
        }
    }

    private void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        BlockEntityRenderers.register(ENDER_SOURCE_JAR_TILE.get(), EnderSourceJarRenderer::new);
    }

    private static void onBreakEvent(BlockEvent.BreakEvent event) {
        BlockEntity tile = event.getLevel().getBlockEntity(event.getPos());
        if (tile instanceof EnderSourceJarEntity jarTile)
            Mod.EventBusSubscriber.Bus.FORGE.bus().get().unregister(jarTile);
    }
}