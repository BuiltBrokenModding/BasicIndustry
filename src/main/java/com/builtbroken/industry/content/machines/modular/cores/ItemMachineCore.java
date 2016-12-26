package com.builtbroken.industry.content.machines.modular.cores;

import com.builtbroken.industry.content.machines.modular.MachineModuleBuilder;
import com.builtbroken.industry.content.machines.modular.modules.MachineModule;
import com.builtbroken.mc.prefab.module.ItemAbstractModule;
import net.minecraft.item.ItemStack;

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
        MachineModule module = MachineModuleBuilder.INSTANCE.build(stack);
        //TODO add NBT fix for EE
        if (module instanceof MachineCore)
        {
            return (MachineCore) module;
        }
        return null;
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
    }
}
