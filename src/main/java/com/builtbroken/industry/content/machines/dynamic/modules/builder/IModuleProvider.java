package com.builtbroken.industry.content.machines.dynamic.modules.builder;

import com.builtbroken.industry.content.machines.dynamic.modules.MachineModule;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/30/2016.
 */
public interface IModuleProvider<M extends MachineModule>
{
    /**
     * Called to build the module from an itemstack
     *
     * @param stack - item that is the module
     * @return new module, or null if not valid
     */
    M buildModule(ItemStack stack);

    /**
     * Class that represents the module
     *
     * @return
     */
    Class<M> getModuleClass();
}
