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
import codechicken.enderstorage.client.model.ButtonModelLibrary;
import codechicken.enderstorage.client.render.RenderCustomEndPortal;
import codechicken.lib.colour.EnumColour;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCModelLibrary;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.util.ClientUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.Vertex5;
import codechicken.lib.vec.uv.UVTranslation;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import hu.zoldleo.endersourcejars.EnderSourceJars;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.NotNull;

import static codechicken.enderstorage.EnderStorage.MOD_ID;
import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

public class EnderSourceJarRenderer implements BlockEntityRenderer<EnderSourceJarEntity> {
    private static final RenderType buttonType = RenderType.entitySolid(new ResourceLocation(MOD_ID, "textures/buttons.png"));
    private static final RenderType pearlType = CCModelLibrary.getIcos4RenderType(new ResourceLocation(MOD_ID, "textures/hedronmap.png"));
    private static final RenderType lidType = RenderType.entitySolid(new ResourceLocation(EnderSourceJars.MODID, "textures/block/source_jar.png"));
    private static final RenderType lockType = RenderType.entitySolid(new ResourceLocation(EnderSourceJars.MODID, "textures/block/source_jar_lock.png"));
    private static final Material sourceMaterial = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(MODID, "block/mana_still"));
    private static final RenderCustomEndPortal renderEndPortal = new RenderCustomEndPortal(0.126, 0.251, 0.749, 0.251, 0.749);

    private static final CCModel sourceModel100 = genModel(0);
    private static final CCModel sourceModel90 = genModel(1);
    private static final CCModel sourceModel80 = genModel(2);
    private static final CCModel sourceModel70 = genModel(3);
    private static final CCModel sourceModel60 = genModel(4);
    private static final CCModel sourceModel50 = genModel(5);
    private static final CCModel sourceModel40 = genModel(6);
    private static final CCModel sourceModel30 = genModel(7);
    private static final CCModel sourceModel20 = genModel(8);
    private static final CCModel sourceModel10 = genModel(9);

    private static final CCModel lidModel = genLid();

    @SuppressWarnings("unused")
    public EnderSourceJarRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@NotNull EnderSourceJarEntity enderSourceJarEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;
        renderSource(ccrs, new Matrix4(poseStack), multiBufferSource, enderSourceJarEntity.getSource(), enderSourceJarEntity.getFrequency(), RenderUtils.getTimeOffset(enderSourceJarEntity.getBlockPos()));
    }

    public void renderSource(CCRenderState ccrs, Matrix4 mat, MultiBufferSource multiBufferSource, int source, Frequency freq, int pearlOffset) {
        // Animated source texture
        ccrs.bind(sourceMaterial.buffer(multiBufferSource, RenderType::entityCutout), DefaultVertexFormat.NEW_ENTITY);
        // Reinforced Deepslate texture for normal testing
        //ccrs.bind(RenderType.entitySolid(ResourceLocation.withDefaultNamespace("textures/block/reinforced_deepslate_side.png")), multiBufferSource);
        // Draw source or portal based on level
        switch (source / 1000) {
            case 10:
                sourceModel100.render(ccrs, mat.copy());
                break;
            case 9:
                sourceModel90.render(ccrs, mat.copy());
                renderPearl(ccrs, mat, multiBufferSource, pearlOffset);
                break;
            case 8:
                sourceModel80.render(ccrs, mat.copy());
                renderPearl(ccrs, mat, multiBufferSource, pearlOffset);
                break;
            case 7:
                sourceModel70.render(ccrs, mat.copy());
                renderPearl(ccrs, mat, multiBufferSource, pearlOffset);
                break;
            case 6:
                sourceModel60.render(ccrs, mat.copy());
                renderPearl(ccrs, mat, multiBufferSource, pearlOffset);
                break;
            case 5:
                sourceModel50.render(ccrs, mat.copy());
                renderPearl(ccrs, mat, multiBufferSource, pearlOffset);
                break;
            case 4:
                sourceModel40.render(ccrs, mat.copy());
                renderPearl(ccrs, mat, multiBufferSource, pearlOffset);
                break;
            case 3:
                sourceModel30.render(ccrs, mat.copy());
                renderPearl(ccrs, mat, multiBufferSource, pearlOffset);
                break;
            case 2:
                sourceModel20.render(ccrs, mat.copy());
                renderPearl(ccrs, mat, multiBufferSource, pearlOffset);
                break;
            case 1:
                sourceModel10.render(ccrs, mat.copy());
                renderPearl(ccrs, mat, multiBufferSource, pearlOffset);
                break;
            case 0:
                renderEndPortal.render(mat.copy(), multiBufferSource);
                renderPearl(ccrs, mat, multiBufferSource, pearlOffset);
                break;
        }
        ccrs.reset();
        // Draw buttons
        ccrs.bind(buttonType, multiBufferSource);
        EnumColour[] colours = freq.toArray();
        for (int i = 0; i < 3; i++) {
            Matrix4 buttonMat = mat.copy().translate((5.5 + i * 2.5) / 16.0, 15.0 / 16.0, 0.5).scale(0.75, 0.75, 0.75);
            ButtonModelLibrary.button.render(ccrs, buttonMat, new UVTranslation(0.25 * (colours[i].getWoolMeta() % 4), 0.25 * (colours[i].getWoolMeta() >> 2)));
        }
        ccrs.reset();
        // Draw lid
        ccrs.bind(freq.hasOwner() ? lockType : lidType, multiBufferSource);
        lidModel.render(ccrs, mat.copy());
        ccrs.reset();
    }

    private void renderPearl(CCRenderState ccrs, Matrix4 mat, MultiBufferSource multiBufferSource, int pearlOffset) {
        ccrs.reset();
        double time = ClientUtils.getRenderTime() + pearlOffset;
        Matrix4 pearlMat = RenderUtils.getMatrix(mat.copy(), new Vector3(0.5, 0.475 + RenderUtils.getPearlBob(time) * 2, 0.5), new Rotation(time / 3, Vector3.Y_POS), 0.04);
        ccrs.bind(pearlType, multiBufferSource);
        CCModelLibrary.icosahedron4.render(ccrs, pearlMat);
    }

    private static CCModel genModel(double v) {
        CCModel model = CCModel.quadModel(20);
        Vector3 min = new Vector3(4.0 / 16.0, 2.0 / 16.0, 4 / 16.0);
        Vector3 max = new Vector3(12.0 / 16.0, (12.0 - v) / 16.0, 12.0 / 16.0);
        Vector3[] corners = new Vector3[8];
        corners[0] = new Vector3(min.x, min.y, min.z);
        corners[1] = new Vector3(max.x, min.y, min.z);
        corners[2] = new Vector3(max.x, max.y, min.z);
        corners[3] = new Vector3(min.x, max.y, min.z);
        corners[4] = new Vector3(min.x, min.y, max.z);
        corners[5] = new Vector3(max.x, min.y, max.z);
        corners[6] = new Vector3(max.x, max.y, max.z);
        corners[7] = new Vector3(min.x, max.y, max.z);

        int i = 0;
        Vertex5[] verts = model.verts;

        // Up
        verts[i++] = new Vertex5(corners[7], 0.0, 0.5);
        verts[i++] = new Vertex5(corners[6], 0.5, 0.5);
        verts[i++] = new Vertex5(corners[2], 0.5, 0.0);
        verts[i++] = new Vertex5(corners[3], 0.0, 0.0);

        // South
        verts[i++] = new Vertex5(corners[4], 0.0, (10.0 - v) / 16.0);
        verts[i++] = new Vertex5(corners[5], 0.5, (10.0 - v) / 16.0);
        verts[i++] = new Vertex5(corners[6], 0.5, 0.0);
        verts[i++] = new Vertex5(corners[7], 0.0, 0.0);

        // North
        verts[i++] = new Vertex5(corners[0], 0.5, (10.0 - v) / 16.0);
        verts[i++] = new Vertex5(corners[3], 0.5, 0.0);
        verts[i++] = new Vertex5(corners[2], 0.0, 0.0);
        verts[i++] = new Vertex5(corners[1], 0.0, (10.0 - v) / 16.0);

        // East
        verts[i++] = new Vertex5(corners[6], 0.0, 0.0);
        verts[i++] = new Vertex5(corners[5], 0.0, (10.0 - v) / 16.0);
        verts[i++] = new Vertex5(corners[1], 0.5, (10.0 - v) / 16.0);
        verts[i++] = new Vertex5(corners[2], 0.5, 0.0);

        // West
        verts[i++] = new Vertex5(corners[7], 0.5, 0.0);
        verts[i++] = new Vertex5(corners[3], 0.0, 0.0);
        verts[i++] = new Vertex5(corners[0], 0.0, (10.0 - v) / 16.0);
        verts[i  ] = new Vertex5(corners[4], 0.5, (10.0 - v) / 16.0);

        return model.computeNormals();
    }

    private static CCModel genLid() {
        CCModel model = CCModel.quadModel(56);

        Vector3 minO = new Vector3(3.0 / 16.0, 14.0 / 16.0, 3.0 / 16.0);
        Vector3 maxO = new Vector3(13.0 / 16.0, 1.0, 13.0 / 16.0);

        Vector3 minI = new Vector3(4.0 / 16.0, 15.0 / 16.0, 4.0 / 16.0);
        Vector3 maxI = new Vector3(12.0 / 16.0, 1.0, 12.0 / 16.0);

        Vector3[] corners = new Vector3[16];
        corners[0] = new Vector3(minO.x, minO.y, minO.z);
        corners[1] = new Vector3(maxO.x, minO.y, minO.z);
        corners[2] = new Vector3(maxO.x, maxO.y, minO.z);
        corners[3] = new Vector3(minO.x, maxO.y, minO.z);
        corners[4] = new Vector3(minO.x, minO.y, maxO.z);
        corners[5] = new Vector3(maxO.x, minO.y, maxO.z);
        corners[6] = new Vector3(maxO.x, maxO.y, maxO.z);
        corners[7] = new Vector3(minO.x, maxO.y, maxO.z);

        corners[8]  = new Vector3(minI.x, minI.y, minI.z);
        corners[9]  = new Vector3(maxI.x, minI.y, minI.z);
        corners[10] = new Vector3(maxI.x, maxI.y, minI.z);
        corners[11] = new Vector3(minI.x, maxI.y, minI.z);
        corners[12] = new Vector3(minI.x, minI.y, maxI.z);
        corners[13] = new Vector3(maxI.x, minI.y, maxI.z);
        corners[14] = new Vector3(maxI.x, maxI.y, maxI.z);
        corners[15] = new Vector3(minI.x, maxI.y, maxI.z);

        int i = 0;
        Vertex5[] verts = model.verts;

        // Down
        verts[i++] = new Vertex5(corners[1], 42.0 / 64.0, 0.0);
        verts[i++] = new Vertex5(corners[5], 42.0 / 64.0, 10.0 / 64.0);
        verts[i++] = new Vertex5(corners[4], 0.5, 10.0 / 64.0);
        verts[i++] = new Vertex5(corners[0], 0.5, 0.0);

        // South Outer
        verts[i++] = new Vertex5(corners[4], 0.0, 36.0 / 64.0);
        verts[i++] = new Vertex5(corners[5], 10.0 / 64.0, 36.0 / 64.0);
        verts[i++] = new Vertex5(corners[6], 10.0 / 64.0, 34.0 / 64.0);
        verts[i++] = new Vertex5(corners[7], 0.0, 34.0 / 64.0);

        // North Outer
        verts[i++] = new Vertex5(corners[0], 40.0 / 64, 34.0 / 64.0);
        verts[i++] = new Vertex5(corners[3], 40.0 / 64, 32.0 / 64.0);
        verts[i++] = new Vertex5(corners[2], 30.0 / 64, 32.0 / 64.0);
        verts[i++] = new Vertex5(corners[1], 30.0 / 64, 34.0 / 64.0);

        // East Outer
        verts[i++] = new Vertex5(corners[6], 32.0 / 64.0, 30.0 / 64);
        verts[i++] = new Vertex5(corners[5], 32.0 / 64.0, 32.0 / 64.0);
        verts[i++] = new Vertex5(corners[1], 42.0 / 64.0, 32.0 / 64.0);
        verts[i++] = new Vertex5(corners[2], 42.0 / 64.0, 30.0 / 64);

        // West Outer
        verts[i++] = new Vertex5(corners[7], 28.0 / 64.0, 34.0 / 64.0);
        verts[i++] = new Vertex5(corners[3], 18.0 / 64.0, 34.0 / 64.0);
        verts[i++] = new Vertex5(corners[0], 18.0 / 64.0, 36.0 / 64.0);
        verts[i++] = new Vertex5(corners[4], 28.0 / 64.0, 36.0 / 64.0);

        // Up Center
        verts[i++] = new Vertex5(corners[12], 9.0 / 64.0, 25.0 / 64.0);
        verts[i++] = new Vertex5(corners[13], 1.0 / 64.0, 25.0 / 64.0);
        verts[i++] = new Vertex5(corners[9], 1.0 / 64.0, 33.0 / 64.0);
        verts[i++] = new Vertex5(corners[8], 9.0 / 64.0, 33.0 / 64.0);

        // South Inner
        verts[i++] = new Vertex5(corners[8], 9.0 / 64.0, 33.0 / 64.0);
        verts[i++] = new Vertex5(corners[9], 1.0 / 64.0, 33.0 / 64.0);
        verts[i++] = new Vertex5(corners[10], 1.0 / 64.0, 34.0 / 64.0);
        verts[i++] = new Vertex5(corners[11], 9.0 / 64.0, 34.0 / 64.0);

        // North Inner
        verts[i++] = new Vertex5(corners[12], 9.0 / 64, 25.0 / 64.0);
        verts[i++] = new Vertex5(corners[15], 9.0 / 64, 24.0 / 64.0);
        verts[i++] = new Vertex5(corners[14], 1.0 / 64, 24.0 / 64.0);
        verts[i++] = new Vertex5(corners[13], 1.0 / 64, 25.0 / 64.0);

        // East Inner
        verts[i++] = new Vertex5(corners[15], 10.0 / 64.0, 25.0 / 64);
        verts[i++] = new Vertex5(corners[12], 9.0 / 64.0, 25.0 / 64);
        verts[i++] = new Vertex5(corners[8],  9.0 / 64.0, 33.0 / 64.0);
        verts[i++] = new Vertex5(corners[11], 10.0 / 64.0, 33.0 / 64.0);

        // West Inner
        verts[i++] = new Vertex5(corners[14], 0.0 / 64.0, 25.0 / 64.0);
        verts[i++] = new Vertex5(corners[10], 0.0 / 64.0, 33.0 / 64.0);
        verts[i++] = new Vertex5(corners[9],  1.0 / 64.0, 33.0 / 64.0);
        verts[i++] = new Vertex5(corners[13], 1.0 / 64, 25.0 / 64.0);

        // Up South
        verts[i++] = new Vertex5(corners[7], 10.0 / 64.0, 24.0 / 64.0);
        verts[i++] = new Vertex5(corners[6], 0.0 / 64.0, 24.0 / 64.0);
        verts[i++] = new Vertex5(corners[14], 1.0 / 64.0, 25.0 / 64.0);
        verts[i++] = new Vertex5(corners[15], 9.0 / 64.0, 25.0 / 64.0);

        // Up North
        verts[i++] = new Vertex5(corners[3], 10.0 / 64, 34.0 / 64.0);
        verts[i++] = new Vertex5(corners[11], 9.0 / 64, 33.0 / 64.0);
        verts[i++] = new Vertex5(corners[10], 1.0 / 64, 33.0 / 64.0);
        verts[i++] = new Vertex5(corners[2], 0.0 / 64, 34.0 / 64.0);

        // Up East
        verts[i++] = new Vertex5(corners[6], 0.0 / 64.0, 24.0 / 64);
        verts[i++] = new Vertex5(corners[2], 0.0 / 64.0, 34.0 / 64);
        verts[i++] = new Vertex5(corners[10],  1.0 / 64.0, 33.0 / 64.0);
        verts[i++] = new Vertex5(corners[14], 1.0 / 64.0, 25.0 / 64.0);

        // Up West
        verts[i++] = new Vertex5(corners[3], 10.0 / 64.0, 34.0 / 64.0);
        verts[i++] = new Vertex5(corners[7], 10.0 / 64.0, 24.0 / 64.0);
        verts[i++] = new Vertex5(corners[15],  9.0 / 64.0, 25.0 / 64.0);
        verts[i  ] = new Vertex5(corners[11], 9.0 / 64, 33.0 / 64.0);

        return model.computeNormals();
    }
}