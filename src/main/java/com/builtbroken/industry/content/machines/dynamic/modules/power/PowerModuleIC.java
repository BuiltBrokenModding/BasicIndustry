package com.builtbroken.industry.content.machines.dynamic.modules.power;

import com.builtbroken.mc.api.tile.node.IExternalInventory;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/26/2016.
 */
public class PowerModuleIC extends PowerModule
{
    /**
     * Default constructor
     *
     * @param stack
     */
    public PowerModuleIC(ItemStack stack)
    {
        super(stack, "ic");
    }

    @Override
    public void update()
    {

    }

    @Override
    public IExternalInventory newInventory()
    {
        return null;
    }
}
