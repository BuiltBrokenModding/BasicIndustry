package com.builtbroken.industry.content.machines.gen.prefabs;

import com.builtbroken.mc.api.ISave;
import com.builtbroken.mc.api.tile.node.ITileModule;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;

/** Basic generator code, can be used for about anything. As the code is not
 * set to just generate power. If someone wants it can be used to smelt items.
 * All you will need to do is use the generate method to tick the item smelting.
 * Created by robert on 3/8/2015.
 */
public abstract class TileGenerator extends TileModuleMachine
{
    //Machine variables
    public long fuelTicksLeft = 0L;
    public long heatUpTicks = 0L;

    //Machine settings
    protected long maxHeatUpTicks = 100L;
    protected boolean canGenerateDuringWarmUp = false;

    public TileGenerator(String name, Material material)
    {
        super(name, material);
    }

    @Override
    public void update()
    {
        super.update();

        //Update fuel ticks
        if (fuelTicksLeft <= 1)
            fuelTicksLeft += consumeFuel(true);

        if (fuelTicksLeft > 0)
        {
            //Heat up to max burn rate
            if (heatUpTicks < maxHeatUpTicks)
                heatUpTicks++;

            //If warmed up generate output
            if (canGenerateDuringWarmUp || heatUpTicks >= maxHeatUpTicks)
                generate();
        }
        else if (heatUpTicks > 0)
        {
            //Decrease if we are not burning fuel
            heatUpTicks--;
        }

    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        heatUpTicks = nbt.getLong("heatUpTicks");
        fuelTicksLeft = nbt.getLong("fuelLeftTicks");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setLong("heatUpTicks", heatUpTicks);
        nbt.setLong("fuelLeftTicks", fuelTicksLeft);
    }

    /**
     * Called to consume fuel to keep generating power
     *
     * @return how many ticks the fuel will last
     */
    public abstract int consumeFuel(boolean simulate);

    /**
     * Called each tick to add to the generation buffer. Abstracted
     * to allow for anything from power, to steam to be generated
     */
    public abstract void generate();

}
