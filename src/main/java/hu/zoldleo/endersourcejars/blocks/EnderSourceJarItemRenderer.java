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
import codechicken.lib.model.PerspectiveModelState;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Matrix4;
import com.mojang.blaze3d.vertex.PoseStack;
import hu.zoldleo.endersourcejars.EnderSourceJars;
import hu.zoldleo.endersourcejars.storage.EnderSourceStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class EnderSourceJarItemRenderer implements IItemRenderer {
    private final EnderSourceJarRenderer renderer = new EnderSourceJarRenderer(null);

    @SuppressWarnings("deprecation")
    @Override
    public void renderItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext ctx, @NotNull PoseStack mStack, @NotNull MultiBufferSource source, int packedLight, int packedOverlay) {
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        Frequency freq = Frequency.readFromStack(stack);
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;
        renderer.renderSource(ccrs, new Matrix4(mStack), source,(EnderStorageManager.instance(true).getStorage(freq, EnderSourceStorage.TYPE).getStorage().getSource()), freq, 0);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(EnderSourceJars.ENDER_SOURCE_JAR_BLOCK.get().defaultBlockState(), mStack, source, packedLight, packedOverlay);
    }

    @Override
    public @Nullable PerspectiveModelState getModelState() {
        return TransformUtils.DEFAULT_BLOCK;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }
}