package com.builtbroken.industry.content.machines;

import com.builtbroken.industry.content.machines.prefab.TileSimpleProcessor;
import com.builtbroken.mc.api.recipe.MachineRecipeType;

/**
 * Created by robert on 2/22/2015.
 */
public class TileRivetMachine extends TileSimpleProcessor
{
    public TileRivetMachine()
    {
        super("BasicRivetMachine", MachineRecipeType.PLATE_RIVETER, 3);
    }
}
