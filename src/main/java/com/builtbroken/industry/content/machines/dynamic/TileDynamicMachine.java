package com.builtbroken.industry.content.machines.dynamic;

import com.builtbroken.industry.BasicIndustry;
import com.builtbroken.industry.content.machines.dynamic.cores.ItemMachineCore;
import com.builtbroken.industry.content.machines.dynamic.cores.MachineCore;
import com.builtbroken.mc.api.tile.IGuiTile;
import com.builtbroken.mc.api.tile.IRemovable;
import com.builtbroken.mc.api.tile.ITileModuleProvider;
import com.builtbroken.mc.api.tile.node.ITileModule;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.TileEnt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

/**
 * A machine that can be modified, reconfigured, and changed by the user at any time. This allows the machine to be multi-rolled as a grinder, crusher, smelter, etc.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/8/2016.
 */
public class TileDynamicMachine extends TileEnt implements ITileModuleProvider, IGuiTile, IRemovable.ISneakPickup, IPacketIDReceiver, ISidedInventory
{
    @SideOnly(Side.CLIENT)
    public static IIcon dynamicMachineTexture;

    /** The machine core */
    protected MachineCore machineCore;
    /** Decoration for each side */
    protected Block[] machineSides;
    /** Inventory side connections, 0 = none, 1 = input, 2 = output, 3 = both */
    protected byte[] inventoryConnection = new byte[6];

    public TileDynamicMachine()
    {
        super("dynamicMachine", Material.iron);
        this.itemBlock = ItemBlockDynamicMachine.class;
        this.isOpaque = false;
        this.renderTileEntity = true;
    }

    @Override
    public Tile newTile()
    {
        return new TileDynamicMachine();
    }

    @Override
    public void firstTick()
    {
        if (machineCore != null)
        {
            machineCore.onJoinWorld();
        }
    }

    @Override
    public void update()
    {
        super.update();
        if (machineCore != null)
        {
            machineCore.update();
        }
    }

    @Override
    protected boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        ItemStack heldItem = player.getHeldItem();
        if (heldItem != null)
        {
            if (machineCore == null && heldItem.getItem() instanceof ItemMachineCore)
            {
                if (isServer())
                {
                    final MachineCore core = ((ItemMachineCore) heldItem.getItem()).getModule(heldItem);
                    if (core != null)
                    {
                        machineCore = core;
                        machineCore.onJoinWorld();
                        if (!player.capabilities.isCreativeMode)
                        {
                            player.inventory.decrStackSize(player.inventory.currentItem, 1);
                            player.inventoryContainer.detectAndSendChanges();
                        }
                        player.addChatMessage(new ChatComponentText("Machine core added to frame."));
                        sendDescPacket();
                    }
                    else
                    {
                        player.addChatMessage(new ChatComponentText("Error: Core read incorrectly... this is a bug!!!"));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public MachineCore getMachineCore()
    {
        return machineCore;
    }

    @Override
    protected boolean onPlayerRightClickWrench(EntityPlayer player, int side, Pos hit)
    {
        if (player.isSneaking())
        {
            //Cycle connection type for side
            if (isServer())
            {
                byte type = inventoryConnection[side];
                type++;
                if (type > 3)
                {
                    type = 0;
                }
                inventoryConnection[side] = type;
                sendDescPacket();
            }
            return true;
        }
        //TODO make rotatable
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("coreItemStack"))
        {
            readMachineNBT(nbt.getCompoundTag("coreItemStack"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        writeMachineNBT(nbt);
    }

    /**
     * Writes nbt data exclusive to the machine
     *
     * @param nbt - tag to write to
     * @return tag
     */
    public NBTTagCompound writeMachineNBT(NBTTagCompound nbt)
    {
        if (machineCore != null)
        {
            nbt.setTag("coreItemStack", machineCore.toStack().writeToNBT(new NBTTagCompound()));
        }
        return nbt;
    }

    /**
     * Loads nbt data excluse to the machine
     *
     * @param nbt - tag to read
     */
    public void readMachineNBT(NBTTagCompound nbt)
    {
        if (machineCore != null)
        {
            machineCore.load(nbt.getCompoundTag("coreItemStack").getCompoundTag("tag"));
        }
        else
        {
            ItemStack stack = ItemStack.loadItemStackFromNBT(nbt);
            if (stack != null)
            {
                String id = stack.getTagCompound().getString(MachineModuleBuilder.SAVE_ID);
                if (machineCore == null || !machineCore.getSaveID().equals(id))
                {
                    machineCore = (MachineCore) MachineModuleBuilder.INSTANCE.build(stack);
                }
                else
                {
                    readMachineNBT(nbt);
                }
            }
        }
    }

    @Override
    public <N extends ITileModule> N getModule(Class<? extends N> nodeType, ForgeDirection from)
    {
        if (machineCore != null && machineCore.getClass().isAssignableFrom(nodeType))
        {
            return (N) machineCore;
        }
        return null;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        if (machineCore != null)
        {
            return machineCore.getServerGuiElement(ID, player);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        if (machineCore != null)
        {
            return machineCore.getClientGuiElement(ID, player);
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        dynamicMachineTexture = iconRegister.registerIcon(BasicIndustry.PREFIX + "casing_iron");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side)
    {
        if (machineSides != null)
        {
            Block block = machineSides[side];
            if (block != null)
            {
                return block.getIcon(side, 0); //TODO add meta support
            }
        }
        return dynamicMachineTexture;
    }

    @Override
    public List<ItemStack> getRemovedItems(EntityPlayer entity)
    {
        List<ItemStack> list = new ArrayList();
        list.add(toItemStack());
        return list;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        //TODO cache if called often
        if (machineCore != null && side >= 0 && side < 6)
        {
            byte type = inventoryConnection[side];
            if (type != 0)
            {
                int slotCount = 0;
                int spacer = machineCore.getInventory().getSizeInventory();
                if ((type == 1 || type == 3) && machineCore.getInputInventory() != null)
                {
                    slotCount += machineCore.getInputInventory().getSizeInventory();
                }
                if ((type == 2 || type == 3) && machineCore.getOutputInventory() != null)
                {
                    slotCount += machineCore.getOutputInventory().getSizeInventory();
                }

                int[] slots = new int[slotCount];
                int i = 0;
                for (; i < spacer; i++)
                {
                    slots[i] = i;
                }
                if (machineCore.getInputInventory() != null)
                {
                    if (type == 1 || type == 3)
                    {
                        for (int j = 0; j < machineCore.getInputInventory().getSizeInventory(); j++)
                        {
                            slots[i++] = j + spacer;
                        }
                    }
                    spacer += machineCore.getInputInventory().getSizeInventory();
                }
                if ((type == 2 || type == 3) && machineCore.getOutputInventory() != null)
                {
                    for (int j = 0; j < machineCore.getOutputInventory().getSizeInventory(); j++)
                    {
                        slots[i++] = j + spacer;
                    }
                }
            }
        }
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side)
    {
        if (machineCore != null && side >= 0 && side < 6)
        {
            byte type = inventoryConnection[side];
            if (type == 1 || type == 3)
            {
                return slot >= machineCore.getInventory().getSizeInventory() && slot < (machineCore.getInputInventory().getSizeInventory() + machineCore.getInventory().getSizeInventory());
            }
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side)
    {
        if (machineCore != null && side >= 0 && side < 6)
        {
            byte type = inventoryConnection[side];
            if (type == 2 || type == 3)
            {
                int spacer = machineCore.getInventory().getSizeInventory();
                if (machineCore.getInputInventory() != null)
                {
                    spacer += machineCore.getInputInventory().getSizeInventory();
                }
                return slot >= spacer && slot < (machineCore.getOutputInventory().getSizeInventory() + spacer);
            }
        }
        return false;
    }

    @Override
    public int getSizeInventory()
    {
        if (machineCore != null)
        {
            int slots = machineCore.getInventory().getSizeInventory();
            if (machineCore.getInputInventory() != null)
            {
                slots += machineCore.getInputInventory().getSizeInventory();
            }
            if (machineCore.getOutputInventory() != null)
            {
                slots += machineCore.getOutputInventory().getSizeInventory();
            }
            return slots;
        }
        return 0;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        if (machineCore != null && slot >= 0)
        {
            int spacer = machineCore.getInventory().getSizeInventory();
            if (slot >= 0 && slot < spacer)
            {
                return machineCore.getInventory().getStackInSlot(slot);
            }
            if (machineCore.getInputInventory() != null)
            {
                if (slot < spacer + machineCore.getInputInventory().getSizeInventory())
                {
                    return machineCore.getInputInventory().getStackInSlot(slot - spacer);
                }
                spacer += machineCore.getInputInventory().getSizeInventory();
            }
            if (machineCore.getOutputInventory() != null)
            {
                if (slot < spacer + machineCore.getOutputInventory().getSizeInventory())
                {
                    return machineCore.getOutputInventory().getStackInSlot(slot - spacer);
                }
            }
        }
        return null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int n)
    {
        if (this.getStackInSlot(slot) != null)
        {
            ItemStack stack;

            if (this.getStackInSlot(slot).stackSize <= n)
            {
                stack = this.getStackInSlot(slot);
                setInventorySlotContents(slot, null);
                markDirty();
                return stack;
            }
            else
            {
                stack = this.getStackInSlot(slot).splitStack(n);
                if (this.getStackInSlot(slot).stackSize == 0)
                {
                    setInventorySlotContents(slot, null);
                }
                markDirty();
                return stack;
            }
        }
        else
        {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        if (this.getStackInSlot(slot) != null)
        {
            ItemStack var2 = this.getStackInSlot(slot);
            setInventorySlotContents(slot, null);
            return var2;
        }
        else
        {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        if (machineCore != null && slot >= 0)
        {
            int spacer = machineCore.getInventory().getSizeInventory();
            if (slot >= 0 && slot < spacer)
            {
                machineCore.getInventory().setInventorySlotContents(slot, stack);
            }
            if (machineCore.getInputInventory() != null)
            {
                if (slot < spacer + machineCore.getInputInventory().getSizeInventory())
                {
                    machineCore.getInputInventory().setInventorySlotContents(slot - spacer, stack);
                }
                spacer += machineCore.getInputInventory().getSizeInventory();
            }
            if (machineCore.getOutputInventory() != null)
            {
                if (slot < spacer + machineCore.getOutputInventory().getSizeInventory())
                {
                    machineCore.getOutputInventory().setInventorySlotContents(slot - spacer, stack);
                }
            }
        }
    }

    @Override
    public String getInventoryName()
    {
        return "tile.machine.dynamic";
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return true;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return player.getDistance(xi() + 0.5, yi() + 0.5, zi() + 0.5) < 20;
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
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return slot >= this.getSizeInventory() && slot < getSizeInventory();
    }
}

