package com.builtbroken.industry.content.machines.gen.prefabs;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by robert on 3/8/2015.
 */
public abstract class TGSolidFuel extends TileGenerator
{
    public TGSolidFuel(String name, Material material)
    {
        super(name, material);
    }

    @Override
    public int consumeFuel(boolean simulate)
    {
        if(isFuel(getStackInSlot(0)))
        {
            if(!simulate)
            {
                getStackInSlot(0).stackSize--;
                if(getStackInSlot(0).stackSize <= 0)
                    setInventorySlotContents(0, null);
            }
            return getFuelFor(getStackInSlot(0));
        }
        return 0;
    }

    public boolean isFuel(ItemStack stack)
    {
        return stack != null && getFuelFor(stack) > 0;
    }

    public abstract int getFuelFor(ItemStack stack);

    @Override
    public boolean canStore(ItemStack stack, int slot, ForgeDirection side)
    {
        return isFuel(stack);
    }
}
