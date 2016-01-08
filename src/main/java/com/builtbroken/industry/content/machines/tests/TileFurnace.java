package com.builtbroken.industry.content.machines.tests;

import com.builtbroken.industry.content.machines.prefab.TileSimpleProcessor;
import com.builtbroken.jlib.data.Colors;
import com.builtbroken.mc.api.recipe.MachineRecipeType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Energy based version of the MC furnace
 * Created by robert on 1/7/2015.
 */
public class TileFurnace extends TileSimpleProcessor
{
    public TileFurnace()
    {
        super("BasicFurnace", MachineRecipeType.ITEM_SMELTER, 2);
    }

    @SideOnly(Side.CLIENT)
    public int getColorMultiplier()
    {
        return Colors.DARK_AQUA.toInt();
    }
}
