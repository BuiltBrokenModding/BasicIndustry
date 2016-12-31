package com.builtbroken.industry.content.machines.dynamic.modules.cores;

import com.builtbroken.mc.api.recipe.IMachineRecipeHandler;
import com.builtbroken.mc.api.recipe.MachineRecipeType;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/31/2016.
 */
public class MachineCoreSawmill extends MachineCore
{
    public MachineCoreSawmill(ItemStack stack)
    {
        super(stack, "sawmill");
    }

    @Override
    protected IMachineRecipeHandler getRecipeHandler()
    {
        return MachineRecipeType.ITEM_SAWMILL.getHandler();
    }

    @Override
    public int getRecipeProcessingTime()
    {
        return 300;
    }
}
