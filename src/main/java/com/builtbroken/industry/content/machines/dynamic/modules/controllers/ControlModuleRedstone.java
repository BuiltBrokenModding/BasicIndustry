package com.builtbroken.industry.content.machines.dynamic.modules.controllers;

import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/29/2016.
 */
public class ControlModuleRedstone extends ControlModule
{
    /**
     * Default constructor
     *
     * @param stack
     */
    public ControlModuleRedstone(ItemStack stack)
    {
        super(stack, "redstone");
    }
}
