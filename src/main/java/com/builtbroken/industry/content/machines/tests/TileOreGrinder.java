package com.builtbroken.industry.content.machines.tests;

import com.builtbroken.industry.content.machines.prefab.TileSimpleProcessor;
import com.builtbroken.jlib.data.Colors;
import com.builtbroken.mc.api.recipe.MachineRecipeType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
