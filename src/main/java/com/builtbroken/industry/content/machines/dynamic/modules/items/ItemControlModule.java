package com.builtbroken.industry.content.machines.dynamic.modules.items;

import com.builtbroken.industry.BasicIndustry;
import com.builtbroken.industry.content.machines.dynamic.MachineModuleBuilder;
import com.builtbroken.industry.content.machines.dynamic.modules.controllers.ControlModule;
import com.builtbroken.industry.content.machines.dynamic.modules.controllers.ControlModuleRedstone;
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
public class ItemControlModule extends ItemAbstractModule<ControlModule> implements IRecipeContainer
{
    public ItemControlModule()
    {
        this.setMaxStackSize(5);
        this.setUnlocalizedName(BasicIndustry.PREFIX + "controlModule");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for (ControlModules mod : ControlModules.values())
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
        ControlModules type = getType(stack.getItemDamage());
        if (type != null)
        {
            return getUnlocalizedName() + "." + type.name().toLowerCase();
        }
        return getUnlocalizedName();
    }

    @Override
    protected ControlModule newModule(ItemStack stack)
    {
        switch (getType(stack.getItemDamage()))
        {

        }
        return null;
    }

    public ControlModules getType(int meta)
    {
        if (meta > 0 && meta < ControlModules.values().length)
        {
            return ControlModules.values()[meta];
        }
        return ControlModules.REDSTONE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        for (ControlModules module : ControlModules.values())
        {
            module.icon = reg.registerIcon(BasicIndustry.PREFIX + "module.controller." + module.name);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta)
    {
        if (meta >= 0 && meta < ControlModules.values().length)
        {
            return ControlModules.values()[meta].icon;
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
    public enum ControlModules
    {
        REDSTONE("redstone", ControlModuleRedstone.class);

        /** Simplified name of the module. */
        public final String name;
        /** Class of the module, used during registration. */
        public final Class<? extends ControlModule> clazz;

        /** Icon for the module */
        @SideOnly(Side.CLIENT)
        public IIcon icon;

        /** Cached module save ID. */
        private final String cachedSaveID;

        ControlModules(String name, Class<? extends ControlModule> clazz)
        {
            this.name = name;
            cachedSaveID = ".module.machine.controller." + name;
            this.clazz = clazz;
        }

        public String getSaveID()
        {
            return cachedSaveID;
        }

        public static void register()
        {
            for (ControlModules module : values())
            {
                MachineModuleBuilder.INSTANCE.register(BasicIndustry.DOMAIN, module.getSaveID(), module.clazz);
            }
        }
    }
}
