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
    //Main GUI buttons
    public static final int CRAFTING_GUI_ID = 0;
    public static final int MODULE_GUI_ID = 1;
    public static final int HELP_GUI_ID = 2;

    //Secondary buttons
    public static final int RECIPES_GUI_ID = 3;
    public static final int SETTINGS_GUI_ID = 4;

    protected TileDynamicMachine machine;

    public ContainerDynamicMachine(TileDynamicMachine machine, EntityPlayer player, int id)
    {
        super(player, machine);
        this.machine = machine;
        if (machine.getMachineCore() != null)
        {
            if(id == CRAFTING_GUI_ID)
            {

                this.addSlotToContainer(new SlotFurnace(player, machine.getMachineCore().getInventory(), 0, 80, 35));


                InventoryModule inputInventory = machine.getMachineCore().getInputInventory();
                InventoryModule outputInventory = machine.getMachineCore().getOutputInventory();

                //Input inventory
                if (inputInventory != null)
                {
                    int col = 0;
                    int row = 0;
                    for (int i = 0; i < inputInventory.getSizeInventory(); i++)
                    {
                        this.addSlotToContainer(new Slot(inputInventory, i, 10 + col * 18, 15 + row * 18));
                        col++;
                        if (col >= 3)
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
                        this.addSlotToContainer(new SlotFurnace(player, outputInventory, i, 114 + col * 18, 15 + row * 18));
                        col++;
                        if (col >= 3)
                        {
                            col = 0;
                            row++;
                        }
                    }
                }
            }
            else if(id == MODULE_GUI_ID)
            {
                int x = 30;
                this.addSlotToContainer(new SlotInvModule(machine.getMachineCore().getInventory(), MachineCore.INPUT_INV_SLOT, x, 20));
                this.addSlotToContainer(new SlotInvModule(machine.getMachineCore().getInventory(), MachineCore.OUTPUT_INV_SLOT, x + 100, 20));

                this.addSlotToContainer(new SlotPowerModule(machine.getMachineCore().getInventory(), MachineCore.POWER_MOD_SLOT, x + 30, 42));

                this.addSlotToContainer(new SlotControlModule(machine.getMachineCore().getInventory(), MachineCore.CONTROL_MOD_SLOT, x + 70, 42));
            }
        }
        addPlayerInventory(player);
    }
}
