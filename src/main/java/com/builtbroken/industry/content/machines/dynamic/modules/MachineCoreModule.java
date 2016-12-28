package com.builtbroken.industry.content.machines.dynamic.modules;

import com.builtbroken.industry.content.machines.dynamic.cores.MachineCore;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/26/2016.
 */
public class MachineCoreModule extends MachineModule
{
    protected MachineCore machineCore;

    /**
     * Default constructor
     *
     * @param stack
     * @param name
     */
    public MachineCoreModule(ItemStack stack, String name)
    {
        super(stack, name);
    }

    public void setHost(MachineCore core)
    {
        this.machineCore = core;
        setHost(core.getHost());
    }
}
