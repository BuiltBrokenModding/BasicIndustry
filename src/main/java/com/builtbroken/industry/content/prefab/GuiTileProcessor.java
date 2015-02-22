package com.builtbroken.industry.content.prefab;

import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by robert on 1/8/2015.
 */
public class GuiTileProcessor extends GuiContainerBase
{
    protected TileProcessor machine;

    public GuiTileProcessor(TileProcessor machine, EntityPlayer player)
    {
        super(new ContainerTileProcessor(machine, player));
        this.machine = machine;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_)
    {
        super.drawGuiContainerForegroundLayer(p_146979_1_, p_146979_2_);
        //String s = this.machine.hasCustomInventoryName() ? this.machine.getInventoryName() : I18n.format(this.machine.getInventoryName(), new Object[0]);
        //this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        drawString("Ticks: " + machine.getProcessorTicks(), 8, this.ySize - 35);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);

        //Draw slots
        this.drawSlot(inventorySlots.getSlot(0));
        this.drawSlot(inventorySlots.getSlot(1));

        //Draw progress bar, TODO fix
        if (this.machine.isEnabled())
        {
            int width = (int)(((float)this.machine.getProcessorTicks() / (float)this.machine.getMaxProcessingTicks()) *  24);
            this.drawTexturedModalRect(containerWidth + 79, containerHeight + 34, 176, 14, width + 1, 16);
        }
    }
}
