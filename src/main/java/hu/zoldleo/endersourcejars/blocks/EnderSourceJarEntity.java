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
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.network.EnderStorageNetwork;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.packet.PacketCustom;
import com.hollingsworth.arsnouveau.common.block.tile.SourceJarTile;
import com.hollingsworth.arsnouveau.common.capability.SourceStorage;
import hu.zoldleo.endersourcejars.EnderSourceJars;
import hu.zoldleo.endersourcejars.storage.EnderSourceStorage;
import hu.zoldleo.endersourcejars.storage.SourceStorageUpdateEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EnderSourceJarEntity extends SourceJarTile {
    private Frequency frequency = new Frequency();

    public EnderSourceJarEntity(BlockPos pos, BlockState state) {
        super(EnderSourceJars.ENDER_SOURCE_JAR_TILE.get(), pos, state);
        NeoForge.EVENT_BUS.register(this);
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        return EnderSourceJars.ENDER_SOURCE_JAR_TILE.get();
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFreq(Frequency frequency) {
        assert level != null;
        this.frequency = frequency;
        onFrequencySet();
        setChanged();
        BlockState state = level.getBlockState(worldPosition);
        level.sendBlockUpdated(worldPosition, state, state, 3);
        if (!level.isClientSide) {
            sendUpdatePacket();
        }
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Frequency"))
            this.frequency = Frequency.CODEC.parse(NbtOps.INSTANCE, tag.get("Frequency")).result().orElse(new Frequency());
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        Frequency.CODEC.encodeStart(NbtOps.INSTANCE, frequency).resultOrPartial(err -> {
        }).ifPresent(freqTag -> tag.put("Frequency", freqTag));
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void onDataPacket(@NotNull Connection net, @NotNull ClientboundBlockEntityDataPacket pkt, HolderLookup.@NotNull Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
        this.loadAdditional(pkt.getTag(), lookupProvider);
    }

    @Override
    public @NotNull SourceStorage getSourceStorage() {
        if (level == null)
            return new SourceStorage(0);
        return EnderStorageManager.instance(level.isClientSide).getStorage(frequency, EnderSourceStorage.TYPE).getStorage();
    }

    public void onFrequencySet() {
        invalidateCapabilities();
    }

    protected void sendUpdatePacket() {
        assert level != null;
        createPacket().sendToChunk(this);
    }

    public PacketCustom createPacket() {
        PacketCustom packet = new PacketCustom(EnderStorageNetwork.NET_CHANNEL, EnderStorageNetwork.C_TILE_UPDATE, Objects.requireNonNull(level).registryAccess());
        packet.writePos(getBlockPos());
        writeToPacket(packet);
        return packet;
    }

    public void writeToPacket(MCDataOutput packet) {
        packet.writeWithRegistryCodec(Frequency.STREAM_CODEC, frequency);
    }

    public void readFromPacket(MCDataInput packet) {
        frequency = packet.readWithRegistryCodec(Frequency.STREAM_CODEC);
        assert level != null;
        onFrequencySet();
        setChanged();
    }

    @SubscribeEvent
    public void onSourceStorageUpdate(SourceStorageUpdateEvent event) {
        if (level == null || !event.getFrequency().equals(frequency))
            return;
        setChanged();
    }
}