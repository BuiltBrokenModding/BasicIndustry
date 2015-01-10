package com.builtbroken.industry.content;

import com.builtbroken.industry.BasicIndustry;
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

/**
 * Prefab for electric machines, will move to core when completed
 * Created by robert on 1/7/2015.
 */
public abstract class TileMachine extends Tile implements ISidedInventory, IPacketIDReceiver, IGuiTile
{
    /**
     * Size of the inventory
     */
    protected int inventory_size;
    /**
     * Slots that can be accessed all sides, defaults to all slots
     */
    protected int[] openSlots;

    //Vars that are accessed with setters
    private ItemStack[] containedItems;
    private boolean enabled = false;
    private boolean prev_enabled = false;
    private OnOffOption onOffOption = OnOffOption.AUTOMATIC;


    public TileMachine(Material material, int inventory_size)
    {
        super(material);
        this.inventory_size = inventory_size;
    }

    //==============================
    //======= Implements ===========
    //==============================

    /**
     * Is the machine working, separate check from isOn/isEnabled
     */
    protected abstract boolean isWorking();

    //==============================
    //======= Update Logic =========
    //==============================

    @Override
    public void update()
    {
        super.update();
        setEnabled(isWorking() && onOffOption != OnOffOption.ALWAYS_OFF);
    }

    //==============================
    //===== Getters & Setters ======
    //==============================

    @Override
    public Tile newTile()
    {
        try
        {
            return this.getClass().newInstance();
        } catch (Exception e)
        {
            throw new RuntimeException("Failed to make new tile for " + getClass().getName(), e);
        }
    }

    /**
     * Sets the machine to be enabled/on
     */
    public void setEnabled(boolean enabled)
    {
        if (enabled != prev_enabled)
        {
            this.enabled = prev_enabled;
            sendEnabledPacket();
        }
    }

    /**
     * Is the machine enabled/on
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    public void setOnSwitchPosition(OnOffOption onOffOption)
    {
        if (onOffOption != this.onOffOption)
        {
            this.onOffOption = onOffOption;
            if (isServer())
            {
                sendOnSwitchPacket();
            }
        }
    }

    public OnOffOption getOnSwitchPosition()
    {
        return onOffOption;
    }

    //==============================
    //======= Packet Code ==========
    //==============================

    public void sendEnabledPacket()
    {
        Engine.instance.packetHandler.sendToAllAround(new PacketTile(this, 1, isEnabled()), this);
    }

    public void sendOnSwitchPacket()
    {
        Engine.instance.packetHandler.sendToAllAround(new PacketTile(this, 2, getOnSwitchPosition().ordinal()), this);
    }

    @Override
    public PacketTile getDescPacket()
    {
        return new PacketTile(this, 0, isEnabled(), getOnSwitchPosition().ordinal());
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (isClient())
        {
            if (id == 0 || id == 1 || id == 2)
            {
                if (id == 0 || id == 1)
                    this.enabled = buf.readBoolean();
                if (id == 0 || id == 2)
                    this.setOnSwitchPosition(OnOffOption.get(buf.readInt()));
                return true;
            }
        }
        return false;
    }

    //==============================
    //======= Inventory Code =======
    //==============================

    //TODO move inventory code to its own prefab
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
    public boolean hasCustomInventoryName()
    {
        return true;
    }

    @Override
    public String getInventoryName()
    {
        return "container." + BasicIndustry.PREFIX + name;
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
    public boolean isItemValidForSlot(int i, ItemStack itemstack)
    {
        return i <= this.getSizeInventory();
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
        super.readFromNBT(nbt);
        this.clear();

        NBTTagList nbtList = nbt.getTagList("Items", 10);

        for (int i = 0; i < nbtList.tagCount(); ++i)
        {
            NBTTagCompound stackTag = nbtList.getCompoundTagAt(i);
            byte id = stackTag.getByte("Slot");

            if (id >= 0 && id < this.getSizeInventory())
            {
                this.setInventorySlotContents(id, ItemStack.loadItemStackFromNBT(stackTag));
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
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

    public static enum OnOffOption
    {
        /**
         * Machine turns on when it needs to work, and off when it doesn't
         */
        AUTOMATIC,
        /**
         * Machine will always be on
         */
        ALWAYS_ON,
        /**
         * Machine will always be off
         */
        ALWAYS_OFF;

        public static OnOffOption get(int i)
        {
            if (i >= 0 && i < values().length)
                return values()[i];
            return AUTOMATIC;
        }
    }
}
