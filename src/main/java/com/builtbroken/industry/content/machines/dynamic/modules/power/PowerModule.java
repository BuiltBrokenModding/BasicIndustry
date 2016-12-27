package com.builtbroken.industry.content.machines.dynamic.modules.power;

import com.builtbroken.industry.content.machines.dynamic.modules.inv.InventoryModule;
import com.builtbroken.mc.api.tile.node.IExternalInventory;
import net.minecraft.item.ItemStack;

/**
 * Part of the machine that is responsible for generating and storing power for the machine core to consume.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/26/2016.
 */
public abstract class PowerModule<I extends IExternalInventory> extends InventoryModule<I>
{
    /**
     * Simple generic power value that can be used by anything.
     * <p>
     * This value can represent watts, rotational power, cook time, etc
     * <p>
     * The value is entirely generic and is consumed as a standard
     * unity by the machine core. This way the core does not need
     * to know what kind of power module is installed. All it needs
     * to know is that the power value is greater than what it
     * needs to consume to run for another tick.
     */
    protected int power = 0;

    /**
     * Default constructor
     *
     * @param stack
     * @param name
     */
    public PowerModule(ItemStack stack, String name)
    {
        super(stack, "power." + name);
    }

    /**
     * Called to consume power from the module
     *
     * @param powerToConsume - amount of power to consume
     * @param doAction       - true to consume power, false to check if can consume
     * @return
     */
    public boolean consumePower(int powerToConsume, boolean doAction)
    {
        if (power >= powerToConsume)
        {
            if (doAction)
            {
                power -= powerToConsume;
            }
            return true;
        }
        return false;
    }
}
