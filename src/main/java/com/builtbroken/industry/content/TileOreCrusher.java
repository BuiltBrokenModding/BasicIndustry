package com.builtbroken.industry.content;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;

/**
 * Created by robert on 1/7/2015.
 */
public class TileOreCrusher extends TileMachine
{
    public TileOreCrusher()
    {
        super(Material.iron, 2);
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
