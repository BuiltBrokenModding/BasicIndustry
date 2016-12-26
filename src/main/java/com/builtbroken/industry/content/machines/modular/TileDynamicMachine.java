package com.builtbroken.industry.content.machines.modular;

import com.builtbroken.industry.BasicIndustry;
import com.builtbroken.industry.content.machines.modular.cores.ItemMachineCore;
import com.builtbroken.industry.content.machines.modular.cores.MachineCore;
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
public class TileDynamicMachine extends TileEnt implements ITileModuleProvider, IGuiTile, IRemovable.ISneakPickup, IPacketIDReceiver
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
                    MachineCore core = ((ItemMachineCore) heldItem.getItem()).getModule(heldItem);
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
}

