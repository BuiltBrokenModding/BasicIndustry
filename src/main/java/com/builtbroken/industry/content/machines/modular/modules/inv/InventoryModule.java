package com.builtbroken.industry.content.machines.modular.modules.inv;

import com.builtbroken.industry.content.machines.modular.TileDynamicMachine;
import com.builtbroken.industry.content.machines.modular.modules.MachineModule;
import com.builtbroken.mc.api.tile.IInventoryProvider;
import com.builtbroken.mc.api.tile.node.IExternalInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/28/2016.
 */
public abstract class InventoryModule<I extends IExternalInventory> extends MachineModule implements IExternalInventory, IInventoryProvider
{
    protected I inventory;

    /**
     * Default constructor
     *
     * @param name
     * @param machine - host of the machine
     */
    public InventoryModule(ItemStack stack, String name, TileDynamicMachine machine)
    {
        super(stack, "inv." + name, machine);
    }

    @Override
    public I getInventory()
    {
        if (inventory == null)
        {
            inventory = newInventory();
        }
        return inventory;
    }

    public abstract I newInventory();

    @Override
    public Collection<ItemStack> getContainedItems()
    {
        return getInventory().getContainedItems();
    }

    @Override
    public void clear()
    {
        getInventory().clear();
    }

    @Override
    public ArrayList<Integer> getFilledSlots()
    {
        return null;
    }

    @Override
    public ArrayList<Integer> getEmptySlots()
    {
        return getInventory().getEmptySlots();
    }

    @Override
    public ArrayList<Integer> getSlotsWithSpace()
    {
        return getInventory().getSlotsWithSpace();
    }

    @Override
    public int getSizeInventory()
    {
        return getInventory().getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return getInventory().getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int n)
    {
        return getInventory().decrStackSize(slot, n);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        return getInventory().getStackInSlotOnClosing(slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        getInventory().setInventorySlotContents(slot, stack);
    }

    @Override
    public String getInventoryName()
    {
        return getInventory().getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return getInventory().hasCustomInventoryName();
    }

    @Override
    public int getInventoryStackLimit()
    {
        return getInventory().getInventoryStackLimit();
    }

    @Override
    public void markDirty()
    {
        getInventory().markDirty();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return getInventory().isUseableByPlayer(player);
    }

    @Override
    public void openInventory()
    {
        getInventory().openInventory();
    }

    @Override
    public void closeInventory()
    {
        getInventory().openInventory();
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return getInventory().isItemValidForSlot(slot, stack);
    }
}
