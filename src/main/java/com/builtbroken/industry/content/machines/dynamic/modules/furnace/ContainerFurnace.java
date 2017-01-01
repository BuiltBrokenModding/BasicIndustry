package com.builtbroken.industry.content.machines.dynamic.modules.furnace;

import com.builtbroken.mc.prefab.gui.ContainerBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/1/2017.
 */
public class ContainerFurnace extends ContainerBase
{
    private TileFurnace tileFurnace;
    private int lastCookTime;
    private int lastBurnTime;
    private int lastItemBurnTime;

    public ContainerFurnace(EntityPlayer player, TileFurnace furnace)
    {
        super(player, furnace);
        this.tileFurnace = furnace;
        this.addSlotToContainer(new Slot(furnace, 0, 47, 17));
        this.addSlotToContainer(new Slot(furnace, 1, 65, 17));

        this.addSlotToContainer(new SlotFurnace(player, furnace, 2, 112, 35));
        this.addSlotToContainer(new SlotFurnace(player, furnace, 3, 132, 35));

        this.addSlotToContainer(new Slot(furnace, 4, 56, 53));

        addPlayerInventory(player);
    }

    @Override
    public void addCraftingToCrafters(ICrafting p_75132_1_)
    {
        super.addCraftingToCrafters(p_75132_1_);
        p_75132_1_.sendProgressBarUpdate(this, 0, this.tileFurnace.processingTicks);
        p_75132_1_.sendProgressBarUpdate(this, 1, this.tileFurnace.burnTime);
        p_75132_1_.sendProgressBarUpdate(this, 2, this.tileFurnace.burnTimeItem);
    }

   @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int i = 0; i < this.crafters.size(); ++i)
        {
            ICrafting icrafting = (ICrafting) this.crafters.get(i);

            if (this.lastCookTime != this.tileFurnace.processingTicks)
            {
                icrafting.sendProgressBarUpdate(this, 0, this.tileFurnace.processingTicks);
            }

            if (this.lastBurnTime != this.tileFurnace.burnTime)
            {
                icrafting.sendProgressBarUpdate(this, 1, this.tileFurnace.burnTime);
            }

            if (this.lastItemBurnTime != this.tileFurnace.burnTimeItem)
            {
                icrafting.sendProgressBarUpdate(this, 2, this.tileFurnace.burnTimeItem);
            }
        }

        this.lastCookTime = this.tileFurnace.processingTicks;
        this.lastBurnTime = this.tileFurnace.burnTime;
        this.lastItemBurnTime = this.tileFurnace.burnTimeItem;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value)
    {
        if (id == 0)
        {
            this.tileFurnace.processingTicks = value;
        }

        if (id == 1)
        {
            this.tileFurnace.burnTime = value;
        }

        if (id == 2)
        {
            this.tileFurnace.burnTimeItem = value;
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer p_75145_1_)
    {
        return this.tileFurnace.isUseableByPlayer(p_75145_1_);
    }
}
