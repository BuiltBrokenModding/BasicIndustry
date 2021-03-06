package com.builtbroken.industry.content.machines.dynamic.modules.inv;

import com.builtbroken.industry.content.machines.dynamic.modules.MachineCoreModule;
import com.builtbroken.mc.api.ISave;
import com.builtbroken.mc.api.tile.provider.IInventoryProvider;
import com.builtbroken.mc.api.tile.node.IExternalInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/28/2016.
 */
public abstract class InventoryModule<I extends IExternalInventory> extends MachineCoreModule implements IExternalInventory, IInventoryProvider
{
    protected I inventory;

    /**
     * Default constructor
     *
     * @param name
     */
    public InventoryModule(ItemStack stack, String name)
    {
        super(stack, "inv." + name);
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

    /**
     * Creates a new inventory if the current
     * inventory is invalid or null
     *
     * @return inventory instance
     */
    public abstract I newInventory();

    @Override
    public boolean canStore(ItemStack stack, ForgeDirection side)
    {
        return true;
    }

    @Override
    public boolean canRemove(ItemStack stack, ForgeDirection side)
    {
        return true;
    }

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
    public void onInventoryChanged(int slot, ItemStack prev, ItemStack item)
    {
        if (getHost() != null && !getHost().oldWorld().isRemote)
        {
            getHost().onMachineChanged(false);
        }
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

    @Override
    public void load(NBTTagCompound nbt)
    {
        if (nbt.hasKey("inventory") && getInventory() instanceof ISave)
        {
            ((ISave) getInventory()).load(nbt.getCompoundTag("inventory"));
        }
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        if (getInventory() instanceof ISave)
        {
            nbt.setTag("inventory", ((ISave) getInventory()).save(new NBTTagCompound()));
        }
        return nbt;
    }
}
