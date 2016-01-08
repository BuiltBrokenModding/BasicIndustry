package com.builtbroken.industry.content.machines.modular;

import com.builtbroken.industry.content.Content;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * Item block for {@link TileDynamicMachine} so NBT data can be saved when remove and loaded when placed.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/8/2016.
 */
public class ItemBlockDynamicMachine extends ItemBlock
{
    public ItemBlockDynamicMachine(Block block)
    {
        super(block);
    }

    public static ItemStack toItemStack(TileDynamicMachine machine)
    {
        ItemStack stack = new ItemStack(Content.tileDynamicMachine);

        return stack;
    }
}
