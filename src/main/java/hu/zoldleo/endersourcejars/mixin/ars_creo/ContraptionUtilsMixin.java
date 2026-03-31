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

package hu.zoldleo.endersourcejars.mixin.ars_creo;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.manager.EnderStorageManager;
import com.hollingsworth.ars_creo.contraption.ContraptionUtils;
import com.hollingsworth.ars_creo.contraption.source.SourceInfo;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import hu.zoldleo.endersourcejars.blocks.EnderSourceJar;
import hu.zoldleo.endersourcejars.storage.EnderSourceStorage;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ContraptionUtils.class)
public class ContraptionUtilsMixin {
    @SuppressWarnings("all")
    @ModifyExpressionValue(method = "getSourceBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;", ordinal = 0))
    private static Block addEnderJar(Block block, @Local(name = "blockInfo") StructureTemplate.StructureBlockInfo blockInfo, @Local(name = "sourceBlocks") List<SourceInfo> sourceBlocks) {
        if (block instanceof EnderSourceJar) {
            Frequency frequency = new Frequency(blockInfo.nbt().getCompound("Frequency"));
            sourceBlocks.add(new SourceInfo(blockInfo, 0) {
                @Override
                public int getAmount() {
                    return EnderStorageManager.instance(false).getStorage(frequency, EnderSourceStorage.TYPE).getStorage().getSource();
                }

                @Override
                public void removeAmount(int amount) {
                    EnderStorageManager.instance(false).getStorage(frequency, EnderSourceStorage.TYPE).getStorage().extractSource(amount, false);
                }

                @Override
                public void addAmount(int amount) {
                    EnderStorageManager.instance(false).getStorage(frequency, EnderSourceStorage.TYPE).getStorage().receiveSource(amount, false);
                }

                @Override
                public void removeWithUpdate(Level level, int amount, AbstractContraptionEntity entity) {
                    removeAmount(amount);
                }

                @Override
                public void addWithUpdate(Level level, int amount, AbstractContraptionEntity entity) {
                    addAmount(amount);
                }

                @Override
                public void syncSource(AbstractContraptionEntity contraption, int nextFillState) {
                }
            });
        }
        return block;
    }
}