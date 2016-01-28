package com.builtbroken.industry.content.machines.modular.modules.inv;

import com.builtbroken.industry.content.machines.modular.TileDynamicMachine;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Inventory that only contains a single slot, most basic tier of inventory modules for machines
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/28/2016.
 */
public class InventoryModuleUnoSlot extends InventoryModule
{
    protected ItemStack slotStack;

    /**
     * Default constructor
     *
     * @param name
     * @param machine - host of the machine
     */
    public InventoryModuleUnoSlot(String name, TileDynamicMachine machine)
    {
        super(name, machine);
    }

    @Override
    public ItemStack addItem(ItemStack stack)
    {
        if (stack != null)
        {
            if (slotStack == null)
            {
                slotStack = stack.copy();
                return null;
            }
            else if (InventoryUtility.stacksMatch(slotStack, stack))
            {
                int room = slotStack.getMaxStackSize() - slotStack.stackSize;
                //insert stack is smaller
                if (room >= stack.stackSize)
                {
                    slotStack.stackSize += stack.stackSize;
                    return null;
                }
                //insert stack is larger
                else if (room > 0)
                {
                    slotStack.stackSize = slotStack.getMaxStackSize();
                    ItemStack copy = stack.copy();
                    copy.stackSize -= room;
                    if (copy.stackSize <= 0)
                    {
                        return null;
                    }
                    return copy;
                }
            }
        }
        return stack;
    }

    @Override
    public ItemStack addItem(ItemStack stack, int slot)
    {
        if (slot == 0)
        {
            return addItem(stack);
        }
        return stack;
    }

    @Override
    public ItemStack removeItem(ItemStack stack, int count, boolean exact)
    {
        if (stack != null && InventoryUtility.stacksMatch(stack, slotStack))
        {
            //Greater than or exact request
            if (slotStack.stackSize >= count)
            {
                ItemStack re = slotStack.copy();
                re.stackSize = count;

                slotStack.stackSize -= count;
                if (slotStack.stackSize <= 0)
                {
                    slotStack.stackSize = 0;
                }

                return re;
            }
            //Less than request
            else if (!exact)
            {
                ItemStack re = slotStack;
                slotStack = null;
                return re;
            }
        }
        return null;
    }

    @Override
    public ItemStack removeItem(int slot, int count, boolean exact)
    {
        if (slot == 0)
        {
            return removeItem(getItem(0), count, exact);
        }
        return null;
    }

    @Override
    public ItemStack getItem(int slot)
    {
        return slot == 0 ? slotStack : null;
    }

    @Override
    public List<Integer> getSlotsContaining(ItemStack stack)
    {
        if (stack != null && InventoryUtility.stacksMatch(stack, slotStack))
        {
            List<Integer> list = new ArrayList();
            list.add(0);
            return list;
        }
        return null;
    }

    @Override
    public List<Integer> getNearEmptySlotsContaining(ItemStack stack)
    {
        if (stack != null && slotStack.stackSize < slotStack.getMaxStackSize() && InventoryUtility.stacksMatch(stack, slotStack))
        {
            List<Integer> list = new ArrayList();
            list.add(0);
            return list;
        }
        return null;
    }

    @Override
    public int getContainsCount(ItemStack stack)
    {
        if (stack != null && InventoryUtility.stacksMatch(stack, slotStack))
        {
            return slotStack.stackSize;
        }
        return 0;
    }

    @Override
    public boolean containsAll(ItemStack... stacks)
    {
        if (stacks.length == 1)
        {
            return contains(stacks[0]);
        }
        return false;
    }

    @Override
    public Collection<ItemStack> getContainedItems()
    {
        if (slotStack != null)
        {
            List<ItemStack> list = new ArrayList();
            list.add(slotStack);
            return list;
        }
        return null;
    }
}
