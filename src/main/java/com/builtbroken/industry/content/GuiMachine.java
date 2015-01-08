package com.builtbroken.industry.content;

import com.builtbroken.mc.prefab.entity.EntityProjectile;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by robert on 1/8/2015.
 */
public class GuiMachine extends GuiContainerBase
{
    private static final ResourceLocation furnaceGuiTextures = new ResourceLocation("textures/gui/container/furnace.png");
    protected TileMachine machine;

    public GuiMachine(TileMachine machine, EntityPlayer player)
    {
        super(new ContainerMachine(machine, player));
        this.machine = machine;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_)
    {
        String s = this.machine.hasCustomInventoryName() ? this.machine.getInventoryName() : I18n.format(this.machine.getInventoryName(), new Object[0]);
        this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(furnaceGuiTextures);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

        if (this.machine.isEnabled())
        {
            int width = (int)(((float)this.machine.getProcessorTicks() / (float)this.machine.getMaxProcessingTicks()) *  24);
            this.drawTexturedModalRect(k + 79, l + 34, 176, 14, width + 1, 16);
        }
    }
}
