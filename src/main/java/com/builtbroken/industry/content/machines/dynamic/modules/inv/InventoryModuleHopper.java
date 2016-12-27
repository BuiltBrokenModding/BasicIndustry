package com.builtbroken.industry.content.machines.dynamic.modules.inv;

import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/26/2016.
 */
public class InventoryModuleHopper extends InventoryModuleBasic
{
    /**
     * Default constructor
     *
     * @param stack
     */
    public InventoryModuleHopper(ItemStack stack)
    {
        super(stack, "hopper", 5);
    }

    //TODO implement auto output to bottom and sides of the machine
}
