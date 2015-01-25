package com.builtbroken.industry.content;

import com.builtbroken.industry.BasicIndustry;
import com.builtbroken.mc.core.network.packet.AbstractPacket;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.lib.transform.vector.Pos;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Iterator;

/** Machine that can process one( or more) items into an output recipe
 * Created by robert on 1/9/2015.
 */
public abstract class TileProcessor extends TileMachine
{
    /**
     * Is the machine currently processing
     */
    protected boolean isProcessing = false;

    /**
     * How many ticks towards processing time
     */
    protected int processing_ticks = 0;
    /**
     * How many ticks until processing is done
     */
    protected int max_processing_ticks = 0;

    public TileProcessor(Material material, int inventory_size)
    {
        super(material, inventory_size);
    }

    //==============================
    //======= Implements ===========
    //==============================

    /**
     * Gets the recipe for the current input slot(s)
     */
    protected abstract ItemStack getRecipe();

    //==============================
    //======= Update Logic =========
    //==============================

    @Override
    protected boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        if (isServer())
        {
            openGui(player, BasicIndustry.INSTANCE);
        }
        return true;
    }

    @Override
    public void update()
    {
        super.update();
        if(isServer())
        {
            this.isProcessing = this.canProcess();
            if (isProcessing)
            {
                ++this.processing_ticks;
                if (this.processing_ticks >= max_processing_ticks)
                {
                    this.processing_ticks = 0;
                    this.processRecipe();
                }
            }
            setEnabled(isWorking());
        }
    }

    /**
     * Can we process the recipe
     */
    protected boolean canProcess()
    {
        ItemStack itemstack = getRecipe();
        if (itemstack != null)
        {
            ItemStack output = this.getStackInSlot(1);

            if (output == null)
            {
                return true;
            }
            else if (output.isItemEqual(itemstack)) // check for output room
            {
                int newStackSize = output.stackSize + itemstack.stackSize; // check if we don't exceed max stack size
                return newStackSize <= getInventoryStackLimit() && newStackSize <= output.getMaxStackSize();
            }
        }
        return false;
    }

    /**
     * Processes the recipe and places it into the output slot
     */
    protected void processRecipe()
    {
        ItemStack output = this.getStackInSlot(1);
        ItemStack result = getRecipe();

        //Increase output stack size
        if (output == null)
        {
            this.setInventorySlotContents(1, result.copy());
        }
        else if (output.getItem() == result.getItem())
        {
            output.stackSize += result.stackSize;
        }

        //Decrease input stack size
        --this.getStackInSlot(0).stackSize;
        if (this.getStackInSlot(0).stackSize <= 0)
        {
            this.setInventorySlotContents(0, null);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        nbt.setInteger("processingTicks", getProcessorTicks());
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        processing_ticks = nbt.getInteger("processingTicks");
    }

    //==============================
    //======= Packet Code ==========
    //==============================

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (isClient() && !super.read(buf, id, player, type))
        {
            if (id == 12)
            {
                this.processing_ticks = buf.readInt();
                this.max_processing_ticks = buf.readInt();
                return true;
            }
        }
        return false;
    }

    //==============================
    //========= Gui Code ===========
    //==============================

    @Override
    public void doUpdateGuiUsers()
    {
        if (ticks % 3 == 0)
        {
            sendPacketToGuiUsers(new PacketTile(this, 12, processing_ticks, max_processing_ticks));
        }
    }

    @Override
    public void doCleanupCheck()
    {
        if (getPlayersUsing().size() > 0)
        {
            Iterator<EntityPlayer> it = getPlayersUsing().iterator();
            while (it.hasNext())
            {
                EntityPlayer player = it.next();
                if (!(player.inventoryContainer instanceof ContainerTileProcessor))
                {
                    it.remove();
                }
            }
        }
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerTileProcessor(this, player);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return new GuiTileProcessor(this, player);
    }

    //==============================
    //==== Getters $ Setters =======
    //==============================

    public int getProcessorTicks()
    {
        return processing_ticks;
    }

    public int getMaxProcessingTicks()
    {
        return max_processing_ticks;
    }
}
