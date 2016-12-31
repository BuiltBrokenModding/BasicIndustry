package com.builtbroken.industry.content.machines.dynamic.modules.cores;

import com.builtbroken.mc.api.recipe.IMachineRecipeHandler;
import com.builtbroken.mc.api.recipe.MachineRecipeType;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/31/2016.
 */
public class MachineCoreSmelter extends MachineCore
{
    public MachineCoreSmelter(ItemStack stack)
    {
        super(stack, "smelter");
    }

    @Override
    protected IMachineRecipeHandler getRecipeHandler()
    {
        return MachineRecipeType.ITEM_SMELTER.getHandler();
    }

    @Override
    public int getRecipeProcessingTime()
    {
        return 300;
    }
}
