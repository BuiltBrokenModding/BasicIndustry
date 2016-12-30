package com.builtbroken.industry.content.machines.dynamic.modules.builder;

import com.builtbroken.industry.content.machines.dynamic.modules.MachineModule;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/30/2016.
 */
public abstract class ModuleProvider<M extends MachineModule> implements IModuleProvider<M>
{
    private final Class<M> clazz;

    public ModuleProvider(Class<M> clazz)
    {
        this.clazz = clazz;
    }

    @Override
    public Class<M> getModuleClass()
    {
        return clazz;
    }
}
