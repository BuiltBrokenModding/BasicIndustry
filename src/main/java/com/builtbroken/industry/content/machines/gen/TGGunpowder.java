package com.builtbroken.industry.content.machines.gen;

import com.builtbroken.industry.content.machines.gen.prefabs.TGSolidFuel;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * Generates power from gunpowder, has a slight chance to blow up each time it consumed gun powder.
 * Uses the gases generated from burning the powder to generate power. Comes in several odd versions
 * Single Piston(Fills up with powder and then ignites), Car Engine(Works like a car engine), Turbine(Burns the fuel and fuels the gas threw a turbine)
 * Each version burns fuel faster than the last version. Each also has a warm up period making it only effective if constantly
 * burning fuel. For example the turbine version should consume a 10 gunpowder a tick but produce a lot of power. As turbine has a
 * very low chance to explode.
 * Created by robert on 3/8/2015.
 */
public class TGGunpowder extends TGSolidFuel
{
    public TGGunpowder()
    {
        super("gunpowdergen", Material.iron);
    }

    @Override
    public void generate()
    {

    }

    @Override
    public int getFuelFor(ItemStack stack)
    {
        return stack != null && stack.getItem() == Items.gunpowder ? 20 : 0;
    }
}
