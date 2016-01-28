package com.builtbroken.industry.content.machines.modular.modules.inv;

import com.builtbroken.industry.content.machines.modular.TileDynamicMachine;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.items.ItemStackWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.*;

/**
 * Implementation of basic inventory functionality without IInventory or similar interfaces. Design
 * to act like a list of items more than an inventory.
 * <p/>
 * Caches values to help reduce CPU times for repeat operations when machines are automated.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/12/2016.
 */
public class InventoryModuleMap extends InventoryModule
{
    /** Inventory as a map of slots to slot content, reduces CPU time in theory */
    protected HashMap<Integer, ItemStack> inventory;
    /** Map of contained items to slots taken, reduces CPU time in theory */
    protected HashMap<ItemStackWrapper, List<Integer>> stacksToSlots;
    /** List of empty slots, reduces CPU time in theory */
    protected List<Integer> emptySlots;

    /**
     * Default constructor
     *
     * @param name
     * @param machine - host of the machine
     */
    public InventoryModuleMap(String name, TileDynamicMachine machine)
    {
        super(name, machine);
    }


    @Override
    public ItemStack addItem(final ItemStack stack)
    {
        if (stack != null)
        {
            ItemStack stackCopy = stack.copy();
            List<Integer> partialSlots = getNearEmptySlotsContaining(stack);
            if (partialSlots != null && !partialSlots.isEmpty())
            {
                //TODO loop threw slots and insert stack until stack is gone
            }
            //TODO find first empty slot if no partial
            return stackCopy;
        }
        return null;
    }

    @Override
    public ItemStack addItem(final ItemStack stack, int slot)
    {
        return null;
    }

    @Override
    public ItemStack removeItem(ItemStack stack, int count, boolean exact)
    {
        return null;
    }

    @Override
    public ItemStack removeItem(int slot, int count, boolean exact)
    {
        return null;
    }

    @Override
    public ItemStack getItem(int slot)
    {
        return getInventoryMap().get(slot);
    }

    @Override
    public List<Integer> getSlotsContaining(ItemStack stack)
    {
        if (stack != null)
        {
            //Ensure cache is built or this method is useless
            //Only new machines should have an null cache
            //Could cause first calls to be longer but will average out
            if (stacksToSlots == null)
            {
                updateCache();
            }

            //Build key
            ItemStackWrapper wrapper = new ItemStackWrapper(stack);
            wrapper.stack_size = false;
            //Check if we have a slot cache
            if (stacksToSlots.containsKey(wrapper))
            {
                return stacksToSlots.get(wrapper);
            }
        }
        return null;
    }

    @Override
    public List<Integer> getNearEmptySlotsContaining(ItemStack stack)
    {
        List<Integer> list = getSlotsContaining(stack);
        if (list != null)
        {
            List<Integer> reList = new ArrayList();
            for (int i : list)
            {
                ItemStack slotStack = getItem(i);
                if (slotStack == null || slotStack.stackSize < slotStack.getMaxStackSize())
                {
                    reList.add(i);
                }
            }
            return reList;
        }
        return null;
    }

    /**
     * Gets the count of items in the inventory of the type.
     *
     * @param stack - item being search for
     * @return number of items contained of type.
     */
    public int getContainsCount(ItemStack stack)
    {
        if (inventory != null && stack != null && stack.getItem() != null)
        {
            int count = 0;
            for (ItemStack itemStack : inventory.values())
            {
                if (InventoryUtility.stacksMatch(stack, itemStack))
                {
                    count += itemStack.stackSize;
                }
            }
            return count;
        }
        return 0;
    }

    /**
     * Checks if the array of items is present in the inventory.
     *
     * @param stacks - array of items
     * @return true if all items exist with matching stackSizes
     */
    public boolean containsAll(final ItemStack... stacks)
    {
        if (inventory != null && stacks != null && stacks.length > 0)
        {
            //Convert array into list copy
            List<ItemStack> stackCopy = new ArrayList(stacks.length);
            for (ItemStack stack : stacks)
            {
                if (stack != null)
                {
                    stackCopy.add(stack.copy());
                }
            }
            //Loop over inventory
            for (ItemStack itemStack : inventory.values())
            {
                //Loop over remaining entries
                Iterator<ItemStack> it = stackCopy.iterator();
                while (it.hasNext())
                {
                    //If contained decrease stack until removed
                    ItemStack stack = it.next();
                    if (InventoryUtility.stacksMatch(stack, itemStack))
                    {
                        stack.stackSize -= itemStack.stackSize;
                        if (stack.stackSize <= 0)
                        {
                            it.remove();
                        }
                    }
                }
            }
            return stackCopy.isEmpty();
        }
        return false;
    }

    /**
     * Gets a list of items contained in the inventory
     *
     * @return list of items, never null
     */
    public Collection<ItemStack> getContainedItems()
    {
        return getInventoryMap().values();
    }

    /**
     * Gets a map of slots to items stored.
     * Creats a new map if the map reference is null
     *
     * @return map, never null
     */
    public Map<Integer, ItemStack> getInventoryMap()
    {
        if (inventory == null)
        {
            inventory = new HashMap();
        }
        return inventory;
    }

    /**
     * Loops threw inventory looking for empty slots.
     */
    protected void updateEmptySlots()
    {
        if (emptySlots == null)
        {
            emptySlots = new ArrayList();
        }
        else
        {
            emptySlots.clear();
        }
        for (Map.Entry<Integer, ItemStack> entry : getInventoryMap().entrySet())
        {
            if (entry.getValue() != null)
            {
                emptySlots.add(entry.getKey());
            }
        }
    }

    /**
     * Updates cached data, not including empty slots
     */
    protected void updateCache()
    {
        if (stacksToSlots == null)
        {
            stacksToSlots = new HashMap();
        }
        else
        {
            stacksToSlots.clear();
        }
        for (Map.Entry<Integer, ItemStack> entry : getInventoryMap().entrySet())
        {
            //Generate key
            ItemStackWrapper stack = new ItemStackWrapper(entry.getValue());
            stack.stack_size = false;

            //Load old list, or create new
            List<Integer> list;
            if (stacksToSlots.containsKey(stack))
            {
                list = stacksToSlots.get(stack);
                if (list == null)
                {
                    list = new ArrayList();
                }
            }
            else
            {
                list = new ArrayList();
            }
            //Add value
            list.add(entry.getKey());
            stacksToSlots.put(stack, list);
        }
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        if (nbt.hasKey("items"))
        {
            NBTTagList list = nbt.getTagList("items", 10);
            for (int i = 0; i < list.tagCount(); i++)
            {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                int slot = tag.getInteger("_slot");
                ItemStack stack = ItemStack.loadItemStackFromNBT(tag);
                getInventoryMap().put(slot, stack);
            }
            updateEmptySlots();
            updateCache();
        }
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        if (!getInventoryMap().isEmpty())
        {
            NBTTagList list = new NBTTagList();
            for (Map.Entry<Integer, ItemStack> entry : getInventoryMap().entrySet())
            {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger("_slot", entry.getKey());
                entry.getValue().writeToNBT(tag);
            }
            nbt.setTag("items", list);
        }
        return nbt;
    }
}
