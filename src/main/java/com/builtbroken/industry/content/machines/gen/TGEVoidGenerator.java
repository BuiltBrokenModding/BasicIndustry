package com.builtbroken.industry.content.machines.gen;

import com.builtbroken.industry.content.machines.gen.prefabs.TGSolidFuel;
import com.builtbroken.mc.api.energy.IEnergyCapacitor;
import com.builtbroken.mc.prefab.recipe.ItemStackWrapper;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.HashMap;

/**
 * Generates power by destroying items and sending them into the void
 * <p/>
 * Lore: By using the head of an enderman you can generate power off it teleporting
 * items. The harder the item is to teleport the better the energy return.
 * Created by robert on 3/8/2015.
 */
public class TGEVoidGenerator extends TGSolidFuel implements IEnergyCapacitor
{
    protected static HashMap<ItemStackWrapper, Integer> fuelsToEnergyMap = new HashMap();

    protected int energy = 0;

    //TODO add animation to GUI showing the enderman's head teleporting the item pixel by pixel.
    //Pixels of the item will be used as the percent fuel left bar
    //Purple beams should come from the enderman's eyes

    //TODO consume enderman head very slowly forcing the player to reset the machine

    public TGEVoidGenerator(String name, Material material)
    {
        super(name, material);
        //TODO add power module
    }

    @Override
    public int getFuelFor(ItemStack stack)
    {
        if (stack != null)
        {
            if (fuelsToEnergyMap.containsKey(stack))
            {
                return fuelsToEnergyMap.get(stack);
            }
            return 100;
        }
        return 0;
    }

    @Override
    public void generate()
    {
        if (fuelTicksLeft > 0)
        {
            energy += 1;
        }
    }

    @Override
    public int getEnergyForSide(ForgeDirection from)
    {
        return energy;
    }

    @Override
    public void setEnergyForSide(ForgeDirection from, int energy)
    {
        this.energy = energy;
    }

    @Override
    public int getMaxEnergyForSide(ForgeDirection from)
    {
        return 10000;
    }
}
