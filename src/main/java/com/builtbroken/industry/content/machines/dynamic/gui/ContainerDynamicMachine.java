package com.builtbroken.industry.content.machines.dynamic.gui;

import com.builtbroken.industry.content.machines.dynamic.TileDynamicMachine;
import com.builtbroken.industry.content.machines.dynamic.cores.MachineCore;
import com.builtbroken.industry.content.machines.dynamic.gui.slots.SlotControlModule;
import com.builtbroken.industry.content.machines.dynamic.gui.slots.SlotInvModule;
import com.builtbroken.industry.content.machines.dynamic.gui.slots.SlotPowerModule;
import com.builtbroken.industry.content.machines.dynamic.modules.inv.InventoryModule;
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

    public ContainerDynamicMachine(TileDynamicMachine machine, EntityPlayer player, int id)
    {
        super(player, machine);
        this.machine = machine;
        if (machine.getMachineCore() != null)
        {
            if(id == 0)
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
            else if(id == 1)
            {
                this.addSlotToContainer(new SlotInvModule(machine.getMachineCore().getInventory(), MachineCore.INPUT_INV_SLOT, 116, 35));
                this.addSlotToContainer(new SlotInvModule(machine.getMachineCore().getInventory(), MachineCore.OUTPUT_INV_SLOT, 116 + 20, 35));

                this.addSlotToContainer(new SlotPowerModule(machine.getMachineCore().getInventory(), MachineCore.POWER_MOD_SLOT, 116 + 40, 35));

                this.addSlotToContainer(new SlotControlModule(machine.getMachineCore().getInventory(), MachineCore.CONTROL_MOD_SLOT, 116 + 60, 35));
            }
        }
        addPlayerInventory(player);
    }
}
