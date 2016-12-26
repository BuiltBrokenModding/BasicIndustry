package com.builtbroken.industry.content.machines.dynamic;

import com.builtbroken.industry.content.machines.dynamic.modules.MachineModule;
import com.builtbroken.mc.prefab.module.ModuleBuilder;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/26/2016.
 */
public class MachineModuleBuilder<M extends MachineModule> extends ModuleBuilder<M>
{
    public static final MachineModuleBuilder INSTANCE = new MachineModuleBuilder();
}
