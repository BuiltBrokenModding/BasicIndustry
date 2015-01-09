package com.builtbroken.industry.content;

import com.builtbroken.jlib.data.Colors;
import com.builtbroken.mc.api.recipe.MachineRecipeType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

/**
 * Created by robert on 1/7/2015.
 */
public class TileFurnace extends TileProcessor
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
        if (isProcessing)
        {
            ++heatLevel;
        }
        else if (heatLevel > 0)
        {
            heatLevel--;
        }
    }

    public boolean isWorking()
    {
        return heatLevel > 0;
    }

    @Override
    protected ItemStack getRecipe()
    {
        //FurnaceRecipes.smelting().getSmeltingResult(this.getStackInSlot(0))
        return getStackInSlot(0) != null ? MachineRecipeType.ITEM_SMELTER.getItemStackRecipe(0, 0, getStackInSlot(0)) : null;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side)
    {
        return slot == 0 && MachineRecipeType.ITEM_SMELTER.getItemStackRecipe(0, 0, stack) != null;
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
