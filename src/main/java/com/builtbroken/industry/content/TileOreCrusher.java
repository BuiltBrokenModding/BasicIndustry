package com.builtbroken.industry.content;

import com.builtbroken.mc.api.recipe.MachineRecipeType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;

/**
 * Created by robert on 1/7/2015.
 */
public class TileOreCrusher extends TileProcessor
{
    public TileOreCrusher()
    {
        super(Material.iron, 2);
    }

    @Override
    protected ItemStack getRecipe()
    {
        return getStackInSlot(0) != null ? MachineRecipeType.ITEM_CRUSHER.getItemStackRecipe(0, 0, getStackInSlot(0)) : null;
    }

    @Override
    protected boolean isWorking()
    {
        return false;
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


}
