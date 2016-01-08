package com.builtbroken.industry.content.machines.tests;

import com.builtbroken.industry.content.machines.prefab.TileSimpleProcessor;
import com.builtbroken.mc.api.recipe.MachineRecipeType;

/** Turns ore into rubble, and rubble into metal fragments
 * Created by robert on 1/7/2015.
 */
public class TileOreCrusher extends TileSimpleProcessor
{
    public TileOreCrusher()
    {
        super("BasicOreCrusher", MachineRecipeType.ITEM_CRUSHER, 2);
        this.max_processing_ticks = 400;
    }
}
