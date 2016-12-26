package com.builtbroken.industry.content.machines.modular.modules.controllers;

import com.builtbroken.industry.content.machines.modular.modules.MachineModule;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/26/2016.
 */
public class ControlModule extends MachineModule
{
    /**
     * Default constructor
     *
     * @param stack
     * @param name
     */
    public ControlModule(ItemStack stack, String name)
    {
        super(stack, "controller." + name);
    }
}
