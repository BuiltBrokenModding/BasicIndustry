package com.builtbroken.industry.content.machines.dynamic;

import com.builtbroken.industry.BasicIndustry;
import com.builtbroken.industry.content.cover.ItemMachineCover;
import com.builtbroken.industry.content.machines.dynamic.modules.cores.MachineCore;
import com.builtbroken.industry.content.machines.dynamic.modules.items.ItemMachineCore;
import com.builtbroken.industry.content.items.ItemParts;
import com.builtbroken.jlib.type.Pair;
import com.builtbroken.mc.api.tile.IGuiTile;
import com.builtbroken.mc.api.tile.IRemovable;
import com.builtbroken.mc.api.tile.IRotation;
import com.builtbroken.mc.api.tile.ITileModuleProvider;
import com.builtbroken.mc.api.tile.node.ITileModule;
import com.builtbroken.mc.core.content.tool.ItemSimpleCraftingTool;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.builtbroken.mc.lib.helper.BlockUtility;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.prefab.module.ModuleBuilder;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.TileEnt;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
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
public class TileDynamicMachine extends TileEnt implements ITileModuleProvider, IGuiTile, IRemovable.ISneakPickup, IPacketIDReceiver, ISidedInventory, IRotation, IRecipeContainer
{
    @SideOnly(Side.CLIENT)
    public static IIcon dynamicMachineTexture;

    /** The machine core */
    protected MachineCore machineCore;
    /** Decoration for each side */
    protected Pair<Block, Integer>[] machineSides;
    /** Inventory side connections, 0 = none, 1 = input, 2 = output, 3 = both */
    protected byte[] inventoryConnection = new byte[6];

    ForgeDirection facing;

    public TileDynamicMachine()
    {
        super("dynamicMachine", Material.iron);
        this.itemBlock = ItemBlockDynamicMachine.class;
        this.isOpaque = false;
        this.renderNormalBlock = false;
        this.renderTileEntity = true;
        this.renderType = ISBRMachine.ID;
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
            machineCore.setHost(this);
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
        if (machineSides == null || machineSides[side] == null)
        {
            ItemStack heldItem = player.getHeldItem();
            if (heldItem != null)
            {
                if (heldItem.getItem() instanceof ItemMachineCore)
                {
                    if (machineCore == null)
                    {
                        if (isServer())
                        {
                            final MachineCore core = ((ItemMachineCore) heldItem.getItem()).getModule(heldItem);
                            if (core != null)
                            {
                                machineCore = core;
                                machineCore.setHost(this);
                                machineCore.onJoinWorld();

                                if (!player.capabilities.isCreativeMode)
                                {
                                    player.inventory.decrStackSize(player.inventory.currentItem, 1);
                                    player.inventoryContainer.detectAndSendChanges();
                                }
                                player.addChatMessage(new ChatComponentText("Machine core added to frame."));
                                onMachineChanged(true);
                            }
                            else
                            {
                                player.addChatMessage(new ChatComponentText("Error: Core read incorrectly... this is a bug!!!"));
                            }
                        }
                        return true;
                    }
                }
                else if (heldItem.getItem() instanceof ItemMachineCover)
                {
                    return true;
                }
            }

            if (machineCore != null)
            {
                if (isServer())
                {
                    openGui(player, BasicIndustry.INSTANCE);
                }
                return true;
            }
            else
            {
                if (isServer())
                {
                    player.addChatMessage(new ChatComponentText("No core installed to interact with machine."));
                }
            }
        }
        return false;
    }

    public void onMachineChanged(boolean syncClient)
    {
        this.worldObj.markTileEntityChunkModified(this.xCoord, this.yCoord, this.zCoord, this);
        if (syncClient)
        {
            sendDescPacket();
        }
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
                player.addChatComponentMessage(new ChatComponentText("Side set to " + (inventoryConnection[side] == 0 ? "no connections" : inventoryConnection[side] == 1 ? "input only" : inventoryConnection[side] == 2 ? "output only" : "input and output")));
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
        if (nbt.hasKey("machineCore"))
        {
            readMachineNBT(nbt.getCompoundTag("machineCore"));
        }
        if (nbt.hasKey("invConnections"))
        {
            NBTTagCompound tag = nbt.getCompoundTag("invConnections");
            for (int i = 0; i < 6; i++)
            {
                inventoryConnection[i] = tag.getByte("" + i);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        NBTTagCompound tag = new NBTTagCompound();
        writeMachineNBT(tag);
        nbt.setTag("machineCore", tag);

        NBTTagCompound invConTag = new NBTTagCompound();
        for (int i = 0; i < 6; i++)
        {
            invConTag.setByte("" + i, inventoryConnection[i]);
        }
        nbt.setTag("invConnections", invConTag);
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
            machineCore.toStack().writeToNBT(nbt);
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
        NBTTagCompound save = nbt.getCompoundTag("tag");
        String saveID = save.getString(ModuleBuilder.SAVE_ID);
        if (machineCore != null && saveID.equals(machineCore.getSaveID()))
        {
            machineCore.load(save);
            machineCore.setHost(this);
        }
        else
        {
            ItemStack stack = ItemStack.loadItemStackFromNBT(nbt);
            if (stack != null)
            {
                machineCore = (MachineCore) MachineModuleBuilder.INSTANCE.build(stack);
                machineCore.setHost(this);
            }
        }
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        if (buf.readBoolean())
        {
            readMachineNBT(ByteBufUtils.readTag(buf));
        }
        else
        {
            machineCore = null;
        }
        this.worldObj.markBlockRangeForRenderUpdate(this.xCoord, this.yCoord, this.zCoord, this.xCoord, this.yCoord, this.zCoord);
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        buf.writeBoolean(machineCore != null);
        if (machineCore != null)
        {
            ByteBufUtils.writeTag(buf, writeMachineNBT(new NBTTagCompound()));
        }
        //TODO write machine sides
        //TODO write connections
        markRender();
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (!super.read(buf, id, player, type))
        {
            if (id == 2)
            {
                openGui(player, buf.readInt(), BasicIndustry.INSTANCE);
            }
            else if (id == 3)
            {
                if (machineCore != null)
                {
                    machineCore.read(buf, buf.readInt(), player, type);
                    return true;
                }
                return false;
            }
            return false;
        }
        return true;
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
            Pair<Block, Integer> pair = machineSides[side];
            if (pair != null)
            {
                return pair.left().getIcon(side, pair.right());
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
                //Input
                if (type == 1 || type == 3)
                {
                    //Machine core grinder input
                    slotCount += 1;
                    //TODO add access to power module
                    if (machineCore.getInputInventory() != null)
                    {
                        slotCount += machineCore.getInputInventory().getSizeInventory();
                    }
                }
                //output
                if ((type == 2 || type == 3) && machineCore.getOutputInventory() != null)
                {
                    slotCount += machineCore.getOutputInventory().getSizeInventory();
                }

                int[] slots = new int[slotCount];
                int i = 0;

                if (type == 1 || type == 3)
                {
                    i = 1;
                    slots[0] = MachineCore.MACHINE_TOOL_SLOT;
                    //TODO add better handling for machine core slots to allow more than 1
                }
                if (machineCore.getInputInventory() != null)
                {
                    if (type == 1 || type == 3)
                    {
                        for (int j = 0; j < machineCore.getInputInventory().getSizeInventory(); j++)
                        {
                            slots[i] = j + spacer;
                            i += 1;
                        }
                    }
                    spacer += machineCore.getInputInventory().getSizeInventory();
                }
                if ((type == 2 || type == 3) && machineCore.getOutputInventory() != null)
                {
                    for (int j = 0; j < machineCore.getOutputInventory().getSizeInventory(); j++)
                    {
                        slots[i] = j + spacer;
                        i += 1;
                    }
                }
                return slots;
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
                if (slot < machineCore.getInventory().getSizeInventory())
                {
                    return machineCore.getInventory().canInsertItem(slot, stack, side);
                }
                if (machineCore.getInputInventory() != null)
                {
                    int spacer = machineCore.getInventory().getSizeInventory();
                    if (slot >= spacer && slot < (machineCore.getInputInventory().getSizeInventory() + spacer))
                    {
                        return machineCore.getInputInventory().canStore(stack, slot, ForgeDirection.getOrientation(side));
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side)
    {
        if (machineCore != null && machineCore.getOutputInventory() != null && side >= 0 && side < 6)
        {
            byte type = inventoryConnection[side];
            if (type == 2 || type == 3)
            {
                int spacer = machineCore.getInventory().getSizeInventory();
                if (machineCore.getInputInventory() != null)
                {
                    spacer += machineCore.getInputInventory().getSizeInventory();
                }
                if (slot >= spacer && slot < (machineCore.getOutputInventory().getSizeInventory() + spacer))
                {
                    return machineCore.getInputInventory().canRemove(stack, slot, ForgeDirection.getOrientation(side));
                }
            }
        }
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        if (slot >= 0 && slot < getSizeInventory())
        {
            int spacer = machineCore.getInventory().getSizeInventory();
            if (slot >= 0 && slot < spacer)
            {
                return machineCore.getInventory().isItemValidForSlot(slot, stack);
            }
            if (machineCore.getInputInventory() != null)
            {
                if (slot < spacer + machineCore.getInputInventory().getSizeInventory())
                {
                    return machineCore.getInputInventory().isItemValidForSlot(slot, stack);
                }
                spacer += machineCore.getInputInventory().getSizeInventory();
            }
            if (machineCore.getOutputInventory() != null)
            {
                if (slot < spacer + machineCore.getOutputInventory().getSizeInventory())
                {
                    return machineCore.getOutputInventory().isItemValidForSlot(slot, stack);
                }
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
    public void onPlaced(EntityLivingBase entityLiving, ItemStack itemStack)
    {
        super.onPlaced(entityLiving, itemStack);
        setMeta(BlockUtility.determineOrientation(xi(), yi(), zi(), entityLiving));
    }

    @Override
    public ForgeDirection getDirection()
    {
        if (facing == null)
        {
            int meta = world().getBlockMetadata(xi(), yi(), zi());
            if (meta < ForgeDirection.NORTH.ordinal() || meta > ForgeDirection.EAST.ordinal())
            {
                setMeta(ForgeDirection.NORTH.ordinal());
                facing = ForgeDirection.NORTH;
            }
            else
            {
                facing = ForgeDirection.getOrientation(meta);
            }
        }
        return facing;
    }

    @Override
    public void genRecipes(List<IRecipe> recipes)
    {
        recipes.add(newShapedRecipe(BasicIndustry.blockDynamicMachine, "FDF", "F F", "FHF", 'F', ItemParts.Parts.MACHINE_FACE.toStack(), 'D', ItemSimpleCraftingTool.getDrill(), 'H', ItemSimpleCraftingTool.getHammer()));
    }
}

