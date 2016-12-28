package com.builtbroken.industry.content.machines.dynamic.gui;

import com.builtbroken.industry.client.Assets;
import com.builtbroken.industry.content.machines.dynamic.TileDynamicMachine;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import com.builtbroken.mc.prefab.gui.buttons.GuiImageButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

/**
 * Gui for the dynamic machine
 * Created by robert on 1/8/2015.
 */
public class GuiDynamicMachine extends GuiContainerBase
{
    protected TileDynamicMachine machine;

    private GuiImageButton powerButton;
    private GuiImageButton moduleButton;
    private GuiImageButton helpButton;
    private GuiImageButton recipesButton;
    private GuiImageButton settingsButton;

    public static final int POWER_BUTTON_ID = 1;
    public static final int MODULE_BUTTON_ID = 2;
    public static final int HELP_BUTTON_ID = 3;
    public static final int RECIPES_BUTTON_ID = 4;
    public static final int SETTINGS_BUTTON_ID = 5;

    public GuiDynamicMachine(TileDynamicMachine machine, EntityPlayer player, int id)
    {
        super(new ContainerDynamicMachine(machine, player, id));
        this.machine = machine;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        powerButton = addButton(GuiImageButton.newButton18(POWER_BUTTON_ID, guiLeft - 18, guiTop + 5, 0, 0).setTexture(Assets.GUI_BUTTONS));
        moduleButton = addButton(GuiImageButton.newButton18(MODULE_BUTTON_ID, guiLeft - 18, guiTop + 5 + 19, 6, 0).setTexture(Assets.GUI_BUTTONS));
        settingsButton = addButton(GuiImageButton.newButton18(SETTINGS_BUTTON_ID, guiLeft - 18, guiTop + 5 + 19 * 2, 1, 0).setTexture(Assets.GUI_BUTTONS));

        helpButton = addButton(GuiImageButton.newButton18(HELP_BUTTON_ID, guiLeft - 18, guiTop + 30, 2, 0).setTexture(Assets.GUI_BUTTONS));
        recipesButton = addButton(GuiImageButton.newButton18(RECIPES_BUTTON_ID, guiLeft - 18, guiTop + 30 + 19, 2, 0).setTexture(Assets.GUI_BUTTONS));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_)
    {
        super.drawGuiContainerForegroundLayer(p_146979_1_, p_146979_2_);
        String s = this.machine.hasCustomInventoryName() ? this.machine.getInventoryName() : I18n.format(this.machine.getInventoryName(), new Object[0]);
        this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        //TODO tool tips for buttons
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);

        //Draw slots
        for (Object object : inventorySlots.inventorySlots)
        {
            drawSlot((Slot) object);
        }

        //Draw progress bar, TODO fix
        if (machine.getMachineCore() != null)
        {
            int width = (int) (((float) machine.getMachineCore().getProcessingTicks() / (float) machine.getMachineCore().getMaxProcessingTicks()) * 24);
            this.drawTexturedModalRect(containerWidth + 79, containerHeight + 34, 176, 14, width + 1, 16);
        }
    }
}
