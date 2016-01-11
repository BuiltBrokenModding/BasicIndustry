package com.builtbroken.industry.content.machines.modular.modules;

import com.builtbroken.industry.BasicIndustry;
import com.builtbroken.industry.content.machines.modular.TileDynamicMachine;
import com.builtbroken.mc.api.ISave;
import com.builtbroken.mc.api.tile.ITileModuleProvider;
import com.builtbroken.mc.api.tile.node.ITileModule;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

/**
 * Prefab for any machine module, eg Grinder core.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/8/2016.
 */
public class MachineModule implements ITileModule, ISave
{
    /** Host of the module */
    protected final TileDynamicMachine machine; //TODO replace with interface
    /** Name of the module */
    protected final String moduleName;
    /** Translation key used for item and inventory */
    protected String unlocalizedName;
    /** Module as an itemstack */
    protected ItemStack selfAsStack;

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
        if (selfAsStack != null)
        {
            items.add(selfAsStack.copy());
        }
    }

    /**
     * Returns the module as an item
     *
     * @return new ItemStack(module)
     */
    public ItemStack toItemStack()
    {
        return selfAsStack != null ? selfAsStack.copy() : null;
    }
}
