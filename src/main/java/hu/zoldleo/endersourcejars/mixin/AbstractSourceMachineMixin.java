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

package hu.zoldleo.endersourcejars.mixin;

import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import hu.zoldleo.endersourcejars.blocks.EnderSourceJarEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSourceMachine.class)
public abstract class AbstractSourceMachineMixin extends BlockEntity {
    public AbstractSourceMachineMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Inject(method = "applyImplicitComponents", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity$DataComponentInput;getOrDefault(Ljava/util/function/Supplier;Ljava/lang/Object;)Ljava/lang/Object;"), cancellable = true)
    void skipEnderJar(DataComponentInput pComponentInput, CallbackInfo ci) {
        if ((Object)this instanceof EnderSourceJarEntity)
            ci.cancel();
    }
}