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

package hu.zoldleo.endersourcejars.compat;

import com.hollingsworth.ars_creo.contraption.SourceJarBehavior;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import hu.zoldleo.endersourcejars.EnderSourceJars;

public class CreateCompat {
    public static void setup() {
        MovementBehaviour.REGISTRY.register(EnderSourceJars.ENDER_SOURCE_JAR_BLOCK.get(), new SourceJarBehavior());
    }
}