package com.builtbroken.industry.content.machines.modular.modules;

import com.builtbroken.industry.content.machines.modular.TileDynamicMachine;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/12/2016.
 */
public class InventoryModule extends MachineModule
{
    protected HashMap<Integer, ItemStack> inventory;

    /**
     * Default constructor
     *
     * @param name
     * @param machine - host of the machine
     */
    public InventoryModule(String name, TileDynamicMachine machine)
    {
        super(name, machine);
    }
}
