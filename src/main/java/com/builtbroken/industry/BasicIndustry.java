package com.builtbroken.industry;

import com.builtbroken.industry.content.blocks.BlockIronMachineParts;
import com.builtbroken.industry.content.blocks.BlockLadderBI;
import com.builtbroken.industry.content.blocks.BlockScaffold;
import com.builtbroken.industry.content.machines.modular.TileDynamicMachine;
import com.builtbroken.industry.content.machines.modular.cores.ItemMachineCore;
import com.builtbroken.mc.lib.mod.AbstractMod;
import com.builtbroken.mc.lib.mod.ModCreativeTab;
import com.builtbroken.mc.prefab.tile.item.ItemBlockMetadata;
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
 * <p/>
 * Created by Dark(robert, DarkGuardsman) on 1/7/2015.
 */
@Mod(modid = BasicIndustry.DOMAIN, name = BasicIndustry.NAME, version = BasicIndustry.VERSION, dependencies = "required-after:VoltzEngine")
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

    @SidedProxy(clientSide = "com.builtbroken.industry.ClientProxy", serverSide = "com.builtbroken.industry.CommonProxy")
    public static CommonProxy proxy;

    //Machines
    public static Block blockDynamicMachine;

    //Blocks
    public static Block blockIronMachineParts;
    public static Block blockScaffold;
    public static Block blockLadderBI;

    //Items
    public static Item itemMachineCore;

    public BasicIndustry()
    {
        super(DOMAIN, "BasicIndustry");
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);

        //Creative tab
        manager.setTab(new ModCreativeTab(NAME));

        //Machines
        blockDynamicMachine = manager.newBlock("BIDynamicMachine", TileDynamicMachine.class);

        //Decoration blocks
        blockIronMachineParts = manager.newBlock("BIIronMachineParts", BlockIronMachineParts.class, ItemBlockMetadata.class);
        blockScaffold = manager.newBlock("ScaffoldBlock", BlockScaffold.class, ItemBlockMetadata.class);
        blockLadderBI = manager.newBlock("Ladder", BlockLadderBI.class, ItemBlockMetadata.class);

        //Items
        itemMachineCore = manager.newItem("machineCore", new ItemMachineCore());
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
