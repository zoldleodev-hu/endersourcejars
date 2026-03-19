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
import hu.zoldleo.endersourcejars.blocks.EnderSourceJarRenderer;
import hu.zoldleo.endersourcejars.network.JarNetwork;
import hu.zoldleo.endersourcejars.storage.EnderSourceStoragePlugin;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static codechicken.enderstorage.init.EnderStorageModContent.FREQUENCY_DATA_COMPONENT;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
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

        @Override
        public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext ctx, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
            Frequency frequency = Frequency.readFromStack(stack);
            frequency.ownerName().ifPresent(tooltip::add);
            tooltip.add(frequency.getTooltip());
        }
    });
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnderSourceJarEntity>> ENDER_SOURCE_JAR_TILE = BLOCK_ENTITY_TYPES.register("endersourcejar", () ->
            BlockEntityType.Builder.of(EnderSourceJarEntity::new, ENDER_SOURCE_JAR_BLOCK.get()).build(null)
    );

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public EnderSourceJars(IEventBus modEventBus) {

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);

        BLOCK_ENTITY_TYPES.register(modEventBus);

        EnderStorageManager.registerPlugin(new EnderSourceStoragePlugin());

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::onRegisterRenderers);
        JarNetwork.init(modEventBus);
    }

    // Add the example block item to the building blocks tab
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
}