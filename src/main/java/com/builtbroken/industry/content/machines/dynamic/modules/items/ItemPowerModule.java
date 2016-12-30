package com.builtbroken.industry.content.machines.dynamic.modules.items;

import com.builtbroken.industry.BasicIndustry;
import com.builtbroken.industry.content.machines.dynamic.MachineModuleBuilder;
import com.builtbroken.industry.content.machines.dynamic.modules.builder.IModuleProvider;
import com.builtbroken.industry.content.machines.dynamic.modules.builder.ModuleProvider;
import com.builtbroken.industry.content.machines.dynamic.modules.power.PowerModule;
import com.builtbroken.industry.content.machines.dynamic.modules.power.PowerModuleBurner;
import com.builtbroken.industry.content.machines.dynamic.modules.power.PowerModuleRF;
import com.builtbroken.industry.content.machines.dynamic.modules.power.PowerModuleUE;
import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.builtbroken.mc.prefab.module.ItemAbstractModule;
import com.builtbroken.mc.prefab.module.ModuleBuilder;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/28/2016.
 */
public class ItemPowerModule extends ItemAbstractModule<PowerModule> implements IRecipeContainer
{
    public ItemPowerModule()
    {
        this.setMaxStackSize(5);
        this.setUnlocalizedName(BasicIndustry.PREFIX + "powerModule");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for (PowerModules mod : PowerModules.values())
        {
            ItemStack stack = new ItemStack(item, 1, mod.ordinal());
            stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setString(ModuleBuilder.SAVE_ID, BasicIndustry.DOMAIN + "." + mod.getSaveID());
            list.add(stack);
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        PowerModules type = getType(stack.getItemDamage());
        if (type != null)
        {
            return getUnlocalizedName() + "." + type.name().toLowerCase();
        }
        return getUnlocalizedName();
    }

    @Override
    protected PowerModule newModule(ItemStack stack)
    {
        PowerModules type = getType(stack.getItemDamage());
        if (type.builder != null)
        {
            return (PowerModule) type.builder.buildModule(stack);
        }
        return null;
    }

    public PowerModules getType(int meta)
    {
        if (meta > 0 && meta < PowerModules.values().length)
        {
            return PowerModules.values()[meta];
        }
        return PowerModules.BURNER;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        for (PowerModules module : PowerModules.values())
        {
            module.icon = reg.registerIcon(BasicIndustry.PREFIX + "module.power." + module.name);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta)
    {
        if (meta >= 0 && meta < PowerModules.values().length)
        {
            return PowerModules.values()[meta].icon;
        }
        return this.itemIcon;
    }

    @Override
    public void genRecipes(List<IRecipe> recipes)
    {
        //TODO recipes
    }

    /**
     * Enum of modules represented by this item's meta value.
     */
    public enum PowerModules
    {
        BURNER("burner", new ModuleProvider<PowerModuleBurner>(PowerModuleBurner.class)
        {
            @Override
            public PowerModuleBurner buildModule(ItemStack stack)
            {
                return new PowerModuleBurner(stack);
            }
        }),
        RF("rf", new ModuleProvider<PowerModuleRF>(PowerModuleRF.class)
        {
            @Override
            public PowerModuleRF buildModule(ItemStack stack)
            {
                return new PowerModuleRF(stack);
            }
        }),
        UE("ue", new ModuleProvider<PowerModuleUE>(PowerModuleUE.class)
        {
            @Override
            public PowerModuleUE buildModule(ItemStack stack)
            {
                return new PowerModuleUE(stack);
            }
        });

        /** Simplified name of the module. */
        public final String name;
        public IModuleProvider builder;

        /** Icon for the module */
        @SideOnly(Side.CLIENT)
        public IIcon icon;

        /** Cached module save ID. */
        private final String cachedSaveID;

        PowerModules(String name, IModuleProvider builder)
        {
            this.name = name;
            this.builder = builder;
            cachedSaveID = ".module.machine.power." + name;
        }

        public String getSaveID()
        {
            return cachedSaveID;
        }

        public static void register()
        {
            for (PowerModules module : values())
            {
                if (module.builder != null)
                {
                    MachineModuleBuilder.INSTANCE.register(BasicIndustry.DOMAIN, module.getSaveID(), module.builder.getModuleClass());
                }
            }
        }
    }
}
