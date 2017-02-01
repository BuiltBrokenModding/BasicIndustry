package com.builtbroken.industry.content.power.wires;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 2/1/2017.
 */
public class ISBRHWires implements ISimpleBlockRenderingHandler
{
    public final static int ID = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
    {

    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        final double size = 0.4;
        final double spacer = (1 - size) / 2.0;

        //Renders center of block
        renderer.setRenderBounds(spacer, spacer, spacer, 1 - spacer, 1 - spacer, 1 - spacer);
        renderer.renderStandardBlock(block, x, y, z);

        //Render connections
        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile instanceof TileWire)
        {

        }
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId)
    {
        return false;
    }

    @Override
    public int getRenderId()
    {
        return ID;
    }

    public void renderBlock(RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon)
    {
        renderer.renderFaceYNeg(block, (double) x, (double) y, (double) z, icon);
        renderer.renderFaceYPos(block, (double) x, (double) y, (double) z, icon);
        renderer.renderFaceZNeg(block, (double) x, (double) y, (double) z, icon);
        renderer.renderFaceZPos(block, (double) x, (double) y, (double) z, icon);
        renderer.renderFaceXNeg(block, (double) x, (double) y, (double) z, icon);
        renderer.renderFaceXPos(block, (double) x, (double) y, (double) z, icon);
    }
}
