package com.builtbroken.industry.content;

import com.builtbroken.jlib.data.Colors;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
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
        if (this.canSmelt())
        {
            ++this.processing_ticks;

            if (this.processing_ticks >= max_processing_ticks)
            {
                this.processing_ticks = 0;
                this.smeltItem();
            }
        }
        else if(heatLevel > 0)
        {
            heatLevel--;
        }

        setEnabled(heatLevel > 0);
    }

    private boolean canSmelt()
    {
        ItemStack input = this.getStackInSlot(0);
        ItemStack output = this.getStackInSlot(1);

        if ( input == null || output != null && output.stackSize >= output.getItem().getItemStackLimit(output))
        {
            return false;
        }
        else
        {
            ItemStack itemstack = FurnaceRecipes.smelting().getSmeltingResult(this.getStackInSlot(1));
            if (itemstack == null) return false;
            if (this.getStackInSlot(1) == null) return true;
            if (!this.getStackInSlot(1).isItemEqual(itemstack)) return false;
            int result = getStackInSlot(1).stackSize + itemstack.stackSize;
            return result <= getInventoryStackLimit() && result <= this.getStackInSlot(1).getMaxStackSize();
        }
    }

    public void smeltItem()
    {
        if (this.canSmelt())
        {
            ItemStack itemstack = FurnaceRecipes.smelting().getSmeltingResult(getStackInSlot(1));

            if (this.getStackInSlot(1) == null)
            {
                this.setInventorySlotContents(1, itemstack.copy());
            }
            else if (this.getStackInSlot(1).getItem() == itemstack.getItem())
            {
                this.getStackInSlot(1).stackSize += itemstack.stackSize;
            }

            --this.getStackInSlot(0).stackSize;

            if (this.getStackInSlot(0).stackSize <= 0)
            {
                this.setInventorySlotContents(1, null);
            }
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
        if(isEnabled())
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