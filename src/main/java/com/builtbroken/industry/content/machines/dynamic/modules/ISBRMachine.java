package com.builtbroken.industry.content.machines.dynamic.modules;

import com.builtbroken.industry.content.machines.dynamic.TileDynamicMachine;
import com.builtbroken.industry.content.machines.dynamic.modules.cores.MachineCore;
import com.builtbroken.mc.lib.render.RenderUtility;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/30/2016.
 */
public class ISBRMachine implements ISimpleBlockRenderingHandler
{
    public final static int ID = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
    {
        RenderUtility.renderCube(0, 0, 0, 1, 1, 1, block, null, metadata);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        renderer.renderStandardBlock(block, x, y, z);
        renderer.renderFromInside = true;
        renderer.setRenderBounds(.01, .01, .01, .99, .99, .99);
        renderer.renderStandardBlock(block, x, y, z);
        renderer.renderFromInside = false;

        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile instanceof TileDynamicMachine)
        {
            MachineCore core = ((TileDynamicMachine) tile).getMachineCore();
            if(core != null)
            {
                renderer.setRenderBounds(.2, .4, .2, .8, .6, .8);
                renderer.renderStandardBlock(Blocks.iron_block, x, y, z);

                if(core.getInputInventory() != null)
                {
                    renderer.setRenderBounds(.2, .6, .2, .8, .8, .8);
                    renderer.renderStandardBlock(Blocks.planks, x, y, z);
                }
                if(core.getOutputInventory() != null)
                {
                    renderer.setRenderBounds(.2, .2, .2, .8, .4, .8);
                    renderer.renderStandardBlock(Blocks.wool, x, y, z);
                }
            }
        }
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId)
    {
        return true;
    }

    @Override
    public int getRenderId()
    {
        return ID;
    }
}
