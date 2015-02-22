package com.builtbroken.industry.content.machines.prefab;

import com.builtbroken.mc.api.tile.IGuiTile;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Prefab for electric machines, will move to core when completed
 * Created by robert on 1/7/2015.
 */
public abstract class TileMachine extends TileModuleMachine implements IPacketIDReceiver, IGuiTile
{
    private boolean enabled = false;
    private boolean prev_enabled = false;
    private OnOffOption onOffOption = OnOffOption.AUTOMATIC;


    public TileMachine(Material material, int inventory_size)
    {
        super("", material);
        this.addInventoryModule(inventory_size);
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

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        onOffOption = OnOffOption.get(nbt.getInteger("onOffState"));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("onOffState", onOffOption.ordinal());
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
