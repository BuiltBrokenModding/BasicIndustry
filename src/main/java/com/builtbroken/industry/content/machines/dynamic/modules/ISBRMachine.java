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
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

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
        if (tile instanceof TileDynamicMachine)
        {
            MachineCore core = ((TileDynamicMachine) tile).getMachineCore();
            ForgeDirection direction = ((TileDynamicMachine) tile).getDirection();
            if (core != null)
            {
                renderer.setRenderBounds(.15, .3, .15, .85, .7, .85);
                renderBlock(renderer, block, x, y, z, Blocks.iron_block.getIcon(0, 0));

                switch (direction)
                {
                    case NORTH:
                        renderer.setRenderBounds(.15, .7, .15, .85, .98, .35);
                        renderBlock(renderer, block, x, y, z, Blocks.iron_block.getIcon(0, 0));
                        break;
                    case SOUTH:
                        renderer.setRenderBounds(.15, .7, .65, .85, .98, .85);
                        renderBlock(renderer, block, x, y, z, Blocks.iron_block.getIcon(0, 0));
                        break;
                    case WEST:
                        renderer.setRenderBounds(.15, .7, .15, .35, .98, .85);
                        renderBlock(renderer, block, x, y, z, Blocks.iron_block.getIcon(0, 0));
                        break;
                    case EAST:
                        renderer.setRenderBounds(.65, .7, .15, .85, .98, .85);
                        renderBlock(renderer, block, x, y, z, Blocks.iron_block.getIcon(0, 0));
                        break;
                }

                if (core.getInputInventory() != null)
                {
                    switch (direction)
                    {
                        case NORTH:
                            renderer.setRenderBounds(.2, .8, .35, .8, .98, .8);
                            renderBlock(renderer, block, x, y, z, Blocks.planks.getIcon(0, 0));
                            renderer.setRenderBounds(.3, .7, .35, .7, .8, .7);
                            renderBlock(renderer, block, x, y, z, Blocks.planks.getIcon(0, 0));
                            break;
                        case SOUTH:
                            renderer.setRenderBounds(.2, .8, .2, .8, .98, .65);
                            renderBlock(renderer, block, x, y, z, Blocks.planks.getIcon(0, 0));
                            renderer.setRenderBounds(.3, .7, .3, .7, .8, .65);
                            renderBlock(renderer, block, x, y, z, Blocks.planks.getIcon(0, 0));
                            break;
                        case WEST:
                            renderer.setRenderBounds(.35, .8, .2, .8, .98, .8);
                            renderBlock(renderer, block, x, y, z, Blocks.planks.getIcon(0, 0));
                            renderer.setRenderBounds(.35, .7, .3, .7, .8, .7);
                            renderBlock(renderer, block, x, y, z, Blocks.planks.getIcon(0, 0));
                            break;
                        case EAST:
                            renderer.setRenderBounds(.2, .8, .2, .65, .98, .8);
                            renderBlock(renderer, block, x, y, z, Blocks.planks.getIcon(0, 0));
                            renderer.setRenderBounds(.3, .7, .3, .65, .8, .7);
                            renderBlock(renderer, block, x, y, z, Blocks.planks.getIcon(0, 0));
                            break;

                    }
                }
                if (core.getOutputInventory() != null)
                {
                    renderer.setRenderBounds(.2, .1, .2, .8, .3, .8);
                    renderBlock(renderer, block, x, y, z, Blocks.planks.getIcon(0, 0));
                    renderer.setRenderBounds(.3, .02, .3, .7, .1, .7);
                    renderBlock(renderer, block, x, y, z, Blocks.planks.getIcon(0, 0));
                }
            }
        }
        return true;
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
