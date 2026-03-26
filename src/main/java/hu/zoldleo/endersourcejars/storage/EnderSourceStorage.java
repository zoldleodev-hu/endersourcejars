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

package hu.zoldleo.endersourcejars.storage;

import codechicken.enderstorage.api.AbstractEnderStorage;
import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.lib.packet.PacketCustom;
import hu.zoldleo.endersourcejars.network.JarNetwork;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

public class EnderSourceStorage extends AbstractEnderStorage {
    public static final EnderStorageManager.StorageType<EnderSourceStorage> TYPE = new EnderStorageManager.StorageType<>("source");
    private int source;
    private int capacity;

    public EnderSourceStorage(EnderStorageManager manager, Frequency freq, int capacity) {
        super(manager, freq);
        this.capacity = capacity;
    }

    @Override
    public void clearStorage() {
        source = 0;
        setDirty();
    }

    @Override
    public @NotNull String type() {
        return "source";
    }

    @Override
    public @NotNull CompoundTag saveToTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("source", source);
        return tag;
    }

    @Override
    public void loadFromTag(@NotNull CompoundTag tag) {
        source = tag.getInt("source");
    }

    public int getSource() {
        return source;
    }

    public int setSource(int source) {
        this.source = Math.max(0, Math.min(source, capacity));
        setChanged();
        return source;
    }

    public int getMaxSource() {
        return capacity;
    }

    public void setMaxSource(int max) {
        capacity = max;
        setChanged();
    }

    public void setChanged() {
        setDirty();

        if (!manager.client) {
            PacketCustom packet = new PacketCustom(JarNetwork.NET_CHANNEL, 0);
            freq.writeToPacket(packet);
            packet.writeInt(source);
            packet.sendToClients();
            Mod.EventBusSubscriber.Bus.FORGE.bus().get().post(new SourceStorageUpdateEvent((freq)));
        }
    }
}