package com.builtbroken.industry.content.machines.dynamic.modules.inv;

import com.builtbroken.mc.prefab.inventory.ExternalInventory;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/26/2016.
 */
public abstract class InventoryModuleBasic extends InventoryModule<ExternalInventory>
{
    /** Size of the inventory. */
    protected final int inventorySize;

    /**
     * Default constructor
     *
     * @param stack
     */
    public InventoryModuleBasic(ItemStack stack, String name, int size)
    {
        super(stack, name);
        this.inventorySize = size;
    }

    @Override
    public ExternalInventory newInventory()
    {
        return new ExternalInventory(this, inventorySize);
    }
}
