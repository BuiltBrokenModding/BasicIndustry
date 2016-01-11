package com.builtbroken.industry.content.machines.modular;

import com.builtbroken.industry.content.machines.modular.modules.MachineModule;
import com.builtbroken.mc.api.tile.ITileModuleProvider;
import com.builtbroken.mc.api.tile.node.ITileModule;
import com.builtbroken.mc.prefab.tile.TileEnt;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * A machine that can be modified, reconfigured, and changed by the user at any time. This allows the machine to be multi-rolled as a grinder, crusher, smelter, etc.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/8/2016.
 */
public class TileDynamicMachine extends TileEnt implements ITileModuleProvider
{
    protected MachineModule machineCore;

    public TileDynamicMachine()
    {
        super("dynamicMachine", Material.iron);
        this.itemBlock = ItemBlockDynamicMachine.class;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        readMachineNBT(nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        writeMachineNBT(nbt);
    }

    /**
     * Writes nbt data exclusive to the machine
     *
     * @param nbt - tag to write to
     * @return tag
     */
    public NBTTagCompound writeMachineNBT(NBTTagCompound nbt)
    {
        return nbt;
    }

    /**
     * Loads nbt data excluse to the machine
     *
     * @param nbt - tag to read
     */
    public void readMachineNBT(NBTTagCompound nbt)
    {

    }

    @Override
    public <N extends ITileModule> N getModule(Class<? extends N> nodeType, ForgeDirection from)
    {
        return null;
    }
}

