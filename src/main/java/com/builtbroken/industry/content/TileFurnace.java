package com.builtbroken.industry.content;

import com.builtbroken.jlib.data.Colors;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.IIcon;

/**
 * Created by robert on 1/7/2015.
 */
public class TileFurnace extends TileMachine
{
    public int heatLevel = 0;

    public TileFurnace()
    {
        super(Material.iron, 2);
        this.name = "BasicFurnace";
        this.max_processing_ticks = 200;
    }

    @Override
    public void update()
    {
        super.update();
        if (this.canProcess())
        {
            ++this.processing_ticks;

            if (this.processing_ticks >= max_processing_ticks)
            {
                this.processing_ticks = 0;
                this.processRecipe();
            }
        }
        else if (heatLevel > 0)
        {
            heatLevel--;
        }

        setEnabled(heatLevel > 0);
    }

    /** Checks if we can smelt the item */
    protected boolean canProcess()
    {
        ItemStack input = this.getStackInSlot(0);
        if(input != null) // check for intput
        {
            ItemStack output = this.getStackInSlot(1);

            if (output == null || output.stackSize <= output.getItem().getItemStackLimit(output)) // check for output room
            {
                ItemStack itemstack = FurnaceRecipes.smelting().getSmeltingResult(input);
                if (itemstack != null) // check output can contain recipe
                {
                    if(output == null)
                    {
                        return true;
                    }
                    else if(output.isItemEqual(itemstack))
                    {
                        int newStackSize = output.stackSize + itemstack.stackSize; // check if we don't exceed max stack size
                        return newStackSize <= getInventoryStackLimit() && newStackSize <= output.getMaxStackSize();
                    }
                }
            }
        }
        return false;
    }

    /** Processes the recipe */
    protected void processRecipe()
    {
        ItemStack output = this.getStackInSlot(1);
        ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(getStackInSlot(0));

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
    public boolean canInsertItem(int slot, ItemStack stack, int side)
    {
        return slot == 0;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side)
    {
        return slot == 1;
    }


    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {

    }

    @Override
    public IIcon getIcon(int side)
    {
        if (isEnabled())
        {
            return Blocks.lit_furnace.getIcon(side, getMetadata());
        }
        return Blocks.furnace.getIcon(side, getMetadata());
    }

    @SideOnly(Side.CLIENT)
    public int getColorMultiplier()
    {
        return Colors.DARK_AQUA.toInt();
    }
}
