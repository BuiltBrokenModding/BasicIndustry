package com.builtbroken.industry.content.machines.modular.cores;

import com.builtbroken.industry.content.machines.modular.TileDynamicMachine;
import com.builtbroken.industry.content.machines.modular.modules.MachineModule;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Core of a machine handling all logic and functionality of the machine. This allows the machine to function without understanding itself or surroundings.
 * In other words the machine can exist as an entity, minecraft, or tile.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/10/2016.
 */
public class MachineCore extends MachineModule
{
    //TODO create inventory module prefab that doesn't use slots, allows for easy AE integration if slots are not used.
    /** Module that handles or is the inventory for inputting items. */
    protected MachineModule inputInventory;

    /** Module that handles or is the inventory for outputting items. */
    protected MachineModule outputInventory;

    /** Module that handles power for the machine */
    protected MachineModule powerModule; //TODO implement power module that returns speed & power

    /** Module that handles automation style controls */
    protected MachineModule controllerModule; //TODO implement basic controller with on/off, input controls, redstone, etc

    /** Hardcore setting, amount of dust in a machine, decreases effectiveness, builds over time when machine is used with minor build up when left alone  */
    protected int dustBuildUpLevel;
    /** Hardcore setting, amount of slug/grease on a machine, decreases effectiveness, builds over time when machine is used */
    protected int greaseBuildUpLevel;
    /** Hardcore setting, amount of rust on a machine, decreases effectiveness, builds over time due to no use */
    protected int rustBuildUpLevel;

    /**
     * Default constructor
     *
     * @param name
     * @param machine - host of the machine
     */
    public MachineCore(String name, TileDynamicMachine machine)
    {
        super(name, machine);
    }

    /**
     * Does the input inventory contain the stack
     *
     * @param stack - item wer are searching for
     *              with stacksize.
     * @return true if the item is contained
     */
    protected boolean hasInputStack(ItemStack stack)
    {
        //TODO add implementation
        if (inputInventory != null)
        {

        }
        return false;
    }

    /**
     * Removes items from the input inventory
     *
     * @param stack
     * @return true if all items were removed
     */
    protected boolean consumeInputStack(ItemStack stack)
    {
        //TODO add implementation
        if (inputInventory != null)
        {

        }
        return false;
    }


    /**
     * Checks if the machine has enough power to do an
     * operation.
     *
     * @return true if machine is powered
     */
    protected boolean hasPower()
    {
        return powerModule != null;
    }

    /**
     * Its there enough space to output the item
     * into the output inventory.
     *
     * @param stack - ItemStack to output
     * @return true if there is enough space
     */
    protected boolean canOutput(ItemStack stack)
    {
        //TODO add implementation
        if (outputInventory != null)
        {

        }
        return false;
    }

    @Override
    public void getContainedItems(final List<ItemStack> items)
    {
        super.getContainedItems(items);
        if (inputInventory != null)
        {
            inputInventory.getContainedItems(items);
        }
        if (outputInventory != null)
        {
            outputInventory.getContainedItems(items);
        }
        if (powerModule != null)
        {
            powerModule.getContainedItems(items);
        }
        if (controllerModule != null)
        {
            controllerModule.getContainedItems(items);
        }
    }
}
