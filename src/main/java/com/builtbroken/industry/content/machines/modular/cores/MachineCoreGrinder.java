package com.builtbroken.industry.content.machines.modular.cores;

import com.builtbroken.industry.content.machines.modular.TileDynamicMachine;
import com.builtbroken.mc.api.recipe.IMachineRecipeHandler;
import com.builtbroken.mc.api.recipe.MachineRecipeType;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/26/2016.
 */
public class MachineCoreGrinder extends MachineCore
{
    /**
     * Default constructor
     *
     * @param machine - host of the machine
     */
    public MachineCoreGrinder(ItemStack stack, TileDynamicMachine machine)
    {
        super(stack, "grinder", machine);
    }

    @Override
    protected IMachineRecipeHandler getRecipeHandler()
    {
        return MachineRecipeType.ITEM_GRINDER.getHandler();
    }
}
