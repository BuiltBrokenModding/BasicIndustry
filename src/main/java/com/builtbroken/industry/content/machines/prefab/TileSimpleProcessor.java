package com.builtbroken.industry.content.machines.prefab;

import com.builtbroken.jlib.data.Colors;
import com.builtbroken.mc.api.recipe.MachineRecipeType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

/**
 * Created by robert on 2/22/2015.
 */
@Deprecated
public class TileSimpleProcessor extends TileProcessor
{
    public int spinUpTime;
    public final MachineRecipeType type;

    public TileSimpleProcessor(String name, MachineRecipeType type, int slots)
    {
        super(Material.iron, slots);
        this.type = type;
        this.name = name;
        this.max_processing_ticks = 400;
    }

    @Override
    protected ItemStack getRecipe()
    {
        return getStackInSlot(0) != null ? type.getItemStackRecipe(0, 0, getStackInSlot(0)) : null;
    }

    @Override
    public void update()
    {
        super.update();
        if (isProcessing)
        {
            ++spinUpTime;
        }
        else if (spinUpTime > 0)
        {
            spinUpTime--;
        }
    }

    @Override
    protected boolean isWorking()
    {
        return spinUpTime > 0;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side)
    {
        return slot == 0 && type.getItemStackRecipe(0, 0, stack) != null;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side)
    {
        return slot == 1;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {

    }

    @Override
    public IIcon getIcon(int side)
    {
        if (isEnabled())
        {
            return Blocks.lit_furnace.getIcon(side, getMetadata());
        }
        return Blocks.furnace.getIcon(side, getMetadata());
    }

    @SideOnly(Side.CLIENT)
    public int getColorMultiplier()
    {
        return Colors.YELLOW.toInt();
    }
}
