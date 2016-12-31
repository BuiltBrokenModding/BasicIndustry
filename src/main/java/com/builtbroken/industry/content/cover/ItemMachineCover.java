package com.builtbroken.industry.content.cover;

import com.builtbroken.industry.BasicIndustry;
import com.builtbroken.mc.prefab.items.ItemAbstract;
import com.builtbroken.mc.prefab.items.ItemStackWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
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

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        if (coverCache == null)
        {
            coverCache = new ArrayList();
            for (Object o : Block.blockRegistry)
            {
                Block b = (Block) o;
                if (b != null && isBlockValid(b))
                {
                    List<ItemStack> stacks = new ArrayList();
                    for (CreativeTabs ct : item.getCreativeTabs())
                    {
                        b.getSubBlocks(item, ct, stacks);
                    }
                    if (stacks != null && !stacks.isEmpty())
                    {
                        for (ItemStack stack : stacks)
                        {
                            if (stack.getTagCompound() == null && !b.hasTileEntity(stack.getItemDamage()))
                            {
                                coverCache.add(new ItemStackWrapper(stack));
                            }
                        }
                    }
                }
            }
        }
        for (ItemStackWrapper stack : coverCache)
        {
            ItemStack coverStack = new ItemStack(item);
            encodeBlockMetaPair(coverStack, Block.getBlockFromItem(stack.itemStack.getItem()), stack.itemStack.getItemDamage());
            list.add(coverStack);
        }
    }

    public static void encodeBlockMetaPair(ItemStack stack, Block block, int meta)
    {
        if (stack.getTagCompound() == null)
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("block", Block.blockRegistry.getNameForObject(block));
        tag.setInteger("meta", meta);
        stack.getTagCompound().setTag("data", tag);
    }

    public static Block getBlock(ItemStack stack)
    {
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("data"))
        {
            String name = stack.getTagCompound().getCompoundTag("data").getString("block");
            if (name != null && !name.isEmpty())
            {
                return (Block) Block.blockRegistry.getObject(name);
            }
        }
        return null;
    }

    public static int getMeta(ItemStack stack)
    {
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("data"))
        {
            return stack.getTagCompound().getCompoundTag("data").getInteger("meta");
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
