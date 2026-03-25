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

package hu.zoldleo.endersourcejars.blocks;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.config.EnderStorageConfig;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.tile.TileFrequencyOwner;
import codechicken.lib.colour.EnumColour;
import codechicken.lib.raytracer.IndexedVoxelShape;
import codechicken.lib.raytracer.MultiIndexedVoxelShape;
import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.raytracer.SubHitBlockHitResult;
import codechicken.lib.raytracer.VoxelShapeCache;
import codechicken.lib.util.ItemUtils;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Translation;
import com.google.common.collect.ImmutableSet;
import com.hollingsworth.arsnouveau.common.block.ModBlock;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import hu.zoldleo.endersourcejars.storage.EnderSourceStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static hu.zoldleo.endersourcejars.EnderSourceJars.ENDER_SOURCE_JAR_TILE;

public class EnderSourceJar extends ModBlock implements SimpleWaterloggedBlock, EntityBlock {
    private static final IndexedVoxelShape jar = new IndexedVoxelShape(Stream.of(
            Block.box(2.0, 0.0, 2.0, 14.0, 2.0, 14.0),
            Block.box(3.0, 2.0, 3.0, 13.0, 13.0, 13.0),
            Block.box(4.0, 13.0, 4.0, 12.0, 14.0, 12.0))
            .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),0);
    private static final IndexedVoxelShape[] buttons = new IndexedVoxelShape[3];
    private static final IndexedVoxelShape lid = new IndexedVoxelShape(Stream.of(
            Block.box(4.0, 14.0, 4.0, 12.0, 15.0, 12.0),
            Block.box(3.0, 14.0, 3.0, 4.0, 16.0, 13.0),
            Block.box(12.0, 14.0, 3.0, 13.0, 16.0, 13.0),
            Block.box(3.0, 14.0, 3.0, 13.0, 16.0, 4.0),
            Block.box(3.0, 14.0, 12.0, 13.0, 16.0, 13.0))
            .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(), 4);
    private static final MultiIndexedVoxelShape shape;
    private static final VoxelShape collisionShape = Shapes.join(jar, lid, BooleanOp.OR);

    public EnderSourceJar(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
    }

    public @Nullable PushReaction getPistonPushReaction(@NotNull BlockState state) {
        return PushReaction.DESTROY;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new EnderSourceJarEntity(blockPos, blockState);
    }

    public @NotNull RenderShape getRenderShape(@NotNull BlockState p_149645_1_) {
        return RenderShape.MODEL;
    }

    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return shape;
    }

    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return collisionShape;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    public @NotNull BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        context.getLevel().scheduleTick(context.getClickedPos(), BlockRegistry.SOURCE_JAR.get(), 1);
        return defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    public @NotNull BlockState updateShape(BlockState stateIn, @NotNull Direction side, @NotNull BlockState facingState, @NotNull LevelAccessor worldIn, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        if (stateIn.getValue(BlockStateProperties.WATERLOGGED)) {
            worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }

        return stateIn;
    }

    public void tick(@NotNull BlockState p_222945_, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource p_222948_) {
        super.tick(p_222945_, level, pos, p_222948_);
        BlockEntity var6 = level.getBlockEntity(pos);
        if (var6 instanceof EnderSourceJarEntity jarTile) {
            jarTile.updateBlock();
        }

    }

    public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    public int getAnalogOutputSignal(@NotNull BlockState blockState, Level worldIn, @NotNull BlockPos pos) {
        EnderSourceJarEntity tile = (EnderSourceJarEntity)worldIn.getBlockEntity(pos);
        if (tile != null && !tile.getFrequency().hasOwner() && tile.getSource() > 0) {
            int step = (tile.getMaxSource() - 1) / 14;
            return (tile.getSource() - 1) / step + 1;
        }
        return 0;
    }

    @SuppressWarnings("deprecation")
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        Frequency frequency = Frequency.readFromStack(stack);
        int mana = EnderStorageManager.instance(true).getStorage(frequency, EnderSourceStorage.TYPE).getStorage().getSource();
        tooltip.add(Component.translatable("ars_nouveau.source_jar.fullness", mana * 100 / 10000));
        frequency.ownerName().ifPresent(tooltip::add);
        tooltip.add(frequency.getTooltip());
    }

    public boolean isPathfindable(@NotNull BlockState pState, @NotNull PathComputationType pType) {
        return false;
    }

    @Override
    public @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult clientHit) {
        if (world.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        BlockEntity tile = world.getBlockEntity(pos);
        if (!(tile instanceof EnderSourceJarEntity owner)) {
            return ItemInteractionResult.FAIL;
        }

        //Normal block trace.
        HitResult rawHit = RayTracer.retrace(player);
        if (!(rawHit instanceof SubHitBlockHitResult hit)) {
            return ItemInteractionResult.FAIL;
        }
        if (hit.subHit == 4) {
            ItemStack item = player.getInventory().getSelected();
            if (player.isCrouching() && owner.getFrequency().hasOwner()) {
                if (!player.getAbilities().instabuild && !player.getInventory().add(EnderStorageConfig.getPersonalItem().copy())) {
                    return ItemInteractionResult.FAIL;
                }

                owner.setFreq(owner.getFrequency().withoutOwner());
                return ItemInteractionResult.SUCCESS;
            } else if (!item.isEmpty() && ItemUtils.areStacksSameType(item, EnderStorageConfig.getPersonalItem())) {
                if (!owner.getFrequency().hasOwner()) {
                    owner.setFreq(owner.getFrequency().withOwner(player));
                    if (!player.getAbilities().instabuild) {
                        item.shrink(1);
                    }
                    return ItemInteractionResult.SUCCESS;
                }
            }
        } else if (hit.subHit >= 1 && hit.subHit <= 3) {
            ItemStack item = player.getInventory().getSelected();
            if (!item.isEmpty()) {
                EnumColour dye = EnumColour.fromDyeStack(item);
                if (dye != null) {
                    EnumColour[] colours = { null, null, null };
                    if (colours[hit.subHit - 1] == dye) {
                        return ItemInteractionResult.FAIL;
                    }
                    colours[hit.subHit - 1] = dye;
                    owner.setFreq(owner.getFrequency().withColours(colours));
                    if (!player.getAbilities().instabuild) {
                        item.shrink(1);
                    }
                    return ItemInteractionResult.FAIL;
                }
            }
        }
        return ItemInteractionResult.FAIL;
    }

    @Override
    public void setPlacedBy(Level world, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        if (world.getBlockEntity(pos) instanceof TileFrequencyOwner tile) {
            tile.onPlaced(placer);
        }
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = new ArrayList<>();
        EnderSourceJarEntity tile = (EnderSourceJarEntity) builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (tile != null) {
            drops.add(createItem(tile.getFrequency()));
            if (EnderStorageConfig.anarchyMode && tile.getFrequency().hasOwner())
                drops.add(EnderStorageConfig.getPersonalItem().copy());
        }
        return drops;
    }

    @SuppressWarnings("deprecation")
    private ItemStack createItem(Frequency freq) {
        if (EnderStorageConfig.anarchyMode)
            freq = freq.withoutOwner();
        ItemStack stack = new ItemStack(this, 1);
        freq.writeToStack(stack);
        return stack;
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockState state, @NotNull HitResult hit, @NotNull LevelReader reader, @NotNull BlockPos pos, @NotNull Player player) {
        ItemStack stack = super.getCloneItemStack(state, hit, reader, pos, player);
        reader.getBlockEntity(pos, ENDER_SOURCE_JAR_TILE.get()).ifPresent((entity) -> entity.saveToItem(stack, reader.registryAccess()));
        return stack;
    }

    static {
        for (int button = 0; button < 3; button++) {
            Cuboid6 cuboid = TileFrequencyOwner.SELECTION_BUTTON.copy();
            cuboid.apply(new Scale(0.75, 0.75, 0.75));
            cuboid.apply(new Translation((5.5 + button * 2.5) / 16.0, 15.0 / 16.0, 0.5));
            buttons[button] = new IndexedVoxelShape(VoxelShapeCache.getShape(cuboid), button + 1);
        }
        ImmutableSet.Builder<IndexedVoxelShape> cuboids = ImmutableSet.builder();
        cuboids.add(jar);
        cuboids.add(buttons);
        cuboids.add(lid);
        shape = new MultiIndexedVoxelShape(jar, cuboids.build());
    }
}