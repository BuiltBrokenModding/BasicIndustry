package com.builtbroken.industry.content.machines.dynamic.modules.inv;

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
 * Created by Dark(DarkGuardsman, Robert) on 12/28/2016.
 */
public class ItemInvModule extends ItemAbstractModule<InventoryModule>
{
    public ItemInvModule()
    {
        this.setMaxStackSize(5);
        this.setUnlocalizedName("inventoryModule");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for (InvModules mod : InvModules.values())
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
        InvModules type = getType(stack.getItemDamage());
        if (type != null)
        {
            return getUnlocalizedName() + "." + type.name().toLowerCase();
        }
        return getUnlocalizedName();
    }

    @Override
    protected InventoryModule newModule(ItemStack stack)
    {
        switch (getType(stack.getItemDamage()))
        {
            case SINGLE:
                return new InventoryModuleUno(stack);
            case CHEST:
                return new InventoryModuleChest(stack);
            case HOPPER:
                return new InventoryModuleHopper(stack);
        }
        return null;
    }

    public InvModules getType(int meta)
    {
        if (meta > 0 && meta < InvModules.values().length)
        {
            return InvModules.values()[meta];
        }
        return InvModules.SINGLE;
    }

    public enum InvModules
    {
        SINGLE("uno", InventoryModuleUno.class),
        CHEST("chest", InventoryModuleChest.class),
        HOPPER("hopper", InventoryModuleHopper.class);

        private final String cachedSaveID;
        public final Class<? extends InventoryModule> clazz;

        InvModules(String name, Class<? extends InventoryModule> clazz)
        {
            cachedSaveID = ".module.machine.inv." + name;
            this.clazz = clazz;
        }

        public String getSaveID()
        {
            return cachedSaveID;
        }

        public static void register()
        {
            for (InvModules module : values())
            {
                MachineModuleBuilder.INSTANCE.register(BasicIndustry.DOMAIN, module.getSaveID(), module.clazz);
            }
        }
    }
}
