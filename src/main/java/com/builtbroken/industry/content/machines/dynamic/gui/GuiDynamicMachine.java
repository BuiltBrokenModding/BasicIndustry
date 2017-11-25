package com.builtbroken.industry.content.machines.dynamic.gui;

import com.builtbroken.industry.client.Assets;
import com.builtbroken.industry.content.machines.dynamic.TileDynamicMachine;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import com.builtbroken.mc.prefab.gui.buttons.GuiImageButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;

/**
 * Gui for the dynamic host
 * Created by robert on 1/8/2015.
 */
public class GuiDynamicMachine extends GuiContainerBase<TileDynamicMachine>
{
    private GuiImageButton craftingButton;
    private GuiImageButton moduleButton;
    private GuiImageButton helpButton;
    private GuiImageButton recipesButton;
    private GuiImageButton settingsButton;


    public final int guiType;

    public GuiDynamicMachine(TileDynamicMachine host, EntityPlayer player, int id)
    {
        super(new ContainerDynamicMachine(host, player, id), host);
        guiType = id;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        //Left hand button bar
        int y = guiTop + 5;
        craftingButton = addButton(GuiImageButton.newButton18(ContainerDynamicMachine.CRAFTING_GUI_ID, guiLeft - 18, y, 0, 0).setTexture(Assets.GUI_BUTTONS));
        y += 19;
        moduleButton = addButton(GuiImageButton.newButton18(ContainerDynamicMachine.MODULE_GUI_ID, guiLeft - 18, y, 6, 0).setTexture(Assets.GUI_BUTTONS));
        y += 19;
        settingsButton = addButton(GuiImageButton.newButton18(ContainerDynamicMachine.SETTINGS_GUI_ID, guiLeft - 18, y, 1, 0).setTexture(Assets.GUI_BUTTONS));
        y += 19;
        helpButton = addButton(GuiImageButton.newButton18(ContainerDynamicMachine.HELP_GUI_ID, guiLeft - 18, y, 2, 0).setTexture(Assets.GUI_BUTTONS));
        y += 19;
        recipesButton = addButton(GuiImageButton.newButton18(ContainerDynamicMachine.RECIPES_GUI_ID, guiLeft - 18, y, 2, 0).setTexture(Assets.GUI_BUTTONS));

        //Disables button for the page we currently are resting on
        switch (guiType)
        {
            case ContainerDynamicMachine.CRAFTING_GUI_ID:
                craftingButton.disable();
                break;
            case ContainerDynamicMachine.MODULE_GUI_ID:
                moduleButton.disable();
                break;
            case ContainerDynamicMachine.HELP_GUI_ID:
                helpButton.disable();
                break;
            case ContainerDynamicMachine.RECIPES_GUI_ID:
                recipesButton.disable();
                break;
            case ContainerDynamicMachine.SETTINGS_GUI_ID:
                settingsButton.disable();
                break;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        int id = button.id;
        if (id >= 0 && id <= 5)
        {
            Engine.packetHandler.sendToServer(new PacketTile(host, 2, id));
            return;
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_)
    {
        super.drawGuiContainerForegroundLayer(p_146979_1_, p_146979_2_);
        String s = this.host.hasCustomInventoryName() ? this.host.getInventoryName() : I18n.format(this.host.getInventoryName(), new Object[0]);
        this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        //TODO tool tips for buttons
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);

        //Draw slots
        drawContainerSlots();

        if (guiType == ContainerDynamicMachine.CRAFTING_GUI_ID)
        {
            //Draw progress bar, TODO fix
            if (host.getMachineCore() != null)
            {
                float percent = (float) host.getMachineCore().getProcessingTicks() / (float) host.getMachineCore().getRecipeProcessingTime();
                drawMicroBar(65, 17, 40, percent, Color.ORANGE);
            }
        }
    }
}
