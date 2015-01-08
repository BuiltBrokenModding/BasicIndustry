package com.builtbroken.industry.content;

import com.builtbroken.mc.api.tile.IGuiTile;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.Tile;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.Iterator;

/** Prefab for electric machines, will move to core when completed
 * Created by robert on 1/7/2015.
 */
public abstract class TileMachine extends Tile implements ISidedInventory, IPacketIDReceiver, IGuiTile
{
    /**
     * Default slot max count
     */
    protected int inventory_size;
    /**
     * Access able slots side all
     */
    protected int[] openSlots;
    /**
     * Items contained in this inv
     */
    protected ItemStack[] containedItems;

    private boolean enabled = false;
    private boolean prev_enabled = false;

    protected int processing_ticks = 0;
    protected int max_processing_ticks = 0;

    public TileMachine(Material material, int inventory_size)
    {
        super(material);
        this.inventory_size = inventory_size;
    }

    @Override
    public Tile newTile()
    {
        try
        {
            return this.getClass().newInstance();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to make new tile for " + getClass().getName(), e);
        }
    }

    public void setEnabled(boolean enabled)
    {
        if(enabled != prev_enabled)
        {
            this.enabled = prev_enabled;
            sendEnabledPacket();
        }
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public int getProcessorTicks()
    {
        return processing_ticks;
    }

    public int getMaxProcessingTicks()
    {
        return max_processing_ticks;
    }

    public void sendEnabledPacket()
    {
        Engine.instance.packetHandler.sendToAllAround(new PacketTile(this, 0, enabled), this);
    }

    @Override
    public void doUpdateGuiUsers()
    {
        if(ticks % 3 == 0)
        {
            sendPacketToGuiUsers(new PacketTile(this, 1, enabled, processing_ticks, max_processing_ticks));
        }
    }

    @Override
    public void doCleanupCheck()
    {
        if(getPlayersUsing().size() > 0)
        {
            Iterator<EntityPlayer> it = getPlayersUsing().iterator();
            while (it.hasNext())
            {
                EntityPlayer player = it.next();
                if (!(player.inventoryContainer instanceof ContainerMachine))
                {
                    it.remove();
                }
            }
        }
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if(isClient())
        {
            if (id == 0)
            {
                this.enabled = buf.readBoolean();
                return true;
            }
            else if(id == 1)
            {
                this.enabled = buf.readBoolean();
                this.processing_ticks = buf.readInt();
                this.max_processing_ticks = buf.readInt();
                return true;
            }
        }
       return false;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerMachine(this, player);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return new GuiMachine(this);
    }

    @Override
    public int getSizeInventory()
    {
        return inventory_size;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        if (slot < getContainedItems().length)
        {
            return this.getContainedItems()[slot];
        }
        return null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int ammount)
    {
        if (this.getContainedItems()[slot] != null)
        {
            ItemStack var3;

            if (this.getContainedItems()[slot].stackSize <= ammount)
            {
                var3 = this.getContainedItems()[slot];
                getContainedItems()[slot] = null;
                markDirty();
                return var3;
            }
            else
            {
                var3 = this.getContainedItems()[slot].splitStack(ammount);

                if (this.getContainedItems()[slot].stackSize == 0)
                {
                    this.getContainedItems()[slot] = null;
                }

                markDirty();
                return var3;
            }
        }
        else
        {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if (this.getContainedItems()[par1] != null)
        {
            ItemStack var2 = this.getContainedItems()[par1];
            this.getContainedItems()[par1] = null;
            return var2;
        }
        else
        {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack insertStack)
    {
        ItemStack pre_stack = this.getContainedItems()[slot] != null ? this.getContainedItems()[slot].copy() : null;
        this.getContainedItems()[slot] = insertStack;

        if (insertStack != null && insertStack.stackSize > this.getInventoryStackLimit())
        {
            insertStack.stackSize = this.getInventoryStackLimit();
        }
        if (!InventoryUtility.stacksMatchExact(pre_stack, getContainedItems()[slot]))
        {
            markDirty();
        }
    }

    @Override
    public String getInventoryName()
    {
        return "container.chest";
    }

    @Override
    public void openInventory()
    {

    }

    @Override
    public void closeInventory()
    {

    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack)
    {
        return i >= this.getSizeInventory();
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int var1)
    {
        if (openSlots == null || openSlots.length != this.getSizeInventory())
        {
            this.openSlots = new int[this.getSizeInventory()];
            for (int i = 0; i < this.openSlots.length; i++)
            {
                openSlots[i] = i;
            }
        }
        return this.openSlots;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return true;
    }

    public ItemStack[] getContainedItems()
    {
        if (this.containedItems == null)
        {
            this.containedItems = new ItemStack[this.getSizeInventory()];
        }
        return this.containedItems;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        this.clear();

        NBTTagList nbtList = nbt.getTagList("Items", 0);

        for (int i = 0; i < nbtList.tagCount(); ++i)
        {
            NBTTagCompound stackTag = nbtList.getCompoundTagAt(i);
            byte id = stackTag.getByte("Slot");

            if (id >= 0 && id < this.getSizeInventory())
            {
                this.setInventorySlotContents(id, ItemStack.loadItemStackFromNBT(stackTag));
            }
        }

        nbt.setTag("Items", nbtList);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        NBTTagList nbtList = new NBTTagList();

        for (int i = 0; i < this.getSizeInventory(); ++i)
        {
            if (this.getStackInSlot(i) != null)
            {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte) i);
                this.getStackInSlot(i).writeToNBT(var4);
                nbtList.appendTag(var4);
            }
        }

        nbt.setTag("Items", nbtList);
    }

    public void clear()
    {
        this.containedItems = null;
        this.getContainedItems();
    }
}
