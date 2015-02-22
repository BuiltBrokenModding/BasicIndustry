package com.builtbroken.industry.content.machines;

import com.builtbroken.industry.content.machines.prefab.TileProcessor;
import com.builtbroken.industry.content.machines.prefab.TileSimpleProcessor;
import com.builtbroken.jlib.data.Colors;
import com.builtbroken.mc.api.recipe.MachineRecipeType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

/** Turns metal fragments into metal dust
 * Created by robert on 1/7/2015.
 */
public class TileOreGrinder extends TileSimpleProcessor
{
    public TileOreGrinder()
    {
        super("BasicOreGrinder", MachineRecipeType.ITEM_GRINDER, 2);
        this.max_processing_ticks = 400;
    }

    @SideOnly(Side.CLIENT)
    public int getColorMultiplier()
    {
        return Colors.PINK.toInt();
    }

}
