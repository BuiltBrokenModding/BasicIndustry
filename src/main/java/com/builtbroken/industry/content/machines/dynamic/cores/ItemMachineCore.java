package com.builtbroken.industry.content.machines.dynamic.cores;

import com.builtbroken.industry.BasicIndustry;
import com.builtbroken.industry.content.machines.dynamic.MachineModuleBuilder;
import com.builtbroken.mc.prefab.module.ItemAbstractModule;
import com.builtbroken.mc.prefab.module.ModuleBuilder;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/26/2016.
 */
public class ItemMachineCore extends ItemAbstractModule<MachineCore>
{
    public ItemMachineCore()
    {
        this.setMaxStackSize(5);
        this.setUnlocalizedName("machineCore");
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        if (stack.getItemDamage() >= 0 && stack.getItemDamage() < MachineCores.values().length)
        {
            return getUnlocalizedName() + "." + MachineCores.values()[stack.getItemDamage()].name;
        }
        return getUnlocalizedName();
    }

    @Override
    protected MachineCore newModule(ItemStack stack)
    {
        MachineCore core = null;
        if (stack.getItemDamage() >= 0 && stack.getItemDamage() < MachineCores.values().length)
        {
            switch (MachineCores.values()[stack.getItemDamage()])
            {
                case GRINDER:
                    core = new MachineCoreGrinder(stack);
                    break;
            }
        }
        return core;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for (MachineCores core : MachineCores.values())
        {
            ItemStack stack = new ItemStack(item, 1, core.ordinal());
            stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setString(ModuleBuilder.SAVE_ID, BasicIndustry.DOMAIN + ".module.machine.core." + core.name);
            list.add(stack);
        }
    }

    public enum MachineCores
    {
        GRINDER("grinder", MachineCoreGrinder.class);

        public final String name;
        public final Class<? extends MachineCore> clazz;

        MachineCores(String name, Class<? extends MachineCore> clazz)
        {
            this.name = name;
            this.clazz = clazz;
        }

        public static void register()
        {
            for (MachineCores core : values())
            {
                MachineModuleBuilder.INSTANCE.register(BasicIndustry.DOMAIN, "module.machine.core." + core.name, core.clazz);
            }
        }
    }
}
