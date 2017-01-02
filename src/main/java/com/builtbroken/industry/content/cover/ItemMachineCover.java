package com.builtbroken.industry.content.cover;

import com.builtbroken.industry.BasicIndustry;
import com.builtbroken.mc.prefab.items.ItemAbstract;
import com.builtbroken.mc.prefab.items.ItemStackWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

/**
 * Thin sheet of block used to cover machines or wires
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/30/2016.
 */
public class ItemMachineCover extends ItemAbstract
{
    @SideOnly(Side.CLIENT)
    private List<ItemStackWrapper> coverCache;

    public ItemMachineCover()
    {
        setUnlocalizedName(BasicIndustry.PREFIX + "blockCover");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
    {
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("data"))
        {
            NBTTagCompound tag = stack.getTagCompound().getCompoundTag("data");
            if (tag.hasKey("stack"))
            {
                ItemStack blockStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("stack"));
                if (blockStack != null)
                {
                    list.add("Block: " + blockStack.getUnlocalizedName());
                    return;
                }
            }
        }
        list.add("Corrupted NBT data!!");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item itemCover, CreativeTabs tab, List list)
    {
        if (coverCache == null)
        {
            coverCache = new ArrayList();
            for (Object entry : Block.blockRegistry)
            {
                final Block block = (Block) entry;
                if (block != null && block.getMaterial().isSolid() && !block.getMaterial().isReplaceable())
                {
                    try
                    {
                        if (block != null && isBlockValid(block))
                        {
                            List<ItemStack> stacks = new ArrayList();
                            Item item = Item.getItemFromBlock(block);

                            for (CreativeTabs ct : item.getCreativeTabs())
                            {
                                block.getSubBlocks(item, ct, stacks);
                            }
                            if (stacks != null && !stacks.isEmpty())
                            {
                                for (ItemStack stack : stacks)
                                {
                                    if (stack.getTagCompound() == null && !block.hasTileEntity(stack.getItemDamage()))
                                    {
                                        boolean npe = false;
                                        for (int i = 0; i < 6; i++)
                                        {
                                            if (block.getIcon(i, stack.getItemDamage()) == null)
                                            {
                                                npe = true;
                                                break;
                                            }
                                        }
                                        if (!npe)
                                        {
                                            coverCache.add(new ItemStackWrapper(stack));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        BasicIndustry.INSTANCE.logger().error("Error while building cover cache for " + block);
                    }
                }
            }
        }
        for (ItemStackWrapper stack : coverCache)
        {
            ItemStack coverStack = new ItemStack(itemCover);
            encodeBlockMetaPair(coverStack, stack.itemStack);
            list.add(coverStack);
        }
    }

    public static void encodeBlockMetaPair(ItemStack stack, ItemStack blockStack)
    {
        if (stack.getTagCompound() == null)
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("stack", blockStack.writeToNBT(new NBTTagCompound()));
        stack.getTagCompound().setTag("data", tag);
    }

    public static Block getBlock(ItemStack stack)
    {
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("data"))
        {
            NBTTagCompound tag = stack.getTagCompound().getCompoundTag("data");
            if (tag.hasKey("stack"))
            {
                ItemStack blockStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("stack"));
                if (blockStack != null)
                {
                    Block block = Block.getBlockFromItem(blockStack.getItem());
                    return block;
                }
            }
        }
        return null;
    }

    public static int getMeta(ItemStack stack)
    {
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("data"))
        {
            NBTTagCompound tag = stack.getTagCompound().getCompoundTag("data");
            if (tag.hasKey("stack"))
            {
                ItemStack blockStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("stack"));
                if (blockStack != null)
                {
                    return Math.min(blockStack.getItemDamage(), 15);
                }
            }
        }
        return 0;
    }

    private static boolean isBlockValid(Block block)
    {
        if (block.renderAsNormalBlock() && block.getRenderType() == 0)
        {
            if (block.getBlockBoundsMinX() != 0 || block.getBlockBoundsMinY() != 0 || block.getBlockBoundsMinZ() != 0)
            {
                return false;
            }
            if (block.getBlockBoundsMaxX() != 1 || block.getBlockBoundsMaxY() != 1 || block.getBlockBoundsMaxZ() != 1)
            {
                return false;
            }
            return true;
        }
        return false;
    }
}
