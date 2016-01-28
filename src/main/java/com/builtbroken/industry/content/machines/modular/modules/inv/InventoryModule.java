package com.builtbroken.industry.content.machines.modular.modules.inv;

import com.builtbroken.industry.content.machines.modular.TileDynamicMachine;
import com.builtbroken.industry.content.machines.modular.modules.MachineModule;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/28/2016.
 */
public abstract class InventoryModule extends MachineModule
{
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

    /**
     * Adds an item to the inventory
     *
     * @param stack
     * @return what is left of the stack
     */
    public abstract ItemStack addItem(final ItemStack stack);

    /**
     * Adds an item to the inventory
     *
     * @param stack
     * @return
     */
    public abstract ItemStack addItem(final ItemStack stack, int slot);

    /**
     * Removes an item from the inventory up to
     * max stack size of the item
     *
     * @param stack - stack to match
     * @return
     */
    public ItemStack removeItem(final ItemStack stack)
    {
        return stack != null ? removeItem(stack, stack.getMaxStackSize()) : null;
    }

    /**
     * Removes an item from the inventory up to the count provided
     *
     * @param stack - stack to match
     * @param count - amount of items to remove
     * @return
     */
    public ItemStack removeItem(final ItemStack stack, int count)
    {
        return removeItem(stack, count, false);
    }

    /**
     * Removes an item from the inventory up to the count provided
     *
     * @param stack - stack to match
     * @param count - amount of items to remove
     * @return
     */
    public abstract ItemStack removeItem(final ItemStack stack, int count, boolean exact);

    /**
     * Removes an item from the inventory up to max stacksize
     *
     * @param slot - slot to remove the item from
     * @return
     */
    public ItemStack removeItem(int slot)
    {
        return removeItem(slot, 64);
    }

    /**
     * Removes an item from the inventory up to the count provided
     *
     * @param slot  - slot to remove the item from
     * @param count - amount of items to remove
     * @return
     */
    public ItemStack removeItem(int slot, int count)
    {
        return removeItem(slot, count, false);
    }

    /**
     * Removes an item from the inventory up to the count provided
     *
     * @param slot  - slot to remove the item from
     * @param count - amount of items to remove
     * @param exact - match count exactly
     * @return
     */
    public abstract ItemStack removeItem(int slot, int count, boolean exact);

    /**
     * Gets ItemStack contained in the slot
     *
     * @param slot - slot #
     * @return slot contents or null if nothing
     */
    public abstract ItemStack getItem(int slot);

    /**
     * Gets all slots containing the stack, ignores stack size.
     * Is not a new list but a cached value. Never modify or
     * the cache may stop functioning as intended.
     *
     * @param stack
     * @return cached list of slots to contents
     */
    public abstract List<Integer> getSlotsContaining(ItemStack stack);

    /**
     * Gets all slots containing the stack that still have room to
     * store more items, ignores stack size.
     * Is not a new list but a cached value. Never modify or
     * the cache may stop functioning as intended.
     *
     * @param stack
     * @return list of near empty slots, new list each call
     */
    public abstract List<Integer> getNearEmptySlotsContaining(ItemStack stack);

    /**
     * Checks if the inventory contains the stack in question.
     * Does not check stack size.
     *
     * @param stack - item being searched for, ignores stack size
     * @return true if the inventory contains at least 1 item of the type
     */
    public boolean contains(ItemStack stack)
    {
        return getContainsCount(stack) > 0;
    }

    /**
     * Checks if the inventory contains the stack in question.
     * Compares stack size
     *
     * @param stack - item being searched
     * @return true if the inventory contains more than the stacksize of
     * the item passed in.
     */
    public boolean containsExact(ItemStack stack)
    {
        return stack == null ? false : getContainsCount(stack) > stack.stackSize;
    }

    /**
     * Gets the count of items in the inventory of the type.
     *
     * @param stack - item being search for
     * @return number of items contained of type.
     */
    public abstract int getContainsCount(ItemStack stack);

    /**
     * Checks if the array of items is present in the inventory.
     *
     * @param stacks - array of items
     * @return true if all items exist with matching stackSizes
     */
    public abstract boolean containsAll(final ItemStack... stacks);

    /**
     * Gets a list of items contained in the inventory
     *
     * @return list of items, never null
     */
    public abstract Collection<ItemStack> getContainedItems();
}
