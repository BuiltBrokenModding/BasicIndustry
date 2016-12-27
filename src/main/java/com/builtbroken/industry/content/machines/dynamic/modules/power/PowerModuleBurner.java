package com.builtbroken.industry.content.machines.dynamic.modules.power;

import com.builtbroken.mc.api.tile.node.IExternalInventory;
import com.builtbroken.mc.prefab.inventory.ExternalInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

/**
 * Simple burner power source similar to the {@link TileEntityFurnace}
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/26/2016.
 */
public class PowerModuleBurner extends PowerModule
{
    public static int POWER_PER_TICK = 100;
    public static int MAX_POWER = 1000;
    protected int burnTime = 0;

    /**
     * Default constructor
     *
     * @param stack
     */
    public PowerModuleBurner(ItemStack stack)
    {
        super(stack, "burner");
    }

    @Override
    public void update()
    {
        if (burnTime <= 0)
        {
            if (machineCore.isMachineOn() && power < MAX_POWER)
            {
                burnTime = TileEntityFurnace.getItemBurnTime(getInventory().getStackInSlot(0));
            }
        }
        else
        {
            burnTime--;
            if (power < MAX_POWER)
            {
                power += POWER_PER_TICK;
            }
        }
    }

    @Override
    public IExternalInventory newInventory()
    {
        return new ExternalInventory(this, 1);
    }
}
