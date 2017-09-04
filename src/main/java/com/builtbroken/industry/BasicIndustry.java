package com.builtbroken.industry;

import com.builtbroken.industry.content.cover.ItemMachineCover;
import com.builtbroken.industry.content.items.ItemParts;
import com.builtbroken.industry.content.machines.dynamic.TileDynamicMachine;
import com.builtbroken.industry.content.machines.dynamic.modules.furnace.TileFurnace;
import com.builtbroken.industry.content.machines.dynamic.modules.items.ItemControlModule;
import com.builtbroken.industry.content.machines.dynamic.modules.items.ItemInvModule;
import com.builtbroken.industry.content.machines.dynamic.modules.items.ItemMachineCore;
import com.builtbroken.industry.content.machines.dynamic.modules.items.ItemPowerModule;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.framework.mod.AbstractMod;
import com.builtbroken.mc.framework.mod.ModCreativeTab;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Simple industry mod with an interesting take on machines.
 * <p>
 * Created by Dark(robert, DarkGuardsman) on 1/7/2015.
 */
@Mod(modid = BasicIndustry.DOMAIN, name = BasicIndustry.NAME, version = BasicIndustry.VERSION, dependencies = "required-after:voltzengine")
public class BasicIndustry extends AbstractMod
{
    /** Name of the channel and mod ID. */
    public static final String NAME = "Basic_Industry";
    public static final String DOMAIN = "basicindustry";
    public static final String PREFIX = DOMAIN + ":";

    /** The version of ICBM. */
    public static final String MAJOR_VERSION = "@MAJOR@";
    public static final String MINOR_VERSION = "@MINOR@";
    public static final String REVISION_VERSION = "@REVIS@";
    public static final String BUILD_VERSION = "@BUILD@";
    public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVISION_VERSION + "." + BUILD_VERSION;

    @Mod.Instance(DOMAIN)
    public static BasicIndustry INSTANCE;

    @SidedProxy(clientSide = "com.builtbroken.industry.client.ClientProxy", serverSide = "com.builtbroken.industry.CommonProxy")
    public static CommonProxy proxy;

    //Machines
    public static Block blockDynamicMachine;
    public static Block blockBrickFurnace;

    //Items
    public static Item itemMachineCore;
    public static Item itemInventoryModules;
    public static Item itemPowerModules;
    public static Item itemControlModules;
    public static Item itemMachineCover;
    public static Item itemParts;

    //Configs
    public static boolean doInputSlotValidationChecks = true;


    public BasicIndustry()
    {
        super(DOMAIN, "BasicIndustry");
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
        Engine.requestOres();
        Engine.requestResources();
        Engine.requestSheetMetalContent();
        //Engine.requestMultiBlock(); TODO enable when needed
        Engine.requestSimpleTools();
        Engine.requestCircuits();
        Engine.requestCraftingParts();

        //Creative tab
        manager.setTab(new ModCreativeTab(NAME));

        //Machines
        blockDynamicMachine = manager.newBlock("BIDynamicMachine", TileDynamicMachine.class);
        blockBrickFurnace = manager.newBlock("BIBrickFurnace", TileFurnace.class);

        //Items
        itemMachineCore = manager.newItem("BIMachineCore", new ItemMachineCore());
        itemInventoryModules = manager.newItem("BIInventoryModule", new ItemInvModule());
        itemPowerModules = manager.newItem("BIPowerModule", new ItemPowerModule());
        itemControlModules = manager.newItem("BIControlModule", new ItemControlModule());

        itemMachineCover = manager.newItem("BIMachineCover", new ItemMachineCover());
        itemParts = manager.newItem("BIModuleParts", new ItemParts());

        //Register modules
        ItemMachineCore.MachineCores.register();
        ItemInvModule.InvModules.register();
        ItemPowerModule.PowerModules.register();
        ItemControlModule.ControlModules.register();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
        ((ModCreativeTab) manager.defaultTab).itemStack = new ItemStack(blockDynamicMachine);
        //Load mod dependent content
        //Register entities
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);

        //TODO do recipes
    }

    @Override
    public CommonProxy getProxy()
    {
        return proxy;
    }
}
