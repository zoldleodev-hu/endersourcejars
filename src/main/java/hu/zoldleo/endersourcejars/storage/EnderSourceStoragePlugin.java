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

import codechicken.enderstorage.api.EnderStoragePlugin;
import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.api.StorageType;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.lib.packet.PacketCustom;
import hu.zoldleo.endersourcejars.network.JarNetwork;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EnderSourceStoragePlugin implements EnderStoragePlugin<EnderSourceStorage> {
    @Override
    public @NotNull EnderSourceStorage createEnderStorage(@NotNull EnderStorageManager manager, @NotNull Frequency freq) {
        return new EnderSourceStorage(manager, freq);
    }

    @Override
    public @NotNull StorageType<EnderSourceStorage> identifier() {
        return EnderSourceStorage.TYPE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void sendClientInfo(@NotNull ServerPlayer player, @NotNull List<EnderSourceStorage> list) {
        for (EnderSourceStorage storage : list) {
            PacketCustom packet = new PacketCustom(JarNetwork.NET_CHANNEL, JarNetwork.C_SOURCE_SYNC, null);
            storage.freq.writeToPacket(packet);
            packet.writeInt(storage.getStorage().getSource());
            packet.sendToPlayer(player);
        }
    }
}