package com.builtbroken.industry.content.machines.modular.modules;

import com.builtbroken.industry.BasicIndustry;
import com.builtbroken.industry.content.machines.modular.TileDynamicMachine;
import com.builtbroken.mc.api.ISave;
import com.builtbroken.mc.api.tile.ITileModuleProvider;
import com.builtbroken.mc.api.tile.node.ITileModule;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Prefab for any machine module, eg Grinder core.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/8/2016.
 */
public class MachineModule implements ITileModule, ISave
{
    /** Host of the module */
    protected final TileDynamicMachine machine;
    /** Name of the module */
    protected final String moduleName;
    /** Translation key used for item and inventory */
    protected String unlocalizedName;

    /**
     * Default constructor
     *
     * @param machine - host of the machine
     */
    public MachineModule(final String name, final TileDynamicMachine machine)
    {
        this.machine = machine;
        this.moduleName = name;
        this.unlocalizedName = BasicIndustry.PREFIX + "machine.module." + name;
    }

    @Override
    public void onJoinWorld()
    {

    }

    @Override
    public void onParentChange()
    {

    }

    @Override
    public void onLeaveWorld()
    {

    }

    @Override
    public ITileModuleProvider getParent()
    {
        return machine;
    }

    @Override
    public void load(NBTTagCompound nbt)
    {

    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        return nbt;
    }
}
