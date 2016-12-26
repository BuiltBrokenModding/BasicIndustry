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

    /** The machine */
    protected MachineCore machineCore;
    protected Block[] machineSides;

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
            machineCore.load(nbt.getCompoundTag("core").getCompoundTag("tag"));
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
    public int[] getAccessibleSlotsFromSide(int p_94128_1_)
    {
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, int p_102007_3_)
    {
        return false;
    }

    @Override
    public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_, int p_102008_3_)
    {
        return false;
    }

    @Override
    public int getSizeInventory()
    {
        return 0;
    }

    @Override
    public ItemStack getStackInSlot(int p_70301_1_)
    {
        return null;
    }

    @Override
    public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_)
    {
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int p_70304_1_)
    {
        return null;
    }

    @Override
    public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_)
    {

    }

    @Override
    public String getInventoryName()
    {
        return null;
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return false;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 0;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer p_70300_1_)
    {
        return false;
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
    public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_)
    {
        return false;
    }
}

