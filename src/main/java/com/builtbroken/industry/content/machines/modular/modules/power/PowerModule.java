package com.builtbroken.industry.content.machines.modular.modules.power;

import com.builtbroken.industry.content.machines.modular.TileDynamicMachine;
import com.builtbroken.industry.content.machines.modular.modules.MachineModule;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/26/2016.
 */
public class PowerModule extends MachineModule
{
    /**
     * Default constructor
     *
     * @param stack
     * @param name
     * @param machine - host of the machine
     */
    public PowerModule(ItemStack stack, String name, TileDynamicMachine machine)
    {
        super(stack, "power." + name, machine);
    }
}