package com.builtbroken.industry.content.machines.dynamic.modules.cores;

import com.builtbroken.mc.api.recipe.IMachineRecipeHandler;
import com.builtbroken.mc.api.recipe.MachineRecipeType;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/26/2016.
 */
public class MachineCoreCrusher extends MachineCore
{
    public MachineCoreCrusher(ItemStack stack)
    {
        super(stack, "crusher");
    }

    @Override
    protected IMachineRecipeHandler getRecipeHandler()
    {
        return MachineRecipeType.ITEM_CRUSHER.getHandler();
    }

    @Override
    public int getRecipeProcessingTime()
    {
        return 300;
    }
}
