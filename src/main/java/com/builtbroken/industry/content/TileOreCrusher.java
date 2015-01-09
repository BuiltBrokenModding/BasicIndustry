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
public class TileOreCrusher extends TileProcessor
{
    public int spinUpTime;
    public TileOreCrusher()
    {
        super(Material.iron, 2);
        this.name = "BasicOreCrusher";
        this.max_processing_ticks = 400;
    }

    @Override
    protected ItemStack getRecipe()
    {
        return getStackInSlot(0) != null ? MachineRecipeType.ITEM_CRUSHER.getItemStackRecipe(0, 0, getStackInSlot(0)) : null;
    }

    @Override
    public void update()
    {
        super.update();
        if (isProcessing)
        {
            ++spinUpTime;
        }
        else if (spinUpTime > 0)
        {
            spinUpTime--;
        }
    }

    @Override
    protected boolean isWorking()
    {
        return spinUpTime > 0;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side)
    {
        return slot == 0 && MachineRecipeType.ITEM_CRUSHER.getItemStackRecipe(0, 0, stack) != null;
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
        return Colors.YELLOW.toInt();
    }


}
