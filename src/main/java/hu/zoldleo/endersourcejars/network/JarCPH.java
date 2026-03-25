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

package hu.zoldleo.endersourcejars.network;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.lib.packet.ICustomPacketHandler;
import codechicken.lib.packet.PacketCustom;
import hu.zoldleo.endersourcejars.blocks.EnderSourceJarEntity;
import hu.zoldleo.endersourcejars.storage.EnderSourceStorage;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class JarCPH implements ICustomPacketHandler.IClientPacketHandler {
    @SuppressWarnings("deprecation")
    @Override
    public void handlePacket(@NotNull PacketCustom packet, @NotNull Minecraft mc) {
        switch (packet.getType()) {
            case JarNetwork.C_SOURCE_SYNC -> EnderStorageManager.instance(true).getStorage(Frequency.readFromPacket(packet), EnderSourceStorage.TYPE).getStorage().setSource(packet.readInt());
            case JarNetwork.C_TILE_UPDATE -> { if (Objects.requireNonNull(mc.level).getBlockEntity(packet.readPos()) instanceof EnderSourceJarEntity tile) tile.readFromPacket(packet); }
        }
    }
}