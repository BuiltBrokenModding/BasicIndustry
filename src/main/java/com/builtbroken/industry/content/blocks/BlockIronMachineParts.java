package com.builtbroken.industry.content.blocks;

import com.builtbroken.industry.BasicIndustry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/8/2016.
 */
public class BlockIronMachineParts extends Block
{
    public BlockIronMachineParts()
    {
        super(Material.iron);
        this.setBlockName(BasicIndustry.PREFIX + "ironMachinePart");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg)
    {
        for (IronMachineBlocks block : IronMachineBlocks.values())
        {
            block.icon = reg.registerIcon(BasicIndustry.PREFIX + block.textureName);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        if (meta >= 0 && meta < IronMachineBlocks.values().length)
        {
            return IronMachineBlocks.values()[meta].icon;
        }
        return Blocks.iron_bars.getIcon(side, meta);
    }

    public enum IronMachineBlocks
    {
        MACHINE_CASING("casing_iron");

        protected IIcon icon;
        protected final String textureName;

        IronMachineBlocks(String name)
        {
            this.textureName = name;
        }
    }
}
