package com.builtbroken.industry.content;

import com.builtbroken.mc.prefab.gui.GuiContainerBase;

/**
 * Created by robert on 1/8/2015.
 */
public class GuiMachine extends GuiContainerBase
{
    protected TileMachine machine;

    public GuiMachine(TileMachine machine)
    {
        this.machine = machine;
    }
}
