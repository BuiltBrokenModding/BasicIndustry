package com.builtbroken.industry.content.machines.modular.cores.gui;

import com.builtbroken.industry.content.machines.modular.TileDynamicMachine;
import com.builtbroken.industry.content.machines.modular.modules.inv.InventoryModule;
import com.builtbroken.mc.prefab.gui.ContainerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;

/**
 * Container for the dynamic machine
 * Created by robert on 1/8/2015.
 */
public class ContainerDynamicMachine extends ContainerBase
{
    protected TileDynamicMachine machine;

    public ContainerDynamicMachine(TileDynamicMachine machine, EntityPlayer player)
    {
        super(player, machine);
        this.machine = machine;
        if (machine.getMachineCore() != null)
        {
            InventoryModule inputInventory = machine.getMachineCore().getInputInventory();
            InventoryModule outputInventory = machine.getMachineCore().getOutputInventory();

            //Input inventory
            if (inputInventory != null)
            {
                int col = 0;
                int row = 0;
                for (int i = 0; i < inputInventory.getSizeInventory(); i++)
                {
                    this.addSlotToContainer(new Slot(inputInventory, i, 56 + col * 16, 35 + row * 16));
                    col++;
                    if (col >= 4)
                    {
                        col = 0;
                        row++;
                    }
                }
            }
            //Output inventory
            if (outputInventory != null)
            {
                int col = 0;
                int row = 0;
                for (int i = 0; i < outputInventory.getSizeInventory(); i++)
                {
                    this.addSlotToContainer(new SlotFurnace(player, outputInventory, i, 116 + col * 16, 35 + row * 16));
                    col++;
                    if (col >= 4)
                    {
                        col = 0;
                        row++;
                    }
                }
            }
        }
        addPlayerInventory(player);
    }
}
