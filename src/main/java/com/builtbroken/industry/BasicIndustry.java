package com.builtbroken.industry;

import com.builtbroken.industry.content.Content;
import com.builtbroken.industry.content.blocks.BlockIronMachineParts;
import com.builtbroken.industry.content.blocks.BlockScaffold;
import com.builtbroken.industry.content.machines.modular.TileDynamicMachine;
import com.builtbroken.industry.content.machines.tests.TileFurnace;
import com.builtbroken.industry.content.machines.tests.TileOreCrusher;
import com.builtbroken.industry.content.machines.tests.TileOreGrinder;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.lib.mod.AbstractMod;
import com.builtbroken.mc.lib.mod.ModCreativeTab;
import com.builtbroken.mc.prefab.tile.item.ItemBlockMetadata;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
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
        ((ModCreativeTab) manager.defaultTab).itemStack = new ItemStack(Content.testFurnace);

        //Register content
        if (Engine.runningAsDev)
        {
            Content.testFurnace = manager.newBlock(TileFurnace.class);
            Content.testCrusher = manager.newBlock(TileOreCrusher.class);
            Content.testGrinder = manager.newBlock(TileOreGrinder.class);
        }
        Content.testGrinder = manager.newBlock("BIDynamicMachine", TileDynamicMachine.class);
        Content.blockIronMachineParts = manager.newBlock("BIIronMachineParts", BlockIronMachineParts.class, ItemBlockMetadata.class);
        Content.blockScaffold = manager.newBlock("ScaffoldBlock", BlockScaffold.class, ItemBlockMetadata.class);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        super.init(event);

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
