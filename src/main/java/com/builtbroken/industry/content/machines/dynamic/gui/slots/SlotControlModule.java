package com.builtbroken.industry.content.machines.dynamic.gui.slots;

import com.builtbroken.industry.content.machines.dynamic.modules.controllers.ControlModule;
import com.builtbroken.mc.api.modules.IModuleItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/28/2016.
 */
public class SlotControlModule extends Slot
{
    public SlotControlModule(IInventory inventory, int index, int x, int y)
    {
        super(inventory, index, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        if (stack.getItem() instanceof IModuleItem)
        {
            return ((IModuleItem) stack.getItem()).getModule(stack) instanceof ControlModule;
        }
        return false;
    }

    @Override
    public int getSlotStackLimit()
    {
        return 1;
    }
}
