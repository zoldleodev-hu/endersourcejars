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

import codechicken.lib.packet.PacketCustomChannelBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.event.EventNetworkChannel;

import static hu.zoldleo.endersourcejars.EnderSourceJars.MODID;

public class JarNetwork {
    public static final ResourceLocation NET_CHANNEL = new ResourceLocation(MODID, "network");
    public static EventNetworkChannel channel;
    public static final int C_SOURCE_SYNC = 0;
    public static final int C_TILE_UPDATE = 1;

    public static void init() {
        channel = PacketCustomChannelBuilder.named(NET_CHANNEL).assignClientHandler(() -> JarCPH::new).assignServerHandler(() -> JarSPH::new).build();
    }
}