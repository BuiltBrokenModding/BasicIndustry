package com.builtbroken.industry.content;

import com.builtbroken.mc.prefab.gui.ContainerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;

/**
 * Created by robert on 1/8/2015.
 */
public class ContainerTileProcessor extends ContainerBase
{
    protected TileProcessor machine;

    public ContainerTileProcessor(TileProcessor machine, EntityPlayer player)
    {
        super(player, machine);
        this.machine = machine;
        this.addSlotToContainer(new Slot(machine, 0, 56, 35));
        this.addSlotToContainer(new SlotFurnace(player, machine, 1, 116, 35));
        addPlayerInventory(player);
    }
}
