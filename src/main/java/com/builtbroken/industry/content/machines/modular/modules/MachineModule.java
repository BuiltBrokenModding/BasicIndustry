package com.builtbroken.industry.content.machines.modular.modules;

import com.builtbroken.industry.content.machines.modular.MachineModuleBuilder;
import com.builtbroken.industry.content.machines.modular.TileDynamicMachine;
import com.builtbroken.mc.api.ISave;
import com.builtbroken.mc.api.tile.ITileModuleProvider;
import com.builtbroken.mc.api.tile.node.ITileModule;
import com.builtbroken.mc.prefab.module.AbstractModule;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

/**
 * Prefab for any machine module, eg Grinder core.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/8/2016.
 */
public class MachineModule extends AbstractModule implements ITileModule, ISave
{
    /** Host of the module */
    protected final TileDynamicMachine machine;

    /**
     * Default constructor
     *
     * @param machine - host of the machine
     */
    public MachineModule(final ItemStack stack, final String name, final TileDynamicMachine machine)
    {
        super(stack, "module.machine." + name);
        this.machine = machine;
    }

    @Override
    public void onJoinWorld()
    {
        //TODO register any events
        //TODO connect to grids
    }

    @Override
    public void onParentChange()
    {
        //TODO update cache data
    }

    @Override
    public void onLeaveWorld()
    {
        //TODO pop connections to any grids
        //TODO remove event entries
    }

    public void update()
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

    /**
     * Gets all items contained in this module including
     * the module itself as an item. Mainly used to drop
     * the modules from a machine when broken.
     *
     * @param items - list of items
     */
    public void getContainedItems(final List<ItemStack> items)
    {
        if (item != null)
        {
            items.add(toStack());
        }
    }

    @Override
    public String getSaveID()
    {
        return MachineModuleBuilder.INSTANCE.getID(this);
    }

}
