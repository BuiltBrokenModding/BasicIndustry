package com.builtbroken.industry.content.machines.dynamic.modules.items;

import com.builtbroken.industry.BasicIndustry;
import com.builtbroken.industry.content.items.ItemParts;
import com.builtbroken.industry.content.machines.dynamic.MachineModuleBuilder;
import com.builtbroken.industry.content.machines.dynamic.modules.inv.*;
import com.builtbroken.mc.core.content.tool.ItemSimpleCraftingTool;
import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.builtbroken.mc.lib.helper.recipe.OreNames;
import com.builtbroken.mc.prefab.module.ItemAbstractModule;
import com.builtbroken.mc.prefab.module.ModuleBuilder;
import com.builtbroken.mc.framework.recipe.item.sheetmetal.RecipeSheetMetal;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
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
public class ItemInvModule extends ItemAbstractModule<InventoryModule> implements IRecipeContainer
{
    public ItemInvModule()
    {
        this.setMaxStackSize(5);
        this.setUnlocalizedName(BasicIndustry.PREFIX + "inventoryModule");
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
            case DISPENSER:
                return new InventoryModuleDispenser(stack);
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

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        for (InvModules module : InvModules.values())
        {
            module.icon = reg.registerIcon(BasicIndustry.PREFIX + "module.inv." + module.name);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta)
    {
        if (meta >= 0 && meta < InvModules.values().length)
        {
            return InvModules.values()[meta].icon;
        }
        return this.itemIcon;
    }

    @Override
    public void genRecipes(List<IRecipe> recipes)
    {
        recipes.add(new RecipeSheetMetal(InvModules.SINGLE.toStack(8), "F F", "DFH",
                'F', ItemParts.Parts.FRAME_FACE.toStack(),
                'D', ItemSimpleCraftingTool.getDrill(),
                'H', ItemSimpleCraftingTool.getHammer()));

        recipes.add(new RecipeSheetMetal(InvModules.CHEST.toStack(2), "GCG", "DFH",
                'F', ItemParts.Parts.MODULE_FRAME.toStack(),
                'C', Blocks.chest,
                'G', OreNames.GEAR_IRON,
                'D', ItemSimpleCraftingTool.getDrill(),
                'H', ItemSimpleCraftingTool.getHammer()));

        recipes.add(new RecipeSheetMetal(InvModules.HOPPER.toStack(1), "GCG", "DFH",
                'F', ItemParts.Parts.MODULE_FRAME.toStack(),
                'C', Blocks.hopper,
                'G', OreNames.GEAR_IRON,
                'D', ItemSimpleCraftingTool.getDrill(),
                'H', ItemSimpleCraftingTool.getHammer()));

        recipes.add(new RecipeSheetMetal(InvModules.DISPENSER.toStack(1), "BDB", "DFH",
                'F', ItemParts.Parts.MODULE_FRAME.toStack(),
                'C', Blocks.dispenser,
                'B', ItemParts.Parts.GEAR_BOX.toStack(),
                'D', ItemSimpleCraftingTool.getDrill(),
                'H', ItemSimpleCraftingTool.getHammer()));
    }

    /**
     * Enum of modules represented by this item's meta value.
     */
    public enum InvModules
    {
        SINGLE("uno", InventoryModuleUno.class),
        CHEST("chest", InventoryModuleChest.class),
        HOPPER("hopper", InventoryModuleHopper.class),
        DISPENSER("dispenser", InventoryModuleDispenser.class);

        /** Simplified name of the module. */
        public final String name;
        /** Class of the module, used during registration. */
        public final Class<? extends InventoryModule> clazz;

        /** Icon for the module */
        @SideOnly(Side.CLIENT)
        public IIcon icon;

        /** Cached module save ID. */
        private final String cachedSaveID;

        InvModules(String name, Class<? extends InventoryModule> clazz)
        {
            this.name = name;
            cachedSaveID = ".module.machine.inv." + name;
            this.clazz = clazz;
        }

        public String getSaveID()
        {
            return cachedSaveID;
        }

        public ItemStack toStack(int n)
        {
            return new ItemStack(BasicIndustry.itemInventoryModules, n, ordinal());
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
