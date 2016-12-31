package com.builtbroken.industry.client;

import com.builtbroken.industry.BasicIndustry;
import com.builtbroken.industry.CommonProxy;
import com.builtbroken.industry.content.cover.ItemRendererMachineCover;
import com.builtbroken.industry.content.machines.dynamic.modules.ISBRMachine;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.MinecraftForgeClient;

/**
 * Created by robert on 1/7/2015.
 */
public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit()
    {
        super.preInit();
        RenderingRegistry.registerBlockHandler(new ISBRMachine());
    }

    @Override
    public void init()
    {
        super.init();
        MinecraftForgeClient.registerItemRenderer(BasicIndustry.itemMachineCover, new ItemRendererMachineCover());
    }
}
