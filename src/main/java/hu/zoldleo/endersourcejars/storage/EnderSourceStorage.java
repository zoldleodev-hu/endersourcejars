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
import codechicken.enderstorage.api.StorageType;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.lib.packet.PacketCustom;
import com.hollingsworth.arsnouveau.common.capability.SourceStorage;
import hu.zoldleo.endersourcejars.network.JarNetwork;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EnderSourceStorage extends AbstractEnderStorage {
    public static final StorageType<EnderSourceStorage> TYPE = new StorageType<>("source");
    private SourceStorage storage;
    private final EnderStorageManager manager;

    public EnderSourceStorage(EnderStorageManager manager, Frequency freq) {
        super(manager, freq);
        storage = createStorage();
        this.manager = manager;
    }

    @Override
    public void clearStorage() {
        storage = createStorage();
        setDirty();
    }

    @Override
    public @NotNull String type() {
        return "source";
    }

    @Override
    public @NotNull CompoundTag saveToTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.put("source", storage.serializeNBT(registries));
        return tag;
    }

    @Override
    public void loadFromTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        storage.deserializeNBT(registries, Objects.requireNonNull(tag.get("source")));
    }

    public SourceStorage getStorage() {
        return storage;
    }

    @SuppressWarnings("deprecation")
    private SourceStorage createStorage() {
        return new SourceStorage(10000) {
            @Override
            public void setSource(int source) {
                super.setSource(source);
                setChanged();
            }

            @Override
            public int receiveSource(int source, boolean simulate) {
                if (!simulate)
                    setChanged();
                return super.receiveSource(source, simulate);
            }

            @Override
            public int extractSource(int source, boolean simulate) {
                if (!simulate)
                    setChanged();
                return super.extractSource(source, simulate);
            }

            public void setChanged() {
                setDirty();

                if (!manager.client) {
                    PacketCustom packet = new PacketCustom(JarNetwork.NET_CHANNEL, 0, null);
                    freq.writeToPacket(packet);
                    packet.writeInt(source);
                    packet.sendToAllPlayers();
                    NeoForge.EVENT_BUS.post(new SourceStorageUpdateEvent(freq));
                }
            }
        };
    }
}