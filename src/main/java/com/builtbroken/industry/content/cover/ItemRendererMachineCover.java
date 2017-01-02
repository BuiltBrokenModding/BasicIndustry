package com.builtbroken.industry.content.cover;

import com.builtbroken.mc.lib.render.RenderUtility;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/30/2016.
 */
public class ItemRendererMachineCover implements IItemRenderer
{
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        GL11.glPushMatrix();
        Block block = ItemMachineCover.getBlock(item);
        if (block == null)
        {
            block = Blocks.lava;
        }
        RenderUtility.renderCube(0, 0, .8, 1, 1, 1, block, null, ItemMachineCover.getMeta(item));
        GL11.glPopMatrix();
    }
}
