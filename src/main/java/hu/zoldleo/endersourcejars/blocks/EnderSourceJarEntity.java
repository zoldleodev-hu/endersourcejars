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
import hu.zoldleo.endersourcejars.EnderSourceJars;
import hu.zoldleo.endersourcejars.storage.EnderSourceStorage;
import hu.zoldleo.endersourcejars.storage.SourceStorageUpdateEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EnderSourceJarEntity extends SourceJarTile {
    private Frequency frequency = new Frequency();

    public EnderSourceJarEntity(BlockPos pos, BlockState state) {
        super(EnderSourceJars.ENDER_SOURCE_JAR_TILE.get(), pos, state);
        Mod.EventBusSubscriber.Bus.FORGE.bus().get().register(this);
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
    public void load(@NotNull CompoundTag tag) {
        if (tag.contains("Frequency"))
            frequency = new Frequency(tag.getCompound("Frequency"));
        super.load(tag);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Frequency", frequency.writeToNBT(new CompoundTag()));
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void onDataPacket(@NotNull Connection net, @NotNull ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        assert pkt.getTag() != null;
        this.load(pkt.getTag());
    }

    @Override
    public int getSource() {
        return EnderStorageManager.instance(false).getStorage(frequency, EnderSourceStorage.TYPE).getSource();
    }

    @Override
    public int setSource(int source) {
        return EnderStorageManager.instance(false).getStorage(frequency, EnderSourceStorage.TYPE).setSource(source);
    }

    @Override
    public int getMaxSource() {
        return EnderStorageManager.instance(false).getStorage(frequency, EnderSourceStorage.TYPE).getMaxSource();
    }

    @Override
    public void setMaxSource(int max) {
        EnderStorageManager.instance(false).getStorage(frequency, EnderSourceStorage.TYPE).setMaxSource(max);
    }

    public void onFrequencySet() {

        //invalidateCapabilities(); // No cached capabilities
    }

    protected void sendUpdatePacket() {
        assert level != null;
        createPacket().sendToChunk(this);
    }

    public PacketCustom createPacket() {
        PacketCustom packet = new PacketCustom(EnderStorageNetwork.NET_CHANNEL, EnderStorageNetwork.C_TILE_UPDATE);
        packet.writePos(getBlockPos());
        writeToPacket(packet);
        return packet;
    }

    public void writeToPacket(MCDataOutput packet) {
        frequency.writeToPacket(packet);
    }

    public void readFromPacket(MCDataInput packet) {
        frequency = Frequency.readFromPacket(packet);
        assert level != null;
        onFrequencySet();
        setChanged();
    }

    @SubscribeEvent
    public void onSourceStorageUpdate(SourceStorageUpdateEvent event) {
        if (level == null || !isSameFrequency(event.getFrequency(), frequency))
            return;
        setChanged();
    }

    private static boolean isSameFrequency(Frequency freq1, Frequency freq2) {
        return freq1.left == freq2.left &&
                freq1.middle == freq2.middle &&
                freq1.right == freq2.right &&
                Objects.equals(freq1.owner, freq2.owner);
    }
}