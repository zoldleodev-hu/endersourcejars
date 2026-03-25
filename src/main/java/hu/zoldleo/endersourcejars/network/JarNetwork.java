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

import codechicken.lib.packet.PacketCustomChannel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;

import static hu.zoldleo.endersourcejars.EnderSourceJars.MODID;

public class JarNetwork {
    public static final ResourceLocation NET_CHANNEL = ResourceLocation.fromNamespaceAndPath(MODID, "network");
    public static final PacketCustomChannel channel = new PacketCustomChannel(NET_CHANNEL)
            .versioned("1.0")
            .client(() -> JarCPH::new)
            .server(() -> JarSPH::new);

    public static final int C_SOURCE_SYNC = 0;
    public static final int C_TILE_UPDATE = 1;

    public static void init(IEventBus modBus) {
        channel.init(modBus);
    }
}