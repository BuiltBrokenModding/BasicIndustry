package com.builtbroken.industry.content.machines.dynamic.modules.items;

import com.builtbroken.industry.BasicIndustry;
import com.builtbroken.mc.core.content.resources.items.ItemSheetMetal;
import com.builtbroken.mc.core.content.tool.ItemSheetMetalTools;
import com.builtbroken.mc.core.content.tool.ItemSimpleCraftingTool;
import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.builtbroken.mc.lib.helper.recipe.OreNames;
import com.builtbroken.mc.prefab.items.ItemAbstract;
import com.builtbroken.mc.prefab.recipe.item.sheetmetal.RecipeSheetMetal;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.IIcon;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/31/2016.
 */
public class ItemParts extends ItemAbstract implements IRecipeContainer
{
    public ItemParts()
    {
        this.setUnlocalizedName(BasicIndustry.PREFIX + "parts");
        this.setTextureName(BasicIndustry.PREFIX + "module");
    }

    @Override
    public void genRecipes(List<IRecipe> recipes)
    {
        recipes.add(newShapedRecipe(Parts.GEAR_BOX.toStack(),
                "GRG", "GPG", "GRG",
                'G', OreNames.GEAR_IRON,
                'P', OreNames.PLATE_IRON,
                'R', OreNames.ROD_IRON));
        recipes.add(new RecipeSheetMetal(Parts.FRAME_FACE.toStack(4),
                "BRB", "HPD", "BRB",
                'B', OreNames.SCREW_IRON,
                'P', OreNames.PLATE_IRON,
                'R', OreNames.ROD_IRON,
                'D', ItemSimpleCraftingTool.getDrill(),
                'H', ItemSimpleCraftingTool.getHammer()));
        recipes.add(new RecipeSheetMetal(Parts.MODULE_FRAME.toStack(),
                "FFF", "FHF", "FFF",
                'F', Parts.FRAME_FACE.toStack(),
                'H', ItemSimpleCraftingTool.getHammer()));
        recipes.add(new RecipeSheetMetal(Parts.ITEM_FEED.toStack(2),
                "P P", "RPR", "DHC",
                'P', ItemSheetMetal.SheetMetal.HALF.stack(),
                'R', OreNames.ROD_IRON,
                'C', ItemSheetMetalTools.getShears(),
                'D', ItemSimpleCraftingTool.getDrill(),
                'H', ItemSimpleCraftingTool.getHammer()));
        recipes.add(new RecipeSheetMetal(Parts.PISTON.toStack(),
                "GBG", "RPR", "DHC",
                'P', OreNames.PLATE_IRON,
                'B', Blocks.piston,
                'R', OreNames.ROD_IRON,
                'C', ItemSheetMetalTools.getShears(),
                'D', ItemSimpleCraftingTool.getDrill(),
                'H', ItemSimpleCraftingTool.getHammer()));
        recipes.add(new RecipeSheetMetal(Parts.MACHINE_FACE.toStack(2),
                "RCR", "HRD", "RIR",
                'R', OreNames.ROD_IRON,
                'I', OreNames.INGOT_IRON,
                'C', ItemSheetMetalTools.getShears(),
                'D', ItemSimpleCraftingTool.getDrill(),
                'H', ItemSimpleCraftingTool.getHammer()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for (Parts part : Parts.values())
        {
            list.add(new ItemStack(item, 1, part.ordinal()));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        if (stack.getItemDamage() >= 0 && stack.getItemDamage() < Parts.values().length)
        {
            return getUnlocalizedName() + "." + Parts.values()[stack.getItemDamage()].name;
        }
        return getUnlocalizedName();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        super.registerIcons(reg);
        for (Parts module : Parts.values())
        {
            module.icon = reg.registerIcon(BasicIndustry.PREFIX + "module.parts." + module.name);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta)
    {
        if (meta >= 0 && meta < Parts.values().length)
        {
            return Parts.values()[meta].icon;
        }
        return this.itemIcon;
    }

    public enum Parts
    {
        GEAR_BOX("gearBox"),
        FRAME_FACE("frame.face"),
        MODULE_FRAME("frame"),
        ITEM_FEED("itemFeed"),
        PISTON("piston"),
        MACHINE_FACE("machine.face");

        public final String name;

        @SideOnly(Side.CLIENT)
        public IIcon icon;

        Parts(String name)
        {
            this.name = name;
        }

        public ItemStack toStack(int n)
        {
            return new ItemStack(BasicIndustry.itemParts, n, ordinal());
        }

        public ItemStack toStack()
        {
            return new ItemStack(BasicIndustry.itemParts, 1, ordinal());
        }
    }
}
